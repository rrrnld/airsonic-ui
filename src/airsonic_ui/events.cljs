(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.config :as config]
            [airsonic-ui.db :as db]
            [clojure.string :as string]))

;; TODO:

;; api related functions

(defn ^:private uri-escape [s]
  (js/encodeURIComponent s))

(defn ^:private api-url [endpoint params]
  (let [query (->> (assoc params
                          :f "json"
                          :c "airsonic-ui-cljs"
                          :v "1.15.0")
                   (map (fn [[k v]]
                          (str (uri-escape (name k)) "=" (uri-escape v))))
                   (string/join "&"))]
    (str config/server "/rest/" endpoint "?" query)))

(defn ^:private api-error?
  "We need to look at the message body because the subsonic api always responds
  with status 200"
  [response]
  (= "failed" (-> response :subsonic-response :status)))

(defn ^:private error-message
  [response]
  (let [{:keys [code message]} (-> response :subsonic-response :error)]
    (str "Code " code ": " message)))

(re-frame/reg-event-fx
 ::authenticate
 (fn [{:keys [db]} [_ user pass]]
   {:db (update db :active-requests inc)
    :http-xhrio {:method :get
                 :uri (api-url "ping" {:u user :p pass})
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::auth-success user pass]
                 :on-failure [::api-failure]}}))

;; TODO: Test that credentials are associated

(re-frame/reg-event-fx
 ::auth-success
 (fn [{:keys [db]} [_ user pass response]]
   ;; TODO: Handle failures differently
   (let [login {:u user :p pass}]
     {::routes/set-credentials login
      :db (-> (update db :active-requests #(max (dec %) 0))
              (assoc :login login))
      :dispatch-n (list [::start-page]
                        [::api-request "getAlbumList2" :albumList2 {:type "recent"}])})))

;; we do this in two steps to make sure the credentials are set once we navigate
(re-frame/reg-event-fx
 ::start-page
 (fn [_ _]
   {::routes/navigate [::routes/main]}))

;; TODO: Test that credentials are actually taken

(re-frame/reg-event-fx
 ::api-request
 (fn [{:keys [db]} [_ endpoint k params]]
   {:http-xhrio {:method :get
                 :uri (api-url endpoint (merge params (:login db)))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::api-success k]
                 :on-failure [::api-failure]}}))

(re-frame/reg-event-db
 ::api-success
 (fn [db [_ k response]]
   (println "api response" response)
   ;; we "unwrap" the responses
   (assoc db :response (-> response :subsonic-response k))))

(re-frame/reg-event-db
 ::api-failure
 (fn [db event]
   (println "api call gone bad; CORS headers missing? check for :status 0" event)
   db))

;; routing

(re-frame/reg-event-db
 ::routes/navigation
 (fn [db [_ route params query]]
   ;; all the naviagation logic is in routes.cljs; all we need to do here
   ;; is say what actually happens once we've navigated succesfully
   (assoc db :current-route [route params query])))

(re-frame/reg-event-fx
 ::routes/unauthorized
 (fn [fx _]
   ;; log out on 403
   {:navigate [routes/default-route]
    ::routes/unset-credentials nil
    :db db/default-db}))

;; database reset / init

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
