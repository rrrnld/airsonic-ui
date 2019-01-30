(ns bulma.modal-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bulma.modal.subs :as sub]
            [bulma.modal.events :as ev]))

(enable-console-print!)

(deftest bulma-modals
  (testing "Should create a collection of modals if there is none"
    (let [new-db (ev/show-modal {} [::ev/show :some-modal-id])]
      (is (= :some-modal-id (sub/visible-modal new-db [::sub/visible-modal])))))
  (testing "Should hide other modals when displaying a new one"
    (let [modal-ids [:some-id-1 :some-id-2 :some-id-3]
          new-db (reduce (fn [db modal-id]
                           (ev/show-modal db [::ev/show modal-id]))
                         {} modal-ids)]
      (is (= :some-id-3 (sub/visible-modal new-db [::sub/visible-modal])))))
  (testing "Should remove a modal from the collection when we hide it"
    (let [modal-ids [:some-id-1 :some-id-2 :some-id-3]
          new-db (-> (reduce (fn [db modal-id]
                               (ev/show-modal db [::ev/show modal-id]))
                             {} modal-ids)
                     (ev/hide-modal [::ev/hide]))]
      (is (not (some? (sub/visible-modal new-db [::sub/visible-modal]))))))
  (testing "Should tell us about the visibility of a modal with a predicate"
    (is (true? (-> (ev/show-modal {} [::ev/show :getting-repetitive])
                   (sub/visible-modal [::sub/visible-modal])
                   (sub/visible? [::sub/visible? :getting-repetitive])))))
  (testing "Modal toggling"
    (is (true? (-> (ev/toggle-modal {} [::ev/toggle :some-generic-modal])
                   (sub/visible-modal [::sub/visible-modal])
                   (sub/visible? [::sub/visible? :some-generic-modal]))))
    (is (not (true? (-> (ev/toggle-modal {} [::ev/toggle :some-generic-modal])
                        (ev/toggle-modal [::ev/toggle :some-generic-modal])
                        (sub/visible-modal [::sub/visible-modal])
                        (sub/visible? [::sub/visible? :some-generic-modal])))))))
