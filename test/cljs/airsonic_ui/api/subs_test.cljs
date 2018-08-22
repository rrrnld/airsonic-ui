(ns airsonic-ui.api.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.api.subs :as sub]))

(enable-console-print!)

(deftest endpoint-keywordification
  (testing "Should strip prefixes"
    (is (= :artist-info (sub/endpoint->kw "getArtistInfo")))
    (is (= :jukebox-control (sub/endpoint->kw "jukeboxControl"))))
  (testing "Should strip trailing numbers"
    (is (= :album-list (sub/endpoint->kw "getAlbumList2")))
    (is (= :search (sub/endpoint->kw "search3")))))

(deftest responses-for-route
  (testing "Should return all cached responses for a route"
    (let [route-events [[:api/request "getAlbumList2" {:type "recent", :size 18}]
                        [:event/should-be-ignored]
                        [:api/request "getArtistInfo" {:id "128"}]]
          db {:api/responses {["getAlbumList2" {:type "recent" :size 18}]
                              {:album [{:genre "foo", :artistId "12345"}
                                       {:genre "electronic", :artistId "9999"}]}

                              ["getArtistInfo" {:id "128"}]
                              {:biography "Interesting bio"
                               :largeImageUrl "https://lastfm-img2.akamaized.net/i/u/300x300/fb416b59cd694587aca0b2dec8f41198.png"}}}]
      (is (= {:album-list (get-in db [:api/responses ["getAlbumList2" {:type "recent" :size 18}]])
              :artist-info (get-in db [:api/responses ["getArtistInfo" {:id "128"}]])}
             (sub/route-data db [:api/route-data route-events]))))))
