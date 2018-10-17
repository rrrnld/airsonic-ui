(ns airsonic-ui.components.library.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.components.library.subs :as sub]))

(def responses {["getAlbumList2" {:type "recent" :offset 1}] {:album '(5 6 7 8)}
                ["getAlbumList2" {:type "recent" :offset 0}] {:album '(1 2 3 4)}
                ["getAlbumList2" {:type "newest" :offset 1}] {:album '(9 8 7 6)}})

(deftest complete-library
  (testing "Should concatenate all album list responses for a given type of list"
    (is (= '(1 2 3 4 5 6 7 8)
           (sub/complete-library responses [:library/complete "recent"])))))
