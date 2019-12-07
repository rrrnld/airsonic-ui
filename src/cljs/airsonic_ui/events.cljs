(ns airsonic-ui.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.db :as db]
            [airsonic-ui.api.helpers :as api]))

(rf/reg-fx
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

(rf/reg-event-fx
 ::initialize-app
 [(rf/inject-cofx :store)]
 initialize-app)

(defn verify-credentials
  "Initializes the whole authentication chain when we have locally stored
  credentials that look plausible."
  [_ [_ credentials]]
  ;; TODO: spec this
  (if (every? string? ((juxt :u :p :server) credentials))
    {:dispatch [:credentials/send-authentication-request credentials]}))

(rf/reg-event-fx :credentials/verify verify-credentials)

;; ---
;; auth logic
;; ---

(defn user-login
  "Gets called after the user clicked on the login button"
  [{:keys [db]} [_ user pass server]]
  (let [credentials {:u user, :p pass, :server server, :verified? false}]
    {:db (assoc db :credentials credentials)
     :dispatch [:credentials/send-authentication-request credentials]}))

(rf/reg-event-fx :credentials/user-login user-login)

(defn authentication-request
  "Tries to authenticate a user by requesting info about the given user, saving
  the credentials when the request was successful."
  [cofx [_ credentials]]
  {:http-xhrio {:method :get
                :uri (api/url credentials "getUser" {:username (:u credentials)})
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [:credentials/authentication-response credentials]
                :on-failure [:api.response/failed]}}) ; <- we don't need endpoint and params here because the response is not cached

(rf/reg-event-fx :credentials/send-authentication-request authentication-request)

(defn authentication-response
  "Since we don't get real status codes, we have to look into the server's
  response and see whether we actually sent the correct credentials"
  [_ [_ credentials response]]
  {:dispatch (if (api/is-error? response)
               [:credentials/authentication-failure response]
               [:credentials/authentication-success credentials response])})

(rf/reg-event-fx :credentials/authentication-response authentication-response)

(defn authentication-failure
  "Removes all stored credentials and displays potential api errors to the user"
  [{:keys [db store]} [_ response]]
  {:dispatch [:notification/show :error (api/error-msg (api/->exception response))]
   :store (dissoc store :credentials)
   :db (dissoc db :credentials)})

(rf/reg-event-fx :credentials/authentication-failure authentication-failure)

(defn authentication-success
  "Gets called after the server indicates that the credentials entered by a user
  are correct (see `credentials-verification-request`)"
  [{:keys [db]} [_ credentials auth-response]]
  {:store {:credentials credentials}
   :db (-> (assoc db :credentials (assoc credentials :verified? true))
           (assoc :user (api/unwrap-response auth-response)))
   :dispatch [::logged-in]})

(rf/reg-event-fx :credentials/authentication-success authentication-success)

(defn logged-in
  [cofx _]
  (let [redirect (or (get-in cofx [:routes/from-query-param :redirect])
                     [::routes/library])]
    {:dispatch [:routes/do-navigation redirect]}))

(rf/reg-event-fx
 ::logged-in
 [(rf/inject-cofx :routes/from-query-param :redirect)]
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

(rf/reg-event-fx ::logout logout)

;; ---
;; routing
;; ---

(rf/reg-event-fx
 :routes/did-navigate
 (fn [{:keys [db]} [_ route params query]]
   {:db (assoc db :routes/current-route [route params query])
    :dispatch-n (routes/route-events route params query)}))

(rf/reg-event-fx
 :routes/unauthorized
 [(rf/inject-cofx :routes/current-route)]
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
  [{:keys [db]} [_ level message]]
  (let [id (.now js/performance)
        ;; the level argument is optional; if it's not given, it defaults to :info
        level' (if (nil? message) :info level)
        message' (if (nil? message) level message)]
    {:db (assoc-in db [:notifications id] {:level level'
                                           :message message'})
     :dispatch-later [{:ms (get notification-duration level)
                       :dispatch [:notification/hide id]}]}))

(rf/reg-event-fx :notification/show show-notification)

(defn hide-notification
  [db [_ notification-id]]
  (update db :notifications dissoc notification-id))

(rf/reg-event-db :notification/hide hide-notification)
