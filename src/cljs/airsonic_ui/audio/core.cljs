(ns airsonic-ui.audio.core
  "This namespace contains some JS interop code to interact with an audio player
  and receive information about the current playback status so we can use it in
  our re-frame app."
  (:require [re-frame.core :as rf]
            [airsonic-ui.audio.playlist :as playlist]
            [goog.functions :refer [throttle]]))

(defonce audio (atom nil))

(defn normalize-time-ranges [time-ranges]
  (if (> (.-length time-ranges) 0)
    (.end time-ranges (dec (.-length time-ranges)))
    0))

(defn ->status
  "Takes an audio object and returns a map describing its current status"
  [elem]
  {:ended? (.-ended elem)
   :paused? (.-paused elem)
   :current-src (.-currentSrc elem)
   :current-time (.-currentTime elem)
   :seekable (normalize-time-ranges (.-seekable elem))
   :buffered (normalize-time-ranges (.-buffered elem))
   :volume (.-volume elem)})

 ; explanation of these events: https://developer.mozilla.org/en-US/Apps/Fundamentals/Audio_and_video_delivery/Cross-browser_audio_basics

(defn attach-listeners! [el]
  (let [emit-audio-update (throttle #(rf/dispatch [:audio/update (->status el)]) 16)]
    (doseq [event ["loadstart" "progress" "play" "timeupdate" "pause" "volumechange"]]
      (.addEventListener el event emit-audio-update))))

;; effects to be fired from event handlers

(rf/reg-fx
 :audio/play
 (fn [stream-url]
   (when-not @audio
     (reset! audio (js/Audio.))
     (attach-listeners! @audio))
   (.pause @audio)
   (set! (.-src @audio) stream-url)
   (.play @audio)))

(rf/reg-fx
 :audio/pause
 (fn [_]
   (some-> @audio .pause)))

(rf/reg-fx
 :audio/stop
 (fn [_]
   (when-let [audio @audio]
     (.pause audio)
     (set! (.-currentTime audio) 0)
     (set! (.-src audio) ""))))

(rf/reg-fx
 :audio/toggle-play-pause
 (fn [_]
   (if-let [a @audio]
     (if (.-paused a)
       (.play a)
       (.pause a)))))

(rf/reg-fx
 :audio/seek
 (fn [[percentage duration]]
   (set! (. @audio -currentTime)
         (* percentage duration))))

(defn- set-volume! [volume]
  (set! (.-volume @audio) volume))

(rf/reg-fx
 :audio/set-volume
 (fn [percentage]
   (when @audio
     (set-volume! percentage))))

(rf/reg-fx
 :audio/increase-volume
 (fn [_]
   (when-let [vol (some-> @audio .-volume)]
     (set-volume! (min 1 (+ vol 0.05))))))

(rf/reg-fx
 :audio/decrease-volume
 (fn [_]
   (when-let [vol (some-> @audio .-volume)]
     (set-volume! (max 0 (- vol 0.05))))))

;; subscriptions

(defn summary
  "Returns all information about audio that we have"
  [db _]
  (:audio db))

(rf/reg-sub :audio/summary summary)

(defn current-playlist
  "Lists the complete current-queue"
  [summary _]
  (:current-playlist summary))

(rf/reg-sub
 :audio/current-playlist
 :<- [:audio/summary]
 current-playlist)

(defn current-song
  "Gives us information about the currently played song as presented by
  the airsonic api"
  [playlist _]
  (when-not (empty? playlist)
    (playlist/current-song playlist)))

(rf/reg-sub
 :audio/current-song
 :<- [:audio/current-playlist]
 current-song)

(defn playback-status
  "Gives us information about the most recently fired html 5 audio event"
  [summary _]
  (:playback-status summary))

(rf/reg-sub
 :audio/playback-status
 :<- [:audio/summary]
 playback-status)

(defn is-playing?
  "Predicate to tell us whether we currently have audio output or not"
  [playback-status _]
  (and (not (:paused? playback-status))
       (not (:ended? playback-status))))

(rf/reg-sub
 :audio/is-playing?
 :<- [:audio/playback-status]
 is-playing?)
