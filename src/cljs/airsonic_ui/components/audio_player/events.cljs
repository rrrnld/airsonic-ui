(ns airsonic-ui.components.audio-player.events
  (:require [re-frame.core :as rf]
            [airsonic-ui.audio.playlist :as playlist]
            [airsonic-ui.api.helpers :as api]))

; sets up the db, starts to play a song and adds the rest to a playlist
(defn play-all-songs [{:keys [db]
                       :routes/keys [current-route]} [_ songs start-idx]]
  (let [playlist (-> (playlist/->playlist songs :playback-mode :linear :repeat-mode :repeat-all :source current-route)
                     (playlist/set-current-song start-idx))]
    {:audio/play (api/stream-url (:credentials db) (playlist/current-song playlist))
     :db (assoc-in db [:audio :current-playlist] playlist)}))

(rf/reg-event-fx
 :audio-player/play-all
 [(rf/inject-cofx :routes/current-route)]
 play-all-songs)

(rf/reg-event-db
 :audio-player/set-playback-mode
 (fn [db [_ playback-mode]]
   (update-in db [:audio :current-playlist] #(playlist/set-playback-mode % playback-mode))))

(rf/reg-event-db
 :audio-player/set-repeat-mode
 (fn [db [_ repeat-mode]]
   (update-in db [:audio :current-playlist] #(playlist/set-repeat-mode % repeat-mode))))

(rf/reg-event-fx
 :audio-player/next-song
 (fn [{:keys [db]} _]
   (let [db (update-in db [:audio :current-playlist] playlist/next-song)
         next (playlist/current-song (get-in db [:audio :current-playlist]))]
     {:db db
      :audio/play (api/stream-url (:credentials db) next)})))

(rf/reg-event-fx
 :audio-player/previous-song
 (fn [{:keys [db]} _]
   (let [db (update-in db [:audio :current-playlist] playlist/previous-song)
         song (playlist/current-song (get-in db [:audio :current-playlist]))]
     {:db db
      :audio/play (api/stream-url (:credentials db) song)})))

(defn set-current-song [{:keys [db]} [_ idx]]
  (let [db (update-in db [:audio :current-playlist] playlist/set-current-song idx)
        song (playlist/current-song (get-in db [:audio :current-playlist]))]
    {:db db
     :audio/play (api/stream-url (:credentials db) song)}))

(rf/reg-event-fx :audio-player/set-current-song set-current-song)

(rf/reg-event-fx
 :audio-player/enqueue-next
 [(rf/inject-cofx :routes/current-route)]
 (fn [{:keys [db]
       :routes/keys [current-route]} [_ song]]
   {:db (update-in db [:audio :current-playlist] #(playlist/enqueue-next % song current-route))}))

(rf/reg-event-fx
 :audio-player/enqueue-last
 [(rf/inject-cofx :routes/current-route)]
 (fn [{:keys [db]
       :routes/keys [current-route]} [_ song]]
   {:db (update-in db [:audio :current-playlist] #(playlist/enqueue-last % song current-route))}))

(rf/reg-event-db
 :audio-player/move-song
 (fn [db [_ from-idx to-idx]]
   (update-in db [:audio :current-playlist] #(playlist/move-song % from-idx to-idx))))

(rf/reg-event-fx
 :audio-player/toggle-play-pause
 (fn [_ _]
   {:audio/toggle-play-pause nil}))

(defn remove-song [{:keys [db]} [_ song-idx]]
  (let [song-removed (update-in db [:audio :current-playlist] #(playlist/remove-song % song-idx))]
    (cond-> {:db song-removed}
      (nil? (playlist/current-song (get-in song-removed [:audio :current-playlist])))
      (assoc :audio/stop nil))))

(rf/reg-event-fx :audio-player/remove-song remove-song)

(defn audio-update
  "Reacts to audio events fired by the HTML5 audio player and plays the next
  track if necessary."
  [{:keys [db]} [_ status]]
  (cond-> {:db (assoc-in db [:audio :playback-status] status)}
    (:ended? status) (assoc :dispatch [:audio-player/next-song])))

(rf/reg-event-fx :audio/update audio-update)

(rf/reg-event-fx
 :audio-player/seek
 (fn [{:keys [db]} [_ percentage]]
   (let [duration (:duration (playlist/current-song (get-in db [:audio :current-playlist])))]
     {:audio/seek [percentage duration]})))

(rf/reg-event-fx
 :audio-player/set-volume
 (fn [_ [_ percentage]]
   {:audio/set-volume percentage}))

(rf/reg-event-fx
 :audio-player/increase-volume
 (fn [_ _]
   {:audio/increase-volume nil}))

(rf/reg-event-fx
 :audio-player/decrease-volume
 (fn [_ _]
   {:audio/decrease-volume nil}))
