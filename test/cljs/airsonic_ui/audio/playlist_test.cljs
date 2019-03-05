(ns airsonic-ui.audio.playlist-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.audio.playlist :as playlist]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.test-helpers :refer [song song-queue]]
            #_[debux.cs.core :refer-macros [dbg]]))

(enable-console-print!)

(def fixture
  {:audio {:current-song fixtures/song
           :playlist (song-queue 20)
           :playback-status fixtures/playback-status}})

(defn- same-song? [a b] (= (:id a) (:id b)))

(deftest playlist-creation
  (testing "Playlist creation"
    (testing "should give us the correct current song for linear playback-mode"
      (let [queue (song-queue 10)]
        (doseq [repeat-mode [:repeat-none :repeat-single :repeat-all]]
          (is (same-song? (first queue)
                          (-> (playlist/->playlist queue :playback-mode :linear :repeat-mode repeat-mode)
                              (playlist/current-song)))
              (str "repeat-mode: " repeat-mode)))))

    (testing "any current song for shuffled playback mode"
      (let [queue (song-queue 10)]
        (doseq [repeat-mode [:repeat-none :repeat-single :repeat-all]]
          (is (some? ((set queue)
                      (-> (playlist/->playlist queue :playback-mode :linear :repeat-mode repeat-mode)
                          (playlist/current-song))))
              (str "repeat-mode: " repeat-mode)))))

    (testing "should give us a playlist with the correct number of tracks"
      (let [queue (song-queue 100)]
        (doseq [playback-mode [:linear :shuffled]
                repeat-mode [:repeat-none :repeat-single :repeat-all]]
          (is (= (count queue)
                 (count (playlist/->playlist queue :playback-mode playback-mode :repeat-mode repeat-mode)))
              (str playback-mode ", " repeat-mode)))))))

