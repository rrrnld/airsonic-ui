(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe]]
            [reagent.core :as reagent]
            ["react-sortable-hoc" :refer [SortableHandle]]
            [airsonic-ui.helpers :as helpers]
            [airsonic-ui.views.song :as song]
            [airsonic-ui.components.sortable.views :as sortable]
            [airsonic-ui.routes :as r]))

(defonce items (reagent/atom [1 2 3 4 5 6 7]))

(def SortHandle
  (SortableHandle.
   ;; Alternative to r/reactify-component, which doens't convert props and hiccup,
   ;; is to just provide fn as component and use as-element or create-element
   ;; to return React elements from the component.
   (fn []
     (reagent/as-element [:span {:style {:WebkitTouchCallout "none"
                                         :WebkitUserSelect "none"
                                         :KhtmlUserSelect "none"
                                         :MozUserSelect "none"
                                         ;; NOTE: lowercase "ms" prefix
                                         ;; https://www.andismith.com/blogs/2012/02/modernizr-prefixed/
                                         :msUserSelect "none"
                                         :userSelect "none"}}
                          "::"]))))

(defn current-queue []
  (let [is-sortable? (reagent/atom true)]
    (fn []
      [:section.section>div.container
       [:h1.title "Current Queue"]
       [sortable/sortable-component {:container [:table.table.is-fullwidth>tbody]
                                     :items @items

                                     :render-item
                                     (fn [{:keys [value]}]
                                       [:tr
                                        [:td "Some table cell"]
                                        [:td (str "Value: " value)]
                                        [:td [:a {:on-click #(swap! is-sortable? not)} (str "Is sortable: " @is-sortable?)]]
                                        [:td (when @is-sortable?
                                               [:> SortHandle])]])

                                     :on-sort-end
                                     (fn [{:keys [old-idx new-idx]}]
                                       (swap! items helpers/vector-move old-idx new-idx))}]
       (let [current-queue @(subscribe [:audio/current-queue])
             #_#_ current-song @(subscribe [:audio/current-song])]
         (if (some? current-queue)
           [song/listing (:items current-queue)]
           [:p "You are currently not playing anything. Use the search or go to your "
            [:a {:href (r/url-for ::r/library)} "Library"] " to start playing some music."]))])))
