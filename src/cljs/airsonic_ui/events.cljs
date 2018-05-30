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

;; database reset / init

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))

;; auth logic

(defn authenticate
  "Tries to authenticate a user by pinging the server with credentials, saving
  them when the request was succesful."
  [{:keys [db]} [_ user pass server]]
  {:db (assoc db :server server)
   :http-xhrio {:method :get
                :uri (api/url server "ping" {:u user :p pass})
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [::credentials-verified user pass]
                :on-failure [::api-failure]}})

(re-frame/reg-event-fx
 ::authenticate authenticate)

(defn credentials-verified
  "Gets called after the server indicates that the credentials entered by a user
  are correct (see `authenticate`)."
  [{:keys [db]} [_ user pass response]]
  (let [login {:u user :p pass}]
    {:routes/set-credentials login
     :db (assoc db :login login)
     :dispatch [::logged-in]}))

(re-frame/reg-event-fx
 ::credentials-verified credentials-verified)

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

(re-frame/reg-event-fx
 :api-request
 (fn [{:keys [db]} [_ endpoint k params]]
   {:http-xhrio {:method :get
                 :uri (api/url (:server db) endpoint (merge params (:login db)))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::api-success k]
                 :on-failure [::api-failure]}}))

(re-frame/reg-event-db
 ::api-success
 (fn [db [_ k response]]
   ; we "unwrap" the responses
   (assoc db :response (-> response :subsonic-response k))))

(re-frame/reg-event-db
 ::api-failure
 (fn [db event]
   (println "api call gone bad; CORS headers missing? check for :status 0" event)
   db))

;; musique

; TODO: Make play, next and previous a bit prettier and more DRY

(re-frame/reg-event-fx
 ; sets up the db, starts to play a song and adds the rest to a playlist
 ::play-songs
 (fn [{:keys [db]} [_ songs song]]
   {:play-song (api/song-url (:server db) (:login db) song)
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
       {:play-song (api/song-url (:server db) (:login db) next)
        :db (assoc-in db [:currently-playing :item] next)}))))

(re-frame/reg-event-fx
 ::previous-song
 (fn [{:keys [db]} _]
   (let [playlist (-> db :currently-playing :playlist)
         current (-> db :currently-playing :item)
         previous (last (take-while #(not= % current) playlist))]
     (when previous
       {:play-song (api/song-url (:server db) (:login db) previous)
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
