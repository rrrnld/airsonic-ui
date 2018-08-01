(ns airsonic-ui.routes-test
  (:require [airsonic-ui.routes :as routes]
            [cljs.test :refer [deftest testing is]]))

(def fixtures
  {:default [::route {:some :data} {:some-more true}]})

#_(deftest permission-checking
    (testing "Should succeed for unprotected routes"
      (testing "without credentials")
      (testing "with unverified credentials"))
    (testing "Should fail for protected routes"
      (testing "without credentials")
      (testing "with unverified credentials"))
    (testing "Should succeed for protected routes with verified credentials"))

(deftest route-encoding
  (testing "Should return a string with hash-compatible characters"
    (let [encoded (routes/encode-route (:default fixtures))]
      (is (string? encoded))
      (is (re-matches #"^[^#?&=]+$" encoded))))
  (testing "Should be bijective"
    (is (= (:default fixtures) (routes/decode-route (routes/encode-route (:default fixtures)))))))
