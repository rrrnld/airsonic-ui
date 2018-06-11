(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.db :as db]
            [airsonic-ui.utils.api :as api]
            [day8.re-frame.tracing :refer-macros [fn-traced]])) ; <- useful to debug handlers

;; this is where all of the event handling takes place; the names put the events into
;; the following categories:
;; ::events/something-happening -> relevant to only this app
;; :single-colon/something -> coming from external sources (e.g. :audio/... or :routes/...) that are potentially reusable

(re-frame/reg-fx
 ;; a simple effect to keep println statements out of our event handlers
 :log
 (fn [params]
   (apply println params)))

;; database reset / init

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))

;; auth logic

(defn authenticate
  "Tries to authenticate a user by pinging the server with credentials, saving
  them when the request was succesful. Bypasses the request when a user saved
  their credentials."
  [{:keys [db]} [_ user pass server]]
  {:db (assoc-in db [:credentials :server] server)
   :http-xhrio {:method :get
                :uri (api/url server "ping" {:u user :p pass})
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [::verify-auth-response user pass]
                :on-failure [:api/bad-response]}})

(re-frame/reg-event-fx
 ::authenticate authenticate)

(defn verify-auth-response
  "Since we don't get real status codes, we have to look into the server's
  response and see whether we actually sent the correct credentials"
  [fx [_ user pass response]]
  {:dispatch (if (api/is-error? response)
               [:notification/show :error (api/error-msg (api/->exception response))]
               [::credentials-verified user pass])})

(re-frame/reg-event-fx
 ::verify-auth-response
 verify-auth-response)

(defn try-remember-user
  "Enables skipping the auth request when credentials are saved in the
  local storage; otherwise has no effect"
  [{:keys [db store]} [_]]
  (when-let [credentials (:credentials store)]
    {:db (assoc-in db [:credentials :server] (:server credentials))
     :dispatch [::credentials-verified (:u credentials) (:p credentials) nil]}))

(re-frame/reg-event-fx
 ::try-remember-user
 [(re-frame/inject-cofx :store)]
 try-remember-user)

(defn credentials-verified
  "Gets called after the server indicates that the credentials entered by a user
  are correct (see `authenticate`)"
  [{:keys [db store]} [_ user pass]]
  (let [auth {:u user :p pass}
        credentials (merge (:credentials db) auth)]
    {:routes/set-credentials auth
     :store {:credentials credentials}
     :db (assoc db :credentials credentials)
     :dispatch [::logged-in]}))

(re-frame/reg-event-fx
 ::credentials-verified
 [(re-frame/inject-cofx :store)]
 credentials-verified)

;; TODO: We have to find another solution for this once we have routes that
;; don't require a login but have the bottom controls

(re-frame/reg-fx
 :show-nav-bar
 (fn [_]
   (.. js/document -documentElement -classList (add "has-navbar-fixed-bottom"))))

;; we do this in two steps to make sure the credentials are set once we navigate
(re-frame/reg-event-fx
 ::logged-in
 (fn [_ _]
   {:routes/navigate [::routes/main]
    :show-nav-bar nil}))

;; TODO: Test that credentials are actually taken
;; TODO: Move these in the future? events.cljs should just do wiring. We could
;; implement api.cljs as a completely independent module.

(defn- api-url [db endpoint params]
  (let [creds (:credentials db)]
    (api/url (:server creds) endpoint (merge params (select-keys creds [:u :p])))))

(defn api-request [{:keys [db]} [_ endpoint params]]
  {:http-xhrio {:method :get
                :uri (api-url db endpoint params)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [:api/good-response]
                :on-failure [:api/bad-response]}})

(re-frame/reg-event-fx
 :api/request
 api-request)

(defn good-api-response [fx [_ response]]
  (try
    (assoc-in fx [:db :response] (api/unwrap-response response))
    (catch ExceptionInfo e
      {:dispatch [:notification/show :error (api/error-msg e)]})))

(re-frame/reg-event-fx
 :api/good-response
 good-api-response)

(re-frame/reg-event-fx
 :api/bad-response
 (fn [db event]
   {:log ["API call gone bad; are CORS headers missing? check for :status 0" event]
    :dispatch [:notification/show :error "Communication with server failed. Check browser logs for details."]}))

;; musique

; TODO: Make play, next and previous a bit prettier and more DRY

(defn- song-url [db song]
  (let [creds (:credentials db)]
    (api/song-url (:server creds) (select-keys creds [:u :p]) song)))

(re-frame/reg-event-fx
 ; sets up the db, starts to play a song and adds the rest to a playlist
 ::play-songs
 (fn [{:keys [db]} [_ songs song]]
   {:play-song (song-url db song)
    :db (-> db
            (assoc-in [:currently-playing :item] song)
            (assoc-in [:currently-playing :playlist] songs))}))

(re-frame/reg-event-fx
 ::next-song
 (fn [{:keys [db]} _]
   (let [playlist (-> db :currently-playing :playlist)
         current (-> db :currently-playing :item)
         next (first (rest (drop-while #(not= % current) playlist)))]
     (when next
       {:play-song (song-url db next)
        :db (assoc-in db [:currently-playing :item] next)}))))

(re-frame/reg-event-fx
 ::previous-song
 (fn [{:keys [db]} _]
   (let [playlist (-> db :currently-playing :playlist)
         current (-> db :currently-playing :item)
         previous (last (take-while #(not= % current) playlist))]
     (when previous
       {:play-song (song-url db previous)
        :db (assoc-in db [:currently-playing :item] previous)}))))

(re-frame/reg-event-fx
 ::toggle-play-pause
 (fn [_ _]
   {:toggle-play-pause nil}))

(re-frame/reg-event-db
 :audio/update
 (fn [db [_ status]]
   ; we receive this from the player once it's playing
   (assoc-in db [:currently-playing :status] status)))

;; routing

(re-frame/reg-event-fx
 :routes/navigation
 (fn [{:keys [db]} [_ route params query]]
   ;; all the naviagation logic is in routes.cljs; all we need to do here
   ;; is say what actually happens once we've navigated succesfully
   {:db (assoc db :current-route [route params query])
    :dispatch (routes/route-data route params query)}))

(re-frame/reg-event-fx
 :routes/unauthorized
 (fn [fx _]
   ;; log out on 403
   {:routes/navigate [routes/default-route]
    :routes/unset-credentials nil
    :db db/default-db}))

;; user messages

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

(re-frame/reg-event-fx
 :notification/show
 show-notification)

(defn hide-notification
  [db [_ notification-id]]
  (update db :notifications dissoc notification-id))

(re-frame/reg-event-db
 :notification/hide
 hide-notification)
