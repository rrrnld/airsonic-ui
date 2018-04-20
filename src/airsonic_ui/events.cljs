(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.db :as db]
            [airsonic-ui.api :as api]))

;; this is called with user and password to try and see if the credentials are
;; correct; if yes, ::auth-success will be fired

(re-frame/reg-event-fx
 ::authenticate
 (fn [{:keys [db]} [_ user pass]]
   {:db (update db :active-requests inc)
    :http-xhrio {:method :get
                 :uri (api/url "ping" {:u user :p pass})
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
      :dispatch [::logged-in]})))

;; we do this in two steps to make sure the credentials are set once we navigate
(re-frame/reg-event-fx
 ::logged-in
 (fn [_ _]
   {::routes/navigate [::routes/main]}))

;; TODO: Test that credentials are actually taken
;; TODO: Move these in the future? events.cljs should just do wiring. We could
;; implement api.cljs as a completely independent module.

(re-frame/reg-event-fx
 :api-request
 (fn [{:keys [db]} [_ endpoint k params]]
   {:http-xhrio {:method :get
                 :uri (api/url endpoint (merge params (:login db)))
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

;; musique

(re-frame/reg-event-fx
 ::play-song
 (fn [{:keys [db]} [_ song]]
   (let [song-url (api/url "stream" (merge {:id (:id song)}
                                           (:login db)))]
     (println "Requesting to stream song at" song-url)
     {:play-song song-url})))

;; routing

(re-frame/reg-event-fx
 ::routes/navigation
 (fn [{:keys [db]} [_ route params query]]
   ;; all the naviagation logic is in routes.cljs; all we need to do here
   ;; is say what actually happens once we've navigated succesfully
   (println "routes/route-data" (routes/data-for route params query))
   {:db (assoc db :current-route [route params query])
    :dispatch (routes/data-for route params query)}))

(re-frame/reg-event-fx
 ::routes/unauthorized
 (fn [fx _]
   ;; log out on 403
   {::routes/navigate [routes/default-route]
    ::routes/unset-credentials nil
    :db db/default-db}))

;; database reset / init

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
