(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.db :as db]
            [airsonic-ui.utils.api :as api]
            [airsonic-ui.audio.playlist :as playlist]))

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
  "Tries to authenticate a user by pinging the server with credentials, saving
  them when the request was successful. Bypasses the request when a user saved
  their credentials."
  [cofx [_ credentials]]
  (assoc cofx :http-xhrio {:method :get
                           :uri (api/url (:server credentials) "ping" (select-keys credentials [:u :p]))
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [:credentials/authentication-response credentials]
                           :on-failure [:api/bad-response]}))

(re-frame/reg-event-fx :credentials/send-authentication-request authentication-request)

(defn authentication-response
  "Since we don't get real status codes, we have to look into the server's
  response and see whether we actually sent the correct credentials"
  [fx [_ credentials response]]
  (assoc fx :dispatch (if (api/is-error? response)
                        [:credentials/authentication-failure response]
                        [:credentials/authentication-success (assoc credentials :verified? true)])))

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
  [{:keys [db]} [_ credentials]]
  {:store {:credentials credentials}
   :db (assoc db :credentials (assoc credentials :verified? true))
   :dispatch [::logged-in]})

(re-frame/reg-event-fx :credentials/authentication-success authentication-success)

;; TODO: We have to find another solution for this once we have routes that
;; don't require a login but have the bottom controls

(re-frame/reg-fx
 :show-nav-bar
 (fn [_]
   (.. js/document -documentElement -classList (add "has-navbar-fixed-bottom"))))

(defn logged-in
  [cofx _]
  (let [redirect (or (get-in cofx [:routes/from-query-param :redirect])
                     [::routes/main])]
    {:dispatch [:routes/do-navigation redirect]
     :show-nav-bar nil}))

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
;; api interaction
;; ---

(defn- api-url [db endpoint params]
  (let [creds (:credentials db)]
    (api/url (:server creds) endpoint (merge params (select-keys creds [:u :p])))))

(defn api-request [{:keys [db]} [_ endpoint params]]
  {:http-xhrio {:method :get
                :uri (api-url db endpoint params)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [:api/good-response]
                :on-failure [:api/bad-response]}})

(re-frame/reg-event-fx :api/request api-request)

(defn good-api-response [fx [_ response]]
  (try
    (assoc-in fx [:db :response] (api/unwrap-response response))
    (catch ExceptionInfo e
      {:dispatch [:notification/show :error (api/error-msg e)]})))

(re-frame/reg-event-fx :api/good-response good-api-response)

(defn bad-api-response [db event]
  {:log ["API call gone bad; are CORS headers missing? check for :status 0" event]
   :dispatch [:notification/show :error "Communication with server failed. Check browser logs for details."]})

(re-frame/reg-event-fx :api/bad-response bad-api-response)

;; ---
;; musique
;; ---

; TODO: Make play, next and previous a bit prettier and more DRY

(defn- song-url [db song]
  (let [creds (:credentials db)]
    (api/song-url (:server creds) (select-keys creds [:u :p]) song)))

(re-frame/reg-event-fx
 ; sets up the db, starts to play a song and adds the rest to a playlist
 ::play-songs
 (fn [{:keys [db]} [_ songs start-idx]]
   (let [playlist (-> (playlist/->playlist songs :playback-mode :linear :repeat-mode :repeat-all)
                      (playlist/set-current-song start-idx))]
     {:audio/play (song-url db (playlist/peek playlist))
      :db (assoc-in db [:audio :playlist] playlist)})))

;; FIXME: :audio/play might not get the right argument here

(re-frame/reg-event-db
 ::set-playback-mode
 (fn [db [_ playback-mode]]
   (update-in db [:audio :playlist] #(playlist/set-playback-mode % playback-mode))))

(re-frame/reg-event-db
 ::set-repeat-mode
 (fn [db [_ repeat-mode]]
   (update-in db [:audio :playlist] #(playlist/set-repeat-mode % repeat-mode))))

(re-frame/reg-event-fx
 ::next-song
 (fn [{:keys [db]} _]
   (let [db (update-in db [:audio :playlist] playlist/next-song)
         next (playlist/peek (get-in db [:audio :playlist]))]
     {:db db
      :audio/play (song-url db next)})))

(re-frame/reg-event-fx
 ::previous-song
 (fn [{:keys [db]} _]
   (let [db (update-in db [:audio :playlist] playlist/previous-song)
         prev (playlist/peek (get-in db [:audio :playlist]))]
     {:db db
      :audio/play (song-url db prev)})))

(re-frame/reg-event-db
 ::enqueue-next
 (fn [db [_ song]]
   (update-in db [:audio :playlist] #(playlist/enqueue-next % song))))

(re-frame/reg-event-db
 ::enqueue-last
 (fn [db [_ song]]
   (update-in db [:audio :playlist] #(playlist/enqueue-last % song))))

(re-frame/reg-event-fx
 ::toggle-play-pause
 (fn [_ _]
   {:audio/toggle-play-pause nil}))

(defn audio-update
  "Reacts to audio events fired by the HTML5 audio player and plays the next
  track if necessary."
  [{:keys [db]} [_ status]]
  (cond-> {:db (assoc-in db [:audio :playback-status] status)}
    (:ended? status) (assoc :dispatch [::next-song])))

(re-frame/reg-event-fx :audio/update audio-update)

;; ---
;; routing
;; ---

(re-frame/reg-event-fx
 :routes/did-navigate
 (fn [{:keys [db]} [_ route params query]]
   ;; FIXME: This leads to an ugly "unregistered event handler `nil`" error
   ;; all the naviagation logic is in routes.cljs; all we need to do here
   ;; is say what actually happens once we've navigated succesfully
   {:db (assoc db :current-route [route params query])
    :dispatch (routes/route-data route params query)}))

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
