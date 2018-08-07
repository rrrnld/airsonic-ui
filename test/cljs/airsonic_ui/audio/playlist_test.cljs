(ns airsonic-ui.audio.playlist-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.audio.playlist :as playlist]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.test-helpers :as helpers]
            [debux.cs.core :refer-macros [dbg]]))

(enable-console-print!)

(defn- playing-queue
  "Generates a seq of n different songs"
  [n]
  (repeatedly n #(hash-map :id (rand-int 9999)
                           :coverArt (rand-int 9999)
                           :year (+ 1900 (rand-int 118))
                           :artist (helpers/rand-str)
                           :aristId (rand-int 100000)
                           :title (helpers/rand-str)
                           :album (helpers/rand-str))))

(def fixture
  {:audio {:current-song fixtures/song
           :playlist (playing-queue 20)
           :playback-status fixtures/playback-status}})

(defn- same-song? [a b] (= (:id a) (:id b)))

(deftest playlist-creation
  (testing "Playlist creation"
    (testing "should give us the correct current song"
      (let [queue (playing-queue 10)
            start-idx (rand-int 10)]
        (doseq [playback-mode [:playback-mode/linear, :playback-mode/shuffle]
                repeat-mode [:repeat-mode/none, :repeat-mode/single, :repeat-mode/all]]
          (is (same-song? (nth queue start-idx)
                          (-> (playlist/->playlist queue start-idx playback-mode repeat-mode)
                              (playlist/peek)))
              (str playback-mode ", " repeat-mode)))))
    (testing "should give us a playlist with the correct number of tracks"
      (let [queue (playing-queue 100)
            start-idx (rand-int 100)]
        (doseq [playback-mode [:playback-mode/linear, :playback-mode/shuffle]
                repeat-mode [:repeat-mode/none, :repeat-mode/single, :repeat-mode/all]]
          (is (= (count queue)
                 (count (playlist/->playlist queue start-idx playback-mode repeat-mode)))
              (str playback-mode ", " repeat-mode)))))))

(deftest changing-playback-mode
  (testing "Changing playback mode"
    (testing "from linear to shuffled"
      (let [queue (playing-queue 10)
            start-idx (rand-int 10)
            linear (playlist/->playlist queue start-idx :playback-mode/linear :repeat-mode/node)
            shuffled (playlist/set-playback-mode linear :playback-mode/shuffle)]
        (testing "should re-order the tracks"
          (is (not= (map :order (:queue shuffled)) (map :order (:queue linear)))))
        (testing "should not change the currently playing track"
          (is (same-song? (playlist/peek linear) (playlist/peek shuffled))))
        (testing "should not change the repeat mode"
          (is (= (:repeat-mode shuffled) (:repeat-mode linear)))))
      (testing "from shuffled to linear"
        (let [queue (playing-queue 10)
              start-idx (rand-int 10)
              shuffled (playlist/->playlist queue start-idx :playback-mode/shuffle :repeat-mode/none)
              linear (playlist/set-playback-mode shuffled :playback-mode/linear)]
          (testing "should set the correct order for tracks"
            (is (every? #(apply same-song? %)
                        (interleave (take start-idx (:queue shuffled))
                                    (take start-idx (:queue linear))))
                "before")
            (is (every? #(apply same-song? %)
                        (interleave (drop (inc start-idx) (:queue shuffled))
                                    (drop (inc start-idx) (:queue linear))))
                "after")
            (is (< (:order (first (:queue linear))) (:order (last (:queue linear))))))
          (testing "should not change the currently playing track"
            (is (same-song? (playlist/peek linear) (playlist/peek shuffled))))
          (testing "should not change the repeat mode"
            (is (= (:repeat-mode shuffled) (:repeat-mode linear)))))))))

(deftest chaging-repeat-mode
  (testing "Changing the repeat mode"
    (testing "should not change the playback mode"
      (doseq [playback-mode [:playback-mode/linear, :playback-mode/shuffle]
              repeat-mode [:repeat-mode/none, :repeat-mode/single, :repeat-mode/all]
              next-repeat-mode [:repeat-mode/none, :repeat-mode/single, :repeat-mode/all]]
        (let [playlist (-> (playlist/->playlist (playing-queue 1) 0 playback-mode repeat-mode)
                           (playlist/set-repeat-mode next-repeat-mode))]
          (is (= playback-mode (:playback-mode playlist)))
          (is (= next-repeat-mode (:repeat-mode playlist))
              (str "from " repeat-mode " to " next-repeat-mode)))))))

(deftest linear-next-song
  (testing "Should follow the same order as the queue used for creation"
    (doseq [repeat-mode [:repeat-mode/none :repeat-mode/all]]
      (let [queue (playing-queue 5)
            playlist (playlist/->playlist queue 0 :playback-mode/linear repeat-mode)]
        (is (same-song? (nth queue 1) (-> (playlist/next-song playlist)
                                          (playlist/peek)))
            (str repeat-mode ", skipped once"))
        (is (same-song? (nth queue 2) (-> (playlist/next-song playlist)
                                          (playlist/next-song)
                                          (playlist/peek)))
            (str repeat-mode ", skipped twice")))))
  (testing "Should go back to the first song when repeat-mode is all and we played the last song")
  (testing "Should always give the same track when repeat-mode is single"
    (let [queue (playing-queue 3)
          playing-idx (rand-int 3)
          playlist (playlist/->playlist queue playing-idx :playback-mode/linear :repeat-mode/single)]
      (is (same-song? (nth queue playing-idx) (playlist/peek playlist)))
      (is (same-song? (nth queue playing-idx)
                      (-> (playlist/next-song playlist)
                          (playlist/peek))))
      (is (same-song? (nth queue playing-idx)
                      (-> (playlist/next-song playlist)
                          (playlist/next-song)
                          (playlist/peek))))
      (is (same-song? (nth queue playing-idx)
                      (-> (playlist/next-song playlist)
                          (playlist/next-song)
                          (playlist/next-song)
                          (playlist/peek)))
          "wrapping around")))
  (testing "Should stop playing at the end of the queue when repeat-mode is none"
    (is (nil? (-> (playing-queue 1)
                  (playlist/->playlist 0 :playback-mode/linear :repeat-mode/none)
                  (playlist/next-song)
                  (playlist/peek))))))

#_(deftest shuffled-next-song
  (testing "Should play every track once when called for the entire queue")
  (testing "Should re-shuffle the playlist when wrapping around and repeat-mode is all")
  (testing "Should always give the same track when repeat-mode is single")
  (testing "Should stop playing at the end of the queue when repeat-mode is none"))

#_(deftest linear-previous-song)
#_(deftest shuffled-previous-song)
