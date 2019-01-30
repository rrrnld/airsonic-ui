(ns airsonic-ui.components.keyboard-shortcuts.views
  (:require [bulma.modal.views :as bulma]
            [airsonic-ui.components.keyboard-shortcuts.config :as config]))

(defn help-modal []
  [bulma/modal-card {:title "Keyboard Shortcuts"
                     :modal-id :keyboard-shortcuts-help}
   [:table.table.is-hoverable.is-fullwidth
    [:thead [:tr [:th "Key"] [:th "Function"]]]
    [:tbody
     (for [[idx [k desc]] (map-indexed vector config/keymap)]
       ^{:key idx} [:tr [:td>code k] [:td desc]])]]])
