(ns airsonic-ui.subs-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.api.helpers :as api]
            [airsonic-ui.subs :as subs]))

(deftest booting
  (let [route [:some-route nil nil]
        verified-credentials (assoc fixtures/credentials :verified? true)
        is-booting? (fn is-booting? [db]
                      (subs/is-booting? db [:subs/is-booting?]))]
    (testing "Should be false when we don't have previous credentials"
      (is (not (is-booting? {:routes/current-route route})))
      (is (not (is-booting? {:routes/current-route route
                             :credentials {}}))) )
    (testing "Should be true when we have unverified credentials"
      (is (true? (is-booting? {:routes/current-route route
                               :credentials fixtures/credentials}))))
    (testing "Should be false when we have verified credentials"
      (is (not (is-booting? {:routes/current-route route
                             :credentials verified-credentials}))))
    (testing "Should be true when routing is not yet set up"
      (is (true? (is-booting? {:routes/current-route nil
                               :credentials verified-credentials}))))))

(deftest cover-images
  (let [credentials {:server "https://foo.bar"
                     :u "test-user"
                     :p "some-random-password"}]
    (testing "Should give the correct path once the credentials are set"
      (is (= (api/cover-url (:server credentials)
                            (select-keys credentials [:u :p])
                            fixtures/song
                            48)
             (subs/cover-url [credentials] [:subs/cover-image fixtures/song 48]))))))
