(ns bulma.dropdown-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bulma.dropdown.subs :as sub]
            [bulma.dropdown.events :as ev]))

;; NOTE: Here as well; this code is very much like the modal code
;; Not sure whether to explicitly duplicate it or provide some smarter
;; abstraction that's harder to understand at first sight

(enable-console-print!)

(deftest bulma-dropdowns
  (testing "Should create a collection of dropdowns if there is none"
    (let [new-db (ev/show-dropdown {} [::ev/show :some-dropdown-id])]
      (is (= :some-dropdown-id (sub/visible-dropdown new-db [::sub/visible-dropdown])))))
  (testing "Should hide other dropdowns when displaying a new one"
    (let [dropdown-ids [:some-id-1 :some-id-2 :some-id-3]
          new-db (reduce (fn [db dropdown-id]
                           (ev/show-dropdown db [::ev/show dropdown-id]))
                         {} dropdown-ids)]
      (is (= :some-id-3 (sub/visible-dropdown new-db [::sub/visible-dropdown])))))
  (testing "Should remove a dropdown from the collection when we hide it"
    (let [dropdown-ids [:some-id-1 :some-id-2 :some-id-3]
          new-db (-> (reduce (fn [db dropdown-id]
                               (ev/show-dropdown db [::ev/show dropdown-id]))
                             {} dropdown-ids)
                     (ev/hide-dropdown [::ev/hide]))]
      (is (not (some? (sub/visible-dropdown new-db [::sub/visible-dropdown]))))))
  (testing "Should tell us about the visibility of a dropdown with a predicate"
    (is (true? (-> (ev/show-dropdown {} [::ev/show :getting-repetitive])
                   (sub/visible-dropdown [::sub/visible-dropdown])
                   (sub/visible? [::sub/visible? :getting-repetitive])))))
  (testing "Dropdown toggling"
    (is (true? (-> (ev/toggle-dropdown {} [::ev/toggle :some-generic-dropdown])
                   (sub/visible-dropdown [::sub/visible-dropdown])
                   (sub/visible? [::sub/visible? :some-generic-dropdown]))))
    (is (not (true? (-> (ev/toggle-dropdown {} [::ev/toggle :some-generic-dropdown])
                        (ev/toggle-dropdown [::ev/toggle :some-generic-dropdown])
                        (sub/visible-dropdown [::sub/visible-dropdown])
                        (sub/visible? [::sub/visible? :some-generic-dropdown])))))))
