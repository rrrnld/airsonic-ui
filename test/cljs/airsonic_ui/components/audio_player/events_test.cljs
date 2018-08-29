(ns airsonic-ui.components.audio-player.events-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.test-helpers :refer [dispatches?]]
            [airsonic-ui.components.audio-player.events :as events]))


(deftest song-has-ended
  (testing "Should play the next song when current song has ended"
    (is (not (dispatches? (events/audio-update {} [:audio/update {:ended? false}]) :audio-player/next-song)))
    (is (dispatches? (events/audio-update {} [:audio/update {:ended? true}]) :audio-player/next-song))))
