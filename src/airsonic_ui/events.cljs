(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.config :as config]
            [airsonic-ui.db :as db]
            [clojure.string :as string]))

(defn ^:private uri-escape [s]
  (js/encodeURIComponent s))

(defn api-url [endpoint params]
  (let [query (->> (assoc params
                          :f "json"
                          :c "airsonic-ui-cljs"
                          :v "1.15.0")
                   (map (fn [[k v]]
                          (str (uri-escape (name k)) "=" (uri-escape v))))
                   (string/join "&"))]
    (str config/server "/rest/" endpoint "?" query)))

(defn api-error?
  "We need to look at the message body because the subsonic api always responds
  with status 200"
  [response]
  (= "failed" (-> response :subsonic-response :status)))

(defn error-message
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
                 :on-success [::auth-successful user pass]
                 :on-failure [::api-failure]}}))

(re-frame/reg-event-db
 ::auth-successful
 (fn [db [_ user pass response]]
   ;; TODO: Handle failures differently
   (-> (update db :active-requests dec)
       (assoc :login {:u user
                      :p pass}))))

(re-frame/reg-event-db
 ::api-failure
 (fn [db event]
   (println "api call gone bad; CORS headers missing? check for :status 0" event)
   db))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
