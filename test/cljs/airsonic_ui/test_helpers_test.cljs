(ns airsonic-ui.test-helpers-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.test-helpers :as h]))

(deftest dispatch-helper
  (testing "Should identify singly dispatched events"
    (is (false? (h/dispatches? {} :foo)))
    (is (true? (h/dispatches? {:dispatch [:foo 1 2 3]} :foo)))
    (is (false? (h/dispatches? {:dispatch [:foo 1 2 3]} :bar)))
    (is (true? (h/dispatches? {:dispatch [:foo 1 2 3]} [:foo 1 2 3])))
    (is (false? (h/dispatches? {:dispatch [:foo 1 2 3]} [:bar 2 3]))))
  (testing "Should identify an event along multiple dispatched events"
    (is (false? (h/dispatches? {:dispatch-n [[:bar]]} :foo)))
    (is (true? (h/dispatches? {:dispatch-n [[:foo 1 2 3]]} :foo)))
    (is (false? (h/dispatches? {:dispatch-n [[:foo 1 2 3]]} :bar)))
    (is (h/dispatches? {:dispatch-n [[:foo 1 2 3]]} [:foo 1 2 3]))
    (is (false? (h/dispatches? {:dispatch-n [[:foo 1 2 3]]} [:bar 2 3])))))

(deftest rand-str
  (testing "Generates strings"
    (is (string? (h/rand-str)))
    (is (string? (h/rand-str 20))))
  (testing "Should respect the length for even lengths"
    (is (= 124 (count (h/rand-str 124))))))
