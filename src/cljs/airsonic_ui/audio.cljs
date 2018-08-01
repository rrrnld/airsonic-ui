(ns airsonic-ui.audio
  (:require [re-frame.core :as re-frame]))

;; TODO: Manage buffering

(defonce audio (atom nil))

(defn ->status
  "Takes an audio object and returns a map describing its current status"
  [elem]
  {:ended? (.-ended elem)
   :loop? (.-loop elem)
   :muted? (.-muted elem)
   :paused? (.-paused elem)
   :current-src (.-currentSrc elem)
   :current-time (.-currentTime elem)})

; explanation of these events: https://developer.mozilla.org/en-US/Apps/Fundamentals/Audio_and_video_delivery/Cross-browser_audio_basics
(defn attach-listeners! [el]
  (doseq [event ["loadstart" "progress" "play" "timeupdate" "pause"]]
    (.addEventListener el event #(re-frame/dispatch [:audio/update (->status el)]))))

;; effects to be fired from event handlers

(re-frame/reg-fx
 :audio/play
 (fn [song-url]
   (when-not @audio
     (reset! audio (js/Audio.))
     (attach-listeners! @audio))
   (.pause @audio)
   (set! (.-src @audio) song-url)
   (.play @audio)))

(re-frame/reg-fx
 :audio/pause
 (fn [_]
   (some-> @audio .pause)))

(re-frame/reg-fx
 :audio/stop
 (fn [_]
   (when-let [audio @audio]
     (.pause audio)
     (set! (.-currentTime audio) 0))))

(re-frame/reg-fx
 :audio/toggle-play-pause
 (fn [_]
   (if-let [a @audio]
     (if (.-paused a)
       (.play a)
       (.pause a)))))

;; subscriptions

(defn summary
  "Returns all information about audio that we have"
  [db _]
  (:audio db))

(re-frame/reg-sub :audio/summary summary)

(defn current-song
  "Gives us information about the currently played song as presented by
  the airsonic api"
  [summary _]
  (:current-song summary))

(re-frame/reg-sub
 :audio/current-song
 (fn [_ _] (re-frame/subscribe [:audio/summary]))
 current-song)

(defn playback-status
  "Gives us information about the most recently fired html 5 audio event"
  [summary _]
  (:playback-status summary))

(re-frame/reg-sub
 :audio/playback-status
 (fn [_ _] (re-frame/subscribe [:audio/summary]))
 playback-status)

(defn is-playing?
  "Predicate to tell us whether we currently have audio output or not"
  [playback-status _]
  (and (not (:paused? playback-status))
       (not (:ended? playback-status))))

(re-frame/reg-sub
 :audio/is-playing?
 (fn [_ _] (re-frame/subscribe [:audio/current-playback-status]))
 is-playing?)

(comment
  ;; NOTE: Not in use currently
  (defn current-playlist
    "Lists the complete playlist"
    [summary _]
    (:playlist summary))

  (re-frame/reg-sub
   :audio/current-playlist
   (fn [_ _] (re-frame/subscribe [:audio/summary]))
   current-playlist))
