(ns airsonic-ui.audio
  (:require [re-frame.core :as re-frame]))

;; TODO: Manage multiple songs, buffering, stopping, progress notification...

(defonce current-audio (atom nil))

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

(re-frame/reg-fx
 :play-song
 (fn [song-url]
   (some-> @current-audio .pause)
   (let [audio (js/Audio. song-url)]
     (reset! current-audio audio)
     (attach-listeners! audio)
     (.play audio))))

(re-frame/reg-fx
 :toggle-play-pause
 (fn [_]
   (when-let [a @current-audio]
     (if (.-paused a)
       (.play a)
       (.pause a)))))