(deftest changing-playback-mode
  (testing "Changing playback mode"
    (testing "from linear to shuffled"
      (let [queue (song-queue 10)
            linear (playlist/->playlist queue :playback-mode :linear :repeat-mode :repeat-none)
            shuffled (playlist/set-playback-mode linear :shuffled)]
        (testing "should indicate the new playback mode"
          (is (= :linear (:playback-mode linear)))
          (is (= :shuffled (:playback-mode shuffled))))
        (testing "should re-order the tracks"
          (is (not= (:items shuffled) (:items linear))))
        (testing "should not change the currently playing track"
          (is (same-song? (playlist/current-song linear) (playlist/current-song shuffled))))
        (testing "should not change the repeat mode"
          (is (= (:repeat-mode shuffled) (:repeat-mode linear))))))
    (testing "from shuffled to linear"
      (let [queue (song-queue 10)
            shuffled (playlist/->playlist queue :playback-mode :shuffled :repeat-mode :repeat-none)
            linear (playlist/set-playback-mode shuffled :linear)]
        (testing "should indicate the new playback mode"
          (is (= :linear (:playback-mode linear)))
          (is (= :shuffled (:playback-mode shuffled))))
        (testing "should set the correct order for tracks"
          (let [linear-order (comp :playlist/linear-order meta)]
            (is (every? #(apply same-song? %) (interleave queue (vals (:items linear)))))
            ;; every song should have a smaller order than its successor
            (is (->> (map linear-order (vals (:items linear)))
                     (partition 2 1)
                     (every? (fn [[a b]] (< a b)))))))
        (testing "should not change the currently playing track"
          (is (same-song? (playlist/current-song linear) (playlist/current-song shuffled))))
        (testing "should not change the repeat mode"
          (is (= (:repeat-mode shuffled) (:repeat-mode linear))))))))

(deftest changing-repeat-mode
  (testing "Changing the repeat mode"
    (testing "should not change the playback mode"
      (doseq [playback-mode '(:linear :shuffled)
              repeat-mode '(:repeat-none :repeat-single :repeat-all)
              next-repeat-mode '(:repeat-none :repeat-single :repeat-all)]
        (let [playlist (-> (playlist/->playlist (song-queue 1) :playback-mode playback-mode :repeat-mode repeat-mode)
                           (playlist/set-repeat-mode next-repeat-mode))]
          (is (= playback-mode (:playback-mode playlist)))
          (is (= next-repeat-mode (:repeat-mode playlist))
              (str "from " repeat-mode " to " next-repeat-mode)))))))

(deftest linear-next-song
  (testing "Should follow the same order as the queue used for creation"
    (doseq [repeat-mode [:repeat-none :repeat-all]]
      (let [queue (song-queue 5)
            playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode repeat-mode)]
        (is (same-song? (nth queue 1) (-> (playlist/next-song playlist)
                                          (playlist/current-song)))
            (str repeat-mode ", skipped once"))
        (is (same-song? (nth queue 2) (-> (playlist/next-song playlist)
                                          (playlist/next-song)
                                          (playlist/current-song)))
            (str repeat-mode ", skipped twice")))))
  ;; TODO: Write this test
  (testing "Should go back to the first song when repeat-mode is all and we played the last song")
  (testing "Should always give the same track when repeat-mode is single"
    (let [queue (song-queue 3)
          playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode :repeat-single)
          played-back (map playlist/current-song (iterate playlist/next-song playlist))]
      (is (same-song? (first queue) (nth played-back 0)))
      (is (same-song? (first queue) (nth played-back 1)))
      (is (same-song? (first queue) (nth played-back 2)))
      (is (same-song? (first queue) (nth played-back 3)) "wrapping around")))
  (testing "Should stop playing at the end of the queue when repeat-mode is none"
    (is (nil? (-> (song-queue 1)
                  (playlist/->playlist :playback-mode :linear :repeat-mode :repeat-none)
                  (playlist/next-song)
                  (playlist/current-song))))))

(deftest shuffled-next-song
  (testing "Should play every track once when called for the entire queue"
    (doseq [repeat-mode '(:repeat-none :repeat-all)]
      (let [length 10
            playlist (playlist/->playlist (song-queue length) :playback-mode :shuffled :repeat-mode repeat-mode)
            played-tracks (->> (iterate playlist/next-song playlist)
                               (map playlist/current-song)
                               (take length))]
        (is (= (count played-tracks) (count (set played-tracks)))
            (str repeat-mode)))))
  (testing "Should keep the song order when wrapping around and repeat-mode is all"
    (let [playlist (playlist/->playlist (song-queue 100) :playback-mode :shuffled :repeat-mode :repeat-all)
          next-playlist (-> (playlist/set-current-song playlist 99)
                            (playlist/next-song))]
      (= (playlist/current-song playlist)
         (playlist/current-song next-playlist))))

  (testing "Should always give the same track when repeat-mode is single"
    (let [playlist (playlist/->playlist (song-queue 10) :playback-mode :shuffled :repeat-mode :repeat-single)
          played-back (map playlist/current-song (iterate playlist/next-song playlist))]
      (dotimes [i 3]
        (is (same-song? (nth played-back i) (nth played-back (inc i)))))))

  (testing "Should stop playing at the end of the queue when repeat-mode is none"
    (is (nil? (-> (song-queue 1)
                  (playlist/->playlist :playback-mode :linear :repeat-mode :repeat-none)
                  (playlist/next-song)
                  (playlist/current-song))))))

(deftest linear-previous-song
  (testing "Should always give the same track when repeat-mode is single"
    (let [queue (song-queue 3)
          playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode :repeat-single)
          played-back (map playlist/current-song (iterate playlist/next-song playlist))]
      (is (same-song? (first queue) (nth played-back 0)))
      (is (same-song? (first queue) (nth played-back 1)))
      (is (same-song? (first queue) (nth played-back 2)))
      (is (same-song? (first queue) (nth played-back 3)) "wrapping around")))
  (testing "Should keep the linear order when repeat-mode is not single"
    (doseq [repeat-mode '(:repeat-none :repeat-all)]
      (let [queue (song-queue 3)
            playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode repeat-mode)]
        (is (same-song? (nth queue 1) (-> (playlist/next-song playlist)
                                          (playlist/next-song)
                                          (playlist/previous-song)
                                          (playlist/current-song)))))))
  ;; TODO: Should it?
  #_(testing "Should repeatedly give the first song when repeat-mode is none"
    (let [queue (song-queue 3)
          playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode :repeat-none)]
      (is (same-song? (first queue) (-> (playlist/previous-song playlist)
                                        (playlist/current-song))))))
  (testing "Should wrap around to last song when repeat-mode is all"
    (let [queue (song-queue 3)
          playlist (playlist/->playlist queue :playback-mode :linear :repeat-mode :repeat-all)]
      (is (same-song? (last queue) (-> (playlist/previous-song playlist)
                                       (playlist/current-song)))))))

