(ns airsonic-ui.test-helpers-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.test-helpers :refer [dispatches?]]))

(deftest dispatch-helper
  (testing "single dispatch"
    (is (false? (dispatches? {} :foo)))
    (is (true? (dispatches? {:dispatch [:foo 1 2 3]} :foo)))
    (is (false? (dispatches? {:dispatch [:foo 1 2 3]} :bar)))
    (is (true? (dispatches? {:dispatch [:foo 1 2 3]} [:foo 1 2 3])))
    (is (false? (dispatches? {:dispatch [:foo 1 2 3]} [:bar 2 3]))))
  (testing "multiple dispatch"
    (is (false? (dispatches? {:dispatch-n [[:bar]]} :foo)))
    (is (true? (dispatches? {:dispatch-n [[:foo 1 2 3]]} :foo)))
    (is (false? (dispatches? {:dispatch-n [[:foo 1 2 3]]} :bar)))
    (is (dispatches? {:dispatch-n [[:foo 1 2 3]]} [:foo 1 2 3]))
    (is (false? (dispatches? {:dispatch-n [[:foo 1 2 3]]} [:bar 2 3])))))
