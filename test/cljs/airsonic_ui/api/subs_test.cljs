(ns airsonic-ui.api.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.api.subs :as sub]))

(enable-console-print!)

(deftest single-response
  (testing "Should return the response for a specified endpoint"
    (let [responses (sub/responses {:api/responses {["search2" {:query "query term"}] :result}} [:api/responses])]
      (is (= :result (sub/response-for responses [:api/response-for "search2" {:query "query term"}])))
      (is (nil? (sub/response-for responses [:api/response-for "search2" {:query "another query term"}]))))))

(deftest endpoint-keywordification
  (testing "Should strip prefixes"
    (is (= :artist-info (sub/endpoint->kw "getArtistInfo")))
    (is (= :jukebox-control (sub/endpoint->kw "jukeboxControl"))))
  (testing "Should strip trailing numbers"
    (is (= :album-list (sub/endpoint->kw "getAlbumList2")))
    (is (= :search (sub/endpoint->kw "search3")))))

(def responses {["getAlbumList2" {:type "recent" :size 18}]
                {:album [{:genre "foo", :artistId "12345"}
                         {:genre "electronic", :artistId "9999"}]}

                ["getArtistInfo" {:id "128"}]
                {:biography "Interesting bio"
                 :largeImageUrl "https://lastfm-img2.akamaized.net/i/u/300x300/fb416b59cd694587aca0b2dec8f41198.png"}})

(deftest responses-for-route
  (testing "Should return all cached responses for a route"
    (let [current-route-events [[:api/request "getAlbumList2" {:type "recent", :size 18}]
                                [:event/should-be-ignored]
                                [:api/request "getArtistInfo" {:id "128"}]]]
      (is (= {:album-list (get responses ["getAlbumList2" {:type "recent" :size 18}])
              :artist-info (get responses ["getArtistInfo" {:id "128"}])}
             (sub/current-route-data [responses current-route-events]
                                     [:api/current-route-data]))))))

(deftest content-pending
  (testing "Should indicate if there are outstanding requests for the current route"
    (let [current-route-events [[:api/request "getAlbumList2" {:type "recent", :size 18}]
                                [:event/should-be-ignored]
                                [:api/request "getArtistInfo" {:id "128"}]]
          done responses
          in-progress (assoc-in responses
                                [["getAlbumList2" {:type "recent" :size 18}] :api/is-loading?]
                                true)]
          (is (true? (-> (sub/current-route-data [in-progress current-route-events]
                                                 [:api/current-route-data])
                         (sub/content-pending? [:api/content-pending?]))))
          (is (not (true? (-> (sub/current-route-data [done current-route-events]
                                                      [:api/current-route-data])
                              (sub/content-pending? [:api/content-pending?]))))))))
