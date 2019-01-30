(ns bulma.modal.subs
  (:require [re-frame.core :as rf]))

(defn visible-modal
  "Gives us the ID of the currently visible modal"
  [db _]
  (get-in db [:bulma :visible-modal]))

(rf/reg-sub ::visible-modal visible-modal)

(defn visible?
  "Predicate to check the visibility of a single modal"
  [visible-modal [_ modal-id]]
  (= visible-modal modal-id))

(rf/reg-sub
 ::visible?
 :<- [::visible-modal]
 visible?)
