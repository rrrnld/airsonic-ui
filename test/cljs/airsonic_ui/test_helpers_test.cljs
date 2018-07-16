(ns airsonic-ui.test-helpers-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.test-helpers :refer [dispatches?]]))

(deftest dispatch-helper
  (testing "single dispatch"
    (is (not (dispatches? {} :foo)))
    (is (dispatches? {:dispatch [:foo 1 2 3]} :foo))
    (is (not (dispatches? {:dispatch [:foo 1 2 3]} :bar)))
    (is (dispatches? {:dispatch [:foo 1 2 3]} [:foo 1 2 3]))
    (is (not (dispatches? {:dispatch [:foo 1 2 3]} [:bar 2 3]))))
  (testing "multiple dispatch"
    (is (not (dispatches? {:dispatch-n [[:bar]]} :foo)))
    (is (dispatches? {:dispatch-n [[:foo 1 2 3]]} :foo))
    (is (not (dispatches? {:dispatch-n [[:foo 1 2 3]]} :bar)))
    (is (dispatches? {:dispatch-n [[:foo 1 2 3]]} [:foo 1 2 3]))
    (is (not (dispatches? {:dispatch-n [[:foo 1 2 3]]} [:bar 2 3])))))
