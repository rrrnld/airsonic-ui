(ns airsonic-ui.components.library.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.config :as conf]
            [airsonic-ui.components.library.subs :as sub]))

(defn stub-albums [offset]
  (let [start (* offset conf/albums-per-page)
        end (inc (+ start (* conf/albums-per-page conf/albums-prefetch-factor)))]
    (range start end)))

(def responses {["getAlbumList2" {:type "recent" :offset 1}] {:album (stub-albums 1)}
                ["getAlbumList2" {:type "recent" :offset 2}] {:album (stub-albums 2)}
                ["getAlbumList2" {:type "recent" :offset 0}] {:album (stub-albums 0)}
                ;; vvv this one shouldn't show up in the test vvv
                ["getAlbumList2" {:type "newest" :offset 1}] {:album (reverse (stub-albums 1))}
                ["getAlbumList2" {:type "recent" :offset 3}] {:album (stub-albums 3)}})

(deftest complete-library
  (testing "Should concatenate and deduplicate all album list responses for a given type of list"
    ;; we test from offset 0 to 3, which is where these numbers come from
    (println "last number" (last (stub-albums 3)))

    (is (= (range 0 (inc (+ (* 3 conf/albums-per-page)
                            (* conf/albums-per-page conf/albums-prefetch-factor))))
           (sub/complete-library responses [:library/complete "recent"])))))
