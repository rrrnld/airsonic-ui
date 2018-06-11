(ns airsonic-ui.views.notifications
  (:require [re-frame.core :refer [dispatch]]))

;; user notifications

(defn notification-list [notifications]
  [:div.notifications
   (for [[id notification] notifications]
     (let [class (case (:level notification)
                   :error "danger"
                   "info")]
       ^{:key id} [:div {:class-name (str "notification is-small is-" class)}
                   [:button.delete {:on-click #(dispatch [:notification/hide id])}]
                   (:message notification)]))])