(deftest shuffled-previous-song
  (with-redefs [shuffle reverse]
    (testing "Should always give the same track when repeat-mode is single"
      (let [queue (song-queue 3)
            playlist (playlist/->playlist queue :playback-mode :shuffled :repeat-mode :repeat-single)
            played-back (map playlist/current-song (iterate playlist/next-song playlist))]
        (dotimes [i 3]
          (is (same-song? (nth played-back i) (nth played-back (inc i)))))))
    (testing "Should keep the playing order when repeat-mode is not single"
      (doseq [repeat-mode '(:repeat-none :repeat-all)]
        (let [queue (song-queue 3)
              playlist (playlist/->playlist queue :playback-mode :shuffled :repeat-mode repeat-mode)]
          (is (same-song? (playlist/current-song playlist)
                          (-> playlist
                              (playlist/next-song)
                              (playlist/previous-song)
                              (playlist/current-song)))
              (str "for repeat mode " repeat-mode))
          (is (same-song? (-> (playlist/next-song playlist)
                              (playlist/current-song))
                          (-> (playlist/next-song playlist)
                              (playlist/next-song)
                              (playlist/previous-song)
                              (playlist/current-song)))
                (str "for repeat mode " repeat-mode)))))
    (testing "Should keep the song order when repeat-mode is all and we go back to before the first track"
      (let [playlist (playlist/->playlist (song-queue 10) :playback-mode :shuffled :repeat-mode :repeat-all)
            next-playlist (-> (playlist/previous-song playlist)
                              (playlist/set-current-song 0))]
        (is (= (playlist/current-song playlist)
               (playlist/current-song next-playlist)))))))

(deftest set-current-song
  (testing "Should correctly set the new song"
    (doseq [repeat-mode [:repeat-all :repeat-none]]
      (let [queue (song-queue 3)
            playlist (playlist/->playlist queue :playback-mode :shuffled :repeat-mode repeat-mode)
            next-track (-> (playlist/set-current-song playlist 1)
                           (playlist/current-song))]
        (is (not (nil? next-track)))
        (is (not (same-song? (playlist/current-song playlist)
                             next-track)))))))

(deftest enqueue-last
  (testing "Should make sure the song is played last"
    (doseq [playback-mode '(:linear :shuffled)
            repeat-mode '(:repeat-none :repeat-all)]
      (let [length 5, queue (song-queue length)
            playlist (with-redefs [shuffle identity]
                       (playlist/->playlist queue :playback-mode playback-mode :repeat-mode repeat-mode))
            played-back (->> (iterate playlist/next-song playlist)
                             (take (dec length))
                             (map #(:id (playlist/current-song %)))
                             (set))
            to-enqueue (song)
            playlist' (playlist/enqueue-last playlist to-enqueue)]
        (is (nil? (played-back (-> (->> (iterate playlist/next-song playlist')
                                        (map playlist/current-song))
                                   (nth length)
                                   (:id))))
            (str "for " playback-mode ", " repeat-mode)))))
  (testing "Should not change the order of the songs already in queue"
    (doseq [playback-mode '(:linear :shuffled)
            repeat-mode '(:repeat-none :repeat-all)]
      (let [length 5, queue (song-queue length)
            playlist (playlist/->playlist queue :playback-mode playback-mode :repeat-mode repeat-mode)
            played-back-songs (fn played-back-songs [playlist]
                                (->> (iterate playlist/next-song playlist)
                                     (take length)
                                     (map playlist/current-song)
                                     (map :playlist/order)))
            played-back (played-back-songs playlist)
            played-back' (played-back-songs (playlist/enqueue-last playlist (song)))]
        (is (= played-back played-back')
            (str "for " playback-mode ", " repeat-mode))))))

(deftest enqueue-next
  (testing "Should play the song after the currently playing song"
    (doseq [playback-mode '(:linear :shuffled)
            repeat-mode '(:repeat-none :repeat-all)]
      (let [length 5, queue (song-queue length)
            playlist (playlist/->playlist queue :playback-mode playback-mode :repeat-mode repeat-mode)
            next-song (song)]
        (is (same-song? next-song (-> (playlist/enqueue-next playlist next-song)
                                      (playlist/next-song)
                                    (playlist/current-song))))))))
