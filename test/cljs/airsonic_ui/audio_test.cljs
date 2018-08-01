(ns airsonic-ui.audio-test
  (:require [airsonic-ui.audio :as audio]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.test-helpers :as helpers]
            [cljs.test :refer [deftest testing is]]))

(enable-console-print!)

(defn- simulate-playlist [n ]
  (repeatedly n #(hash-map :id (rand-int 9999)
                           :coverArt (rand-int 9999)
                           :year (+ 1900 (rand-int 118))
                           :artist (helpers/rand-str)
                           :aristId (rand-int 100000)
                           :title (helpers/rand-str)
                           :album (helpers/rand-str))))

(def fixture
  {:audio {:current-song fixtures/song
           :playlist (simulate-playlist 20)
           :playback-status fixtures/playback-status}})

(deftest current-song
  (letfn [(current-song [db]
            (-> (audio/summary db [:audio/summary])
                (audio/current-song [:audio/current-song])))]
    (testing "Should provide information about the song"
      (= fixtures/song (current-song fixture)))))

(deftest playback-status
  (letfn [(is-playing? [playback-status]
            (audio/is-playing? playback-status [:audio/is-playing?]))]
    (testing "Should be shown as not playing when the song is paused or has ended"
      (is (not (is-playing? {:paused? true, :ended? false})))
      (is (not (is-playing? {:paused? false, :ended? true}))))
    (testing "Should be shown as playing when the song is not paused or finished"
      (is (is-playing? {:paused? false, :ended? false})))))

#_(deftest current-playlist
  (testing "Should show the complete playlist"))
