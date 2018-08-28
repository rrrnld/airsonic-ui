(ns airsonic-ui.helpers-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.helpers :as helpers]))

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

(deftest add-classes
  (testing "Should add classes to a simple hiccup keyword"
    (is (= :div.foo (helpers/add-classes :div :foo)))
    (is (= :div.bar.bar (helpers/add-classes :div.bar :bar)))
    (is (= :div.foo.bar (helpers/add-classes :div.foo :bar))))
  (testing "Should add classes to the innermost child of a nested hiccup element"
    (is (= :p>input.input (helpers/add-classes :p>input :input)))
    (is (= :div.field>p>input.input.has-background-red (helpers/add-classes :div.field>p>input.input :has-background-red)))))
