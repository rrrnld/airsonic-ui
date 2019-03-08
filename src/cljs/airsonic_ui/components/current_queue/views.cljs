(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            ["react-sortable-hoc" :refer [SortableHandle]]
            [airsonic-ui.helpers :as helpers]
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

(defn song-link [song idx]
  [:a
   {:href "#"
    :on-click (helpers/muted-dispatch [:audio-player/set-current-song idx])}
   (:title song)])

(defn song-table [{:keys [songs current-song]}]
  [:table.song-listing-table.table.is-fullwidth
   [:thead>tr
    [:td.is-narrow]
    [:td.song-artist "Artist"]
    [:td.song-title "Title"]
    [:td.song-duration "Duration"]
    [:td.is-narrow]]
   [sortable/sortable-component
    {:items songs
     :container [:tbody]
     :helper-class "sortable-is-moving"

     :render-item
     (fn [{[idx song] :value}]
       [(if (= (:id song) (:id current-song)) :tr.is-playing :tr)
        [:td.sortable-handle.is-narrow [:> SortHandle]]
        [:td.song-artist [artist-link song]]
        [:td.song-title [song-link song idx]]
        [:td.song-duration (helpers/format-duration (:duration song) :brief? true)]
        [:td.song-actions.is-narrow [song-actions]]])

     :on-sort-end
     (fn [{:keys [old-idx new-idx]}]
       (println "moving from" old-idx "to" new-idx)
       (dispatch [:audio-player/move-song old-idx new-idx]))}]])

(defn current-queue []
  [:section.section>div.container
   [:h1.title "Current Queue"]
   (let [current-playlist @(subscribe [:audio/current-playlist])
         current-song @(subscribe [:audio/current-song])]
     (if (empty? current-playlist)
       [:p "You are currently not playing anything. Use the search or go to your "
        [:a {:href (routes/url-for ::routes/library)} "Library"] " to start playing some music."]
       [song-table {:songs (:items current-playlist)
                    :current-song current-song}]))])
