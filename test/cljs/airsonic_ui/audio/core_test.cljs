(ns airsonic-ui.audio.core-test
  (:require [airsonic-ui.audio.core :as audio]
            #_[airsonic-ui.audio.playlist-test :as p]
            #_[airsonic-ui.fixtures :as fixtures]
            [cljs.test :refer [deftest testing is]]))

(enable-console-print!)

(deftest current-song-subscription
  ;; NOTE: Should the subscription be moved to the playlist.cljs?
  #_(testing "Should provide information about the song"
    (letfn [(current-song [db]
            (-> (audio/summary db [:audio/summary])
                (audio/current-song [:audio/current-song])))]
      (= fixtures/song (current-song p/fixture))))
  (testing "Should work fine when no song is playing"
    (is (nil? (audio/current-song nil [:audio/current-song])))))

(deftest playback-status-subscription
  (letfn [(is-playing? [playback-status]
            (audio/is-playing? playback-status [:audio/is-playing?]))]
    (testing "Should be shown as not playing when the song is paused or has ended"
      (is (not (is-playing? {:paused? true, :ended? false})))
      (is (not (is-playing? {:paused? false, :ended? true}))))
    (testing "Should be shown as playing when the song is not paused or finished"
      (is (is-playing? {:paused? false, :ended? false})))))
