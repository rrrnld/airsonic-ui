(ns airsonic-ui.utils.helpers-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.utils.helpers :as helpers]))

(deftest find-where
  (testing "Finds the correct item and index"
    (is (= [0 1] (helpers/find-where (partial = 1) (range 1 10))))
    (is (= [2 {:foo true, :bar false}] (helpers/find-where :foo '({}
                                                                  {:foo false
                                                                   :bar true}
                                                                  {:foo true
                                                                   :bar false})))))
  (testing "Returns nil when nothing is found"
    (is (nil? (helpers/find-where (partial = 2) (range 2))))))
