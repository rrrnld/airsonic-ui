(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            ["react-sortable-hoc" :refer [SortableHandle]]
            [airsonic-ui.helpers :as helpers]
            [airsonic-ui.views.song :as song]
            [airsonic-ui.views.icon :refer [icon]]
            [airsonic-ui.components.sortable.views :as sortable]
            [airsonic-ui.routes :as routes]))

(def SortHandle
  (SortableHandle.
   ;; Alternative to r/reactify-component, which doens't convert props and hiccup,
   ;; is to just provide fn as component and use as-element or create-element
   ;; to return React elements from the component.
   (fn []
     (r/as-element [:span.is-size-7.has-text-grey-lighter
                          [icon :elevator]]))))

(defn song-actions []
  (let [controls-id (str "song-actions-" (random-uuid))
        is-active? (r/atom false)]
    (fn []
      [(if @is-active? :div.dropdown.is-right.is-active :div.dropdown.is-right)
       [:div.dropdown-trigger
        [:span.is-small.button {:aria-haspopup "true"
                                :aria-controls controls-id
                                :on-click #(swap! is-active? not)}
         [icon :ellipses]]]
       [:div.dropdown-menu {:id controls-id, :role "menu"}
        [:div.dropdown-content
         ;; TODO: Implement removal
         [:a.dropdown-item {:href "#"} "Remove from queue"]
         ;; TODO: Implement "Go to source"
         [:a.dropdown-item {:href "#"} "Go to source"]]]])))

(defn artist-link [{id :artistId, artist :artist}]
  (if id
    [:a {:href (routes/url-for ::routes/artist.detail {:id id})} artist]
    artist))

(defn song-table [{:keys [songs current-song]}]
  [:table.song-listing-table.table.is-fullwidth
   [sortable/sortable-component
    {:container [:tbody]
     :items songs

     :render-item
     (fn [{song :value}]
       [(if (= (:id song) (:id current-song)) :tr.is-playing :tr)
        [:td.sort-handle.is-narrow [:> SortHandle]]
        [:td.song-artist [artist-link song]]
        [:td.song-title (:title song)]
        [:td.meta>code (str (meta song))]
        [:td.song-duration (helpers/format-duration (:duration song) :brief? true)]
        [:td.song-actions.is-narrow [song-actions]]])

     :on-sort-end
     (fn [{:keys [old-idx new-idx]}]
       )}]])

(defn current-queue []
  [:section.section>div.container
   [:h1.title "Current Queue"]
   (let [current-queue @(subscribe [:audio/current-queue])
         current-song @(subscribe [:audio/current-song])]
     (if (some? current-queue)
       [song-table {:songs current-queue
                    :current-song current-song}]
       [:p "You are currently not playing anything. Use the search or go to your "
        [:a {:href (routes/url-for ::routes/library)} "Library"] " to start playing some music."]))])
