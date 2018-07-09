(ns airsonic-ui.routes-test
  (:require [airsonic-ui.routes :as routes]
            [cljs.test :refer [deftest testing is]]))

(def fixtures
  {:default [::route {:some :data} {:some-more true}]})

(deftest route-encoding
  (testing "Should return a string with hash-compatible characters"
    (let [encoded (routes/encode-route (:default fixtures))]
      (is (string? encoded))
      (is (re-matches #"^[^#?&=]+$" encoded))))
  (testing "Should be bijective"
    (is (= (:default fixtures) (routes/decode-route (routes/encode-route (:default fixtures)))))))
