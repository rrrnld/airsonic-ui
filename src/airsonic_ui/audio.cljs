(ns airsonic-ui.audio
  (:require [re-frame.core :as re-frame]))

;; TODO: Manage multiple songs, buffering, stopping, progress notification...

(def current-audio (atom nil))

(re-frame/reg-fx
 :play-song
 (fn [song-url]
   (let [audio (js/Audio. song-url)]
     (reset! current-audio audio)
     (.play audio))))
