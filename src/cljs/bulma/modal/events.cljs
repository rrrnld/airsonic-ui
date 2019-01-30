(ns bulma.modal.events
  (:require [re-frame.core :as rf]))

(defn show-modal [db [_ modal-id]]
  (assoc-in db [:bulma :visible-modal] modal-id))

(rf/reg-event-db ::show show-modal)

(defn hide-modal [db _]
  (update db :bulma dissoc :visible-modal))

(rf/reg-event-db ::hide hide-modal)

(defn toggle-modal [db [_ modal-id]]
  (let [visible-modal (get-in db [:bulma :visible-modal])]
    (if (= visible-modal modal-id)
      (hide-modal db [::hide])
      (show-modal db [::show modal-id]))))

(rf/reg-event-db ::toggle toggle-modal)
