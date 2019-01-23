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

(deftest kebabify
  (testing "Should turn camelCased and PascalCased strings into kebab-cased keywords"
    (is (= :hello-world (helpers/kebabify "HelloWorld")))
    (is (= :how-are-you (helpers/kebabify "howAreYou")))
    (is (= :foobar (helpers/kebabify "foobar"))))
  (testing "Should kebab-case camelCased and PascalCased keywords"
    (is (= :hello-world (helpers/kebabify :HelloWorld)))
    (is (= :how-are-you (helpers/kebabify :howAreYou)))
    (is (= :foobar (helpers/kebabify :foobar)))))

(deftest format-duration
  (testing "Should format hours, minutes and seconds"
    (is (= "1h" (helpers/format-duration 3600)))
    (is (= "59m" (helpers/format-duration (* 59 60))))
    (is (= "1m" (helpers/format-duration 60)))
    (is (= "5s" (helpers/format-duration 5))))
  (testing "Should respect the :brief? option"
    (is (= "01:00:00" (helpers/format-duration 3600 :brief? true)))
    (is (= "59:00" (helpers/format-duration (* 59 60) :brief? true)))
    (is (= "01:00" (helpers/format-duration 60 :brief? true)))
    (is (= "00:05" (helpers/format-duration 5 :brief? true)))))
