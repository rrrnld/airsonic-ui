(ns airsonic-ui.api.events-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [airsonic-ui.api.events :as events]
            [airsonic-ui.fixtures :as fixtures]))

(enable-console-print!)

(deftest api-failure-notifcations
  (testing "Should show an error notification when airsonic responds with an error"
    (let [fx (events/good-api-response {} [:api/good-response "ping" nil (:error fixtures/responses)])
          ev (:dispatch fx)]
      (is (= :notification/show (first ev)))
      (is (= :error (second ev))))))

(deftest cached-api-requests
  (letfn [(cache [fx [endpoint params]]
            (get-in fx [:db :api/responses [endpoint params]]))]
    (testing "Should be cached"
      (testing "when the response was successful"
        (let [endpoint "getScanStatus"
              successful (events/good-api-response {} [:api/good-response endpoint nil (:ok fixtures/responses)])
              unsuccessful (events/good-api-response {} [:api/good-response endpoint nil (:error fixtures/responses)])]
          (is (map? (cache successful [endpoint])))
          (is (nil? (cache unsuccessful [endpoint])))))
      (testing "in an unwrapped format"
        (let [endpoint "getScanStatus"
              fx (events/good-api-response {} [:api/good-response endpoint nil (:ok fixtures/responses)])]
          (is (= #{:count :scanning} (set (keys (cache fx [endpoint]))))))))
    (testing "When being issued"
      (let [endpoint "getScanStatus"
            fx (events/api-request {:db {:credentials (select-keys fixtures/credentials [:server])}}
                                   [:api/request endpoint])]
        (testing "should send an http request"
          (is (contains? fx :http-xhrio)))
        (testing "should indicate that a request is ongoing"
          (is (true? (:api/is-loading? (cache fx [endpoint]))) "for non-cached responses")
          (is (true? (-> (events/good-api-response fx [:api/good-response endpoint nil (:ok fixtures/responses)])
                         (events/api-request [:api/request endpoint])
                         (cache [endpoint])
                         :api/is-loading?)) "for cached responses"))
        (testing "should remove the indication that a request is ongoing when there is a response"
          (is (not (:api/is-loading? (-> (events/good-api-response fx [:api/good-response endpoint nil (:ok fixtures/responses)])
                                         (cache [endpoint])))) "for a good response")
          (is (not (:api/is-loading? (-> (merge fx (events/good-api-response fx [:api/good-response endpoint nil (:error fixtures/responses)]))
                                         (cache [endpoint])))) "when an error is returned")
          (is (not (:api/is-loading? (-> (merge fx (events/failed-api-response fx [:api/failed-response endpoint]))
                                         (cache [endpoint])))) "when communication with the server failed"))))
    (testing "Should be able to avoid the cache"
      ;; FIXME: Implement this
      )))
