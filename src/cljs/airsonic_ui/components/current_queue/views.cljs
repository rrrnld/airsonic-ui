(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe dispatch-sync]]
            [reagent.core :as r]
            ["react-sortable-hoc" :refer [SortableHandle]]
            [bulma.icon :refer [icon]]
            [bulma.dropdown.views :refer [dropdown]]
            [airsonic-ui.helpers :as helpers]
            [airsonic-ui.components.collection.views :as collection]
            [airsonic-ui.components.sortable.views :as sortable]
            [airsonic-ui.routes :as routes]

            ;; ↓ registers subscription handlers ↓
            [airsonic-ui.components.current-queue.subs]))

(def SortHandle
  (SortableHandle.
   ;; Alternative to r/reactify-component, which doens't convert props and hiccup,
   ;; is to just provide fn as component and use as-element or create-element
   ;; to return React elements from the component.
   (fn []
     (r/as-element [:span.is-size-7.has-text-grey-lighter
                    [icon :elevator]]))))

(defn song-actions [{:keys [song idx]}]
  ;; TODO: Implement both of these
  [dropdown {:items [{:label "Remove from queue"
                      :event [:audio-player/remove-song idx]}
                     {:label "Go to source"
                      :event []}]}])

(defn artist-link [{id :artistId, artist :artist}]
  (if id
    [:a {:href (routes/url-for ::routes/artist.detail {:id id})} artist]
    artist))

(defn song-link [song idx]
  [:a
   {:href "#"
    :on-click (helpers/muted-dispatch [:audio-player/set-current-song idx])}
   (:title song)])

(defn song-table-head []
  [:thead>tr
   [:td.is-narrow]
   [:td.song-artist "Artist"]
   [:td.song-title "Title"]
   [:td.song-duration "Duration"]
   [:td.song-actions.is-narrow]])

(defn song-table-sortable-tbdoy [{:keys [songs current-song-idx]}]
  ;; we need this closure to pass in custom arguments (current-song-idx)
  (fn []
    [sortable/sortable-component
     {:items songs
      :container [:tbody]
      :helper-class "sortable-is-moving"

      :render-item
      (fn [{[idx song] :value}]
        [(if (= idx current-song-idx) :tr.is-playing :tr)
         [:td.sortable-handle.is-narrow [:> SortHandle]]
         [:td.song-artist [artist-link song]]
         [:td.song-title [song-link song idx]]
         [:td.song-duration (helpers/format-duration (:duration song) :brief? true)]
         [:td.song-actions.is-narrow [song-actions {:song song
                                                    :idx idx}]]])

      :on-sort-end
      (fn [{:keys [old-idx new-idx]}]
        ;; if we don't dispatch-sync, the UI sometimes places the row back and
        ;; resorts it a litle later
        (dispatch-sync [:audio-player/move-song old-idx new-idx]))}]))

(defn song-table [{:keys [songs current-song-idx]}]
  [collection/song-table
   {:songs songs
    :thead song-table-head
    :tbody (song-table-sortable-tbdoy {:songs songs
                                       :current-song-idx current-song-idx})}])

(defn queue-info [{:keys [playlist-info]}]
  [:ul.is-smaller.collection-info
   [:li [icon :audio-spectrum] (str (:count playlist-info)
                                    (if (pos? (:count playlist-info))
                                      " tracks"
                                      " track"))]
   [:li [icon :clock] (helpers/format-duration (:duration playlist-info))]])

(defn playlist [props]
  [:div
   [queue-info props]
   [song-table {:songs (get-in props [:current-playlist :items])
                :current-song-idx (get-in props [:current-playlist :current-idx])}]])

(defn empty-playlist []
  [:p "You are currently not playing anything. Use the search or go to your "
   [:a {:href (routes/url-for ::routes/library)} "Library"] " to start playing some music."])

(defn current-queue []
  (let [current-playlist @(subscribe [:audio/current-playlist])
        playlist-info @(subscribe [:current-queue/info])]
    [:section.section>div.container
     [:h1.title "Current Queue"]
     (if (empty? current-playlist)
       [empty-playlist]
       [playlist {:current-playlist current-playlist
                  :playlist-info playlist-info}])]))
