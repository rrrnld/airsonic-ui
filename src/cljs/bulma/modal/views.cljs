(ns bulma.modal.views
  (:require [re-frame.core :as rf]
            [bulma.modal.events :as ev]
            [bulma.modal.subs :as sub]))

(defn hide-modal [_]
  (rf/dispatch [::ev/hide]))

(defn modal
  "Generic modal; arguments:

  options:
    {:has-hide-button? boolean
    :modal-id :some-identifier}

  & children"
  [{:keys [has-hide-button? modal-id]} & children]
  {:pre [(some? modal-id)]}
  (let [visible? @(rf/subscribe [::sub/visible? modal-id])
        modal-tag (if visible? :div.modal.is-active :div.modal)]
    [modal-tag
     [:div.modal-background {:on-click hide-modal}]
     (into [:div.modal-content] children)
     (when has-hide-button?
       [:button.modal-hide.is-large {:aria-label "hide"
                                     :on-click hide-modal}])]))

(defn modal-card
  "A card modal that renders content on a background. Arguments:

  options:
  {:title \"Title of the card\"
   :foot [[:div \"An array of hiccup elements\"]]
   :modal-id :some-identifier}

  & children"
  [{:keys [title foot modal-id]} & children]
  [modal {:has-hide-button? (not (some? title))
          :modal-id modal-id}
   (when title
     [:div.modal-card-head
      [:p.modal-card-title title]
      [:button.delete {:aria-label "hide"
                       :on-click hide-modal}]])
   (into [:section.modal-card-body] children)
   (when foot
     (into [:div.modal-card-foot] foot))])
