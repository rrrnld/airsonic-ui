(ns airsonic-ui.components.library.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.config :as conf]
            [airsonic-ui.components.library.fixtures :as fixtures]
            [airsonic-ui.components.library.subs :as sub]))

(deftest partition-library
  (testing "Should give us a map of page -> content"
    (let [pages (sub/partition-responses "recent" fixtures/responses)]
      (is (map? pages))
      (is (every? int? (keys pages)))
      (is (every? seq? (vals pages)))))
  (testing "Should map each response correctly to a page"
    (let [first-response (select-keys fixtures/responses [["getAlbumList2" {:type "recent", :size 100, :offset 0}]])]
      (is (= (range 5) (keys (sub/partition-responses "recent" first-response)))))
    (let [first-and-third (select-keys fixtures/responses [["getAlbumList2" {:type "recent", :size 100, :offset 0}]
                                                           ["getAlbumList2" {:type "recent", :size 100, :offset 40}]])]
      ;; there will be overlapping content for pages 2, 3 and 4 (with a zero-based index)
      (is (= (range 7) (keys (sub/partition-responses "recent" first-and-third)))))))

(deftest paginated-library
  (testing "Should humanize page offsets"
    (let [responses (select-keys fixtures/responses [["getAlbumList2" {:type "recent", :size 100, :offset 0}]])
          paginated (sub/paginated-library responses [:sub/paginated-library "recent"])]
      (is (= [1 2 3 4 5] (keys paginated)))))
  (testing "Should concatenate and deduplicate all album list responses"
    (let [responses (select-keys fixtures/responses [["getAlbumList2" {:type "recent", :size 100, :offset 0}]
                                                     ["getAlbumList2" {:type "recent", :size 100, :offset 20}]
                                                     ["getAlbumList2" {:type "recent", :size 100, :offset 40}]])
          paginated (sub/paginated-library responses [:sub/paginated-library "recent"])]
      (is (= [1 2 3 4 5 6 7] (keys paginated)))
      (is (= 140 (count (mapcat val paginated))))
      (is (= 140 (count (set (mapcat val paginated))))))))
