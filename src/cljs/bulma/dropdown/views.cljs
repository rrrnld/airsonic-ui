(ns bulma.dropdown.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [bulma.icon :refer [icon]]
            [bulma.dropdown.events :as ev]
            [bulma.dropdown.subs :as sub]))

;; there's "muted-dispatch" in airsonic-ui.helpers which does the same thing
;; it's not used here because all the bulma.*-components should work independently

(defn muted-dispatch [event-vector]
  (fn [e]
    (.preventDefault e)
    (dispatch event-vector)))

(defn generate-id []
  (str "bulma-dropdown-" (random-uuid)))

(defn click-overlay
  []
  [:div {:style {:position "fixed"
                 :z-index 19 ;; <- 20 is the z-index of .dropdown-menu
                 :top 0
                 :left 0
                 :bottom 0
                 :right 0}
         :on-click #(dispatch [::ev/hide])}])

(defn dropdown [{:keys [items]}]
  (let [dropdown-id (generate-id)]
    (fn []
      (let [visible? @(subscribe [::sub/visible? dropdown-id])]
        [(if visible? :div.dropdown.is-right.is-active :div.dropdown.is-right)
         (when visible? [click-overlay])
         [:div.dropdown-trigger
          [:span.is-small.button {:aria-haspopup "true"
                                  :aria-controls dropdown-id
                                  :on-click #(dispatch [::ev/toggle dropdown-id])}
           [icon :ellipses]]]
         [:div.dropdown-menu {:id dropdown-id, :role "menu"}
          [:div.dropdown-content
           (for [[idx {:keys [label event]}] (map-indexed vector items)]
             ^{:key (str dropdown-id "-" idx)}
             [:a.dropdown-item {:href "#"
                                :on-click (muted-dispatch event)} label])]]]))))
