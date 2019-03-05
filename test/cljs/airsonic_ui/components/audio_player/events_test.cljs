(ns airsonic-ui.components.audio-player.events-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.audio.core :as audio]
            [airsonic-ui.audio.playlist :as playlist]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.test-helpers :refer [dispatches? song-queue]]
            [airsonic-ui.components.audio-player.events :as events]))

(deftest song-has-ended
  (testing "Should play the next song when current song has ended"
    (is (not (dispatches? (events/audio-update {} [:audio/update {:ended? false}]) :audio-player/next-song)))
    (is (dispatches? (events/audio-update {} [:audio/update {:ended? true}]) :audio-player/next-song))))

(deftest changing-current-song
  (testing "Should correctly set the current song index"
    (doseq [playback-mode [:linear :shuffled]
            repeat-mode [:repeat-none :repeat-single :repeat-all]]
      (let [n-songs 100
            next-idx (rand-int n-songs)
            fixture {:db {:credentials fixtures/credentials
                          :audio {:current-playlist (playlist/->playlist (song-queue n-songs) :playback-mode playback-mode :repeat-mode repeat-mode)}}}
            effects (events/set-current-song fixture [:audio/set-current-song next-idx])]
        (is (= next-idx
               (-> (:db effects)
                   (audio/summary [:audio/summary])
                   (audio/current-playlist [:audio/current-playlist])
                   (:current-idx)))
            (str "for playback-mode " playback-mode " and repeat-mode " repeat-mode))
        (is (contains? effects :audio/play))))))
