(ns airsonic-ui.audio.core
  "This namespace contains some JS interop code to interact with an audio player
  and receive information about the current playback status so we can use it in
  our re-frame app."
  (:require [re-frame.core :as re-frame]
            [airsonic-ui.audio.playlist :as playlist]
            [goog.functions :refer [throttle]]))

;; TODO: Manage buffering

(defonce audio (atom nil))

(defn ->status
  "Takes an audio object and returns a map describing its current status"
  [elem]
  {:ended? (.-ended elem)
   :paused? (.-paused elem)
   :current-src (.-currentSrc elem)
   :current-time (.-currentTime elem)
   :seekable (let [seekable (.-seekable elem)]
               (if (> (.-length seekable) 0)
                 (.end seekable (dec (.-length seekable)))
                 0))})

; explanation of these events: https://developer.mozilla.org/en-US/Apps/Fundamentals/Audio_and_video_delivery/Cross-browser_audio_basics


(defn attach-listeners! [el]
  (let [emit-audio-update (throttle #(re-frame/dispatch [:audio/update (->status el)]) 16)]
    (doseq [event ["loadstart" "progress" "play" "timeupdate" "pause"]]
      (.addEventListener el event emit-audio-update))))

;; effects to be fired from event handlers

(re-frame/reg-fx
 :audio/play
 (fn [stream-url]
   (when-not @audio
     (reset! audio (js/Audio.))
     (attach-listeners! @audio))
   (.pause @audio)
   (set! (.-src @audio) stream-url)
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

(re-frame/reg-fx
 :audio/seek
 (fn [[percentage duration]]
   (set! (. @audio -currentTime)
         (* percentage duration))))

;; subscriptions

(defn summary
  "Returns all information about audio that we have"
  [db _]
  (:audio db))

(re-frame/reg-sub :audio/summary summary)

(defn playlist
  "Lists the complete playlist"
  [summary _]
  (:playlist summary))

(re-frame/reg-sub
 :audio/playlist
 (fn [_ _] (re-frame/subscribe [:audio/summary]))
 playlist)

(defn current-song
  "Gives us information about the currently played song as presented by
  the airsonic api"
  [playlist _]
  (playlist/peek playlist))

(re-frame/reg-sub
 :audio/current-song
 (fn [_ _] (re-frame/subscribe [:audio/playlist]))
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
 (fn [_ _] (re-frame/subscribe [:audio/playback-status]))
 is-playing?)
