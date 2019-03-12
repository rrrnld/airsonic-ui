(ns bulma.dropdown.subs
  (:require [re-frame.core :as rf]))

;; NOTE: This is almost the same as bulma.modal.subs
;; Maybe we can provide some abstraction that covers both, but maybe we shouldn't

(defn visible-dropdown
  "Gives us the ID of the currently visible dropdown"
  [db _]
  (get-in db [:bulma :visible-dropdown]))

(rf/reg-sub ::visible-dropdown visible-dropdown)

(defn visible?
  "Predicate to check the visibility of a single modal"
  [visible-dropdown [_ dropdown-id]]
  (= visible-dropdown dropdown-id))

(rf/reg-sub
 ::visible?
 :<- [::visible-dropdown]
 visible?)
