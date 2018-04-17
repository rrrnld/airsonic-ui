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

(re-frame/reg-event-fx
 ::authenticate
 (fn [{:keys [db]} [_ {:keys [user pass]}]]
   {:db (update db :active-requests inc)
    :http-xhrio {:method :get
                 :uri (api-url "ping" {:u user :p pass})
                 :response-format (ajax/text-response-format)
                 :on-success [::auth-successful user pass]
                 :on-failure [::auth-gone-bad]}}))

(re-frame/reg-event-db
 ::auth-successful
 (fn [db [_ user pass]]
   (-> (update db :active-requests dec)
       (assoc :login {:u user
                      :p pass}))))

(re-frame/reg-event-db
 ::auth-gone-bad
 (fn [db event]
   (println "auth gone bad" event)))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
