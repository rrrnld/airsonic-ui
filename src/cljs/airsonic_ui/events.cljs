(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.db :as db]
            [airsonic-ui.api.helpers :as api]))

(re-frame/reg-fx
 ;; a simple effect to keep println statements out of our event handlers
 :log
 (fn [params]
   (apply println params)))

(defn noop
  "An event handler that can be used for clarity; doesn't do anything, but might
  give a name to an event"
  [cofx _] cofx)

;; ---
;; app boot flow
;; * restoring a previous session
;; * initializing the router
;; * sending out the appropriate requests
;; ---

(defn initialize-app
  [{{:keys [credentials]} :store} _]
  (let [effects {:db db/default-db
                 :routes/start-routing nil}]
    (if (not (empty? credentials))
      (assoc effects :dispatch [:credentials/verify credentials])
      effects)))

(re-frame/reg-event-fx
 ::initialize-app
 [(re-frame/inject-cofx :store)]
 initialize-app)

(defn verify-credentials
  "Initializes the whole authentication chain when we have locally stored
  credentials that look plausible."
  [_ [_ credentials]]
  ;; TODO: spec this
  (if (every? string? ((juxt :u :p :server) credentials))
    {:dispatch [:credentials/send-authentication-request credentials]}))

(re-frame/reg-event-fx :credentials/verify verify-credentials)

;; ---
;; auth logic
;; ---

(defn user-login
  "Gets called after the user clicked on the login button"
  [cofx [_ user pass server]]
  (let [credentials {:u user, :p pass, :server server, :verified? false}]
    (-> (assoc-in cofx [:db :credentials] credentials)
        (assoc :dispatch [:credentials/send-authentication-request credentials]))))

(re-frame/reg-event-fx :credentials/user-login user-login)

(defn authentication-request
  "Tries to authenticate a user by requesting info about the given user, saving
  the credentials when the request was successful."
  [cofx [_ credentials]]
  (assoc cofx :http-xhrio {:method :get
                           :uri (api/url credentials "getUser" {:username (:u credentials)})
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [:credentials/authentication-response credentials]
                           :on-failure [:api/failed-response]})) ; <- we don't need endpoint and params here because the response is not cached

(re-frame/reg-event-fx :credentials/send-authentication-request authentication-request)

(defn authentication-response
  "Since we don't get real status codes, we have to look into the server's
  response and see whether we actually sent the correct credentials"
  [fx [_ credentials response]]
  (assoc fx :dispatch (if (api/is-error? response)
                        [:credentials/authentication-failure response]
                        [:credentials/authentication-success credentials response])))

(re-frame/reg-event-fx :credentials/authentication-response authentication-response)

(defn authentication-failure
  "Removes all stored credentials and displays potential api errors to the user"
  [fx [_ response]]
  (-> (assoc fx :dispatch [:notification/show :error (api/error-msg (api/->exception response))])
      (update :store dissoc :credentials)
      (update :db dissoc :credentials)))

(re-frame/reg-event-fx :credentials/authentication-failure authentication-failure)

(defn authentication-success
  "Gets called after the server indicates that the credentials entered by a user
  are correct (see `credentials-verification-request`)"
  [{:keys [db]} [_ credentials auth-response]]
  {:store {:credentials credentials}
   :db (-> (assoc db :credentials (assoc credentials :verified? true))
           (assoc :user (api/unwrap-response auth-response)))
   :dispatch [::logged-in]})

(re-frame/reg-event-fx :credentials/authentication-success authentication-success)

(defn logged-in
  [cofx _]
  (let [redirect (or (get-in cofx [:routes/from-query-param :redirect])
                     [::routes/library])]
    {:dispatch [:routes/do-navigation redirect]}))

(re-frame/reg-event-fx
 ::logged-in
 [(re-frame/inject-cofx :routes/from-query-param :redirect)]
 logged-in)

(defn logout
  "Clears all credentials and redirects the user to the login page"
  [cofx [_ & args]]
  (let [args (apply hash-map args)]
    {:dispatch [:routes/do-navigation (if-let [redirect (:redirect-to args)]
                                        [::routes/login {} {:redirect (routes/encode-route redirect)}]
                                        [::routes/login])]
     :store nil
     :db db/default-db
     :audio/stop nil}))

(re-frame/reg-event-fx ::logout logout)

;; ---
;; routing
;; ---

(re-frame/reg-event-fx
 :routes/did-navigate
 (fn [{:keys [db]} [_ route params query]]
   {:db (assoc db :routes/current-route [route params query])
    :dispatch-n (routes/route-events route params query)}))

(re-frame/reg-event-fx
 :routes/unauthorized
 [(re-frame/inject-cofx :routes/current-route)]
 (fn [{:routes/keys [current-route]} _]
   {:dispatch [::logout :redirect-to current-route]}))

;; ---
;; user messages
;; ---

(def notification-duration
  {:info 2500
   :error 10000})

(defn show-notification
  "Displays an informative message to the user"
  [fx [_ level message]]
  (let [id (.now js/performance)
        hide-later (fn [level]
                     [{:ms (get notification-duration level)
                       :dispatch [:notification/hide id]}])]
    (if (nil? message)
      (let [message level
            level :info]
        (-> (assoc-in fx [:db :notifications id] {:level level
                                                  :message message})
            (assoc :dispatch-later (hide-later level))))
      (-> (assoc-in fx [:db :notifications id] {:level level
                                                :message message})
          (assoc :dispatch-later (hide-later level))))))

(re-frame/reg-event-fx :notification/show show-notification)

(defn hide-notification
  [db [_ notification-id]]
  (update db :notifications dissoc notification-id))

(re-frame/reg-event-db :notification/hide hide-notification)
