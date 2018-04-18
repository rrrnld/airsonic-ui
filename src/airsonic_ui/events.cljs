(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [bide.core :as r]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.config :as config]
            [airsonic-ui.db :as db]
            [clojure.string :as string]))


;; TODO: Remove impurities

;; api related functions

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
   ;; TODO: Refactor navigation into effect
   (r/navigate! routes/router ::routes/main)
   (-> (update db :active-requests dec)
       (assoc :login {:u user
                      :p pass}))))

(re-frame/reg-event-db
 ::api-failure
 (fn [db event]
   (println "api call gone bad; CORS headers missing? check for :status 0" event)
   db))

;; app interface

(defn authed?
  "Predicate to determine whether we can access a specific route."
  [route credentials]
  (or (not (routes/protected route)) credentials))

(re-frame/reg-event-db
 ::navigate
 (fn [db [_ route]]
   (println "authed?" route (authed? route (:login db)))
   (if (authed? route (:login db)) 
     ;; continue to correct page
     ;; TODO: Fetch data based on route
     (assoc db :route route)
     ;; logout and redirect to login
     (do (re-frame/dispatch [::initialize-db])
         (r/navigate! routes/router routes/default)
         db))))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
