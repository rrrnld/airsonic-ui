(ns bulma.dropdown.events
  (:require [re-frame.core :as rf]))

(defn show-dropdown [db [_ dropdown-id]]
  (assoc-in db [:bulma :visible-dropdown] dropdown-id))

(rf/reg-event-db ::show show-dropdown)

(defn hide-dropdown [db _]
  (update db :bulma dissoc :visible-dropdown))

(rf/reg-event-db ::hide hide-dropdown)

(defn toggle-dropdown [db [_ dropdown-id]]
  (let [visible-dropdown (get-in db [:bulma :visible-dropdown])]
    (if (= visible-dropdown dropdown-id)
      (hide-dropdown db [::hide])
      (show-dropdown db [::show dropdown-id]))))

(rf/reg-event-db ::toggle toggle-dropdown)
