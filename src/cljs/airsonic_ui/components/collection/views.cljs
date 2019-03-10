(ns airsonic-ui.components.collection.views
  "A collection is a list of audio files that belong together (e.g. an album or
  a podcast's overview)"
  (:require [re-frame.core :refer [subscribe]]
            [bulma.icon :refer [icon]]
            [bulma.dropdown.views :refer [dropdown]]
            [airsonic-ui.helpers :as h]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.views.cover :refer [cover card]]))

(defn collection-info [{:keys [songCount duration year]}]
  (vec (cond-> [:ul.is-smaller.collection-info
                [:li [icon :audio-spectrum] (str songCount (if (= 1 songCount)
                                                             " track" " tracks"))]
                [:li [icon :clock] (h/format-duration duration)]]
         year (conj [:li [icon :calendar] (str "Released in " year)]))))

;; TODO: Maybe this view belongs somewhere else?
;; Something like a collection-grid component?

(defn album-card
  "A single element in a grid of albums. Shows the cover, artist and album name."
  [{:keys [artist artistId name id] :as album}]
  [card album
   :url-fn #(routes/url-for ::routes/album.detail {:id id})
   :content [:div
             ;; link to album
             [:div.title.is-5
              [:a {:href (routes/url-for ::routes/album.detail {:id id})
                   :title name} name]]
             ;; link to artist page
             [:div.subtitle.is-6 [:a {:href (routes/url-for ::routes/artist.detail {:id artistId})
                                      :title artist} artist]]]])

(defn listing [albums]
  ;; always show 5 in a row
  [:div.columns.is-multiline.is-mobile
   (for [[idx album] (map-indexed vector albums)]
     ^{:key idx} [:div.column.is-one-fifth-desktop.is-one-quarter-tablet.is-half-mobile
                  [album-card album]])])

;; TODO: Avoid duplication
(defn artist-link [{id :artistId, artist :artist}]
  (if id
    [:a {:href (routes/url-for ::routes/artist.detail {:id id})} artist]
    artist))

(defn song-link [{:keys [song album idx]}]
  [:a
   {:href "#" :on-click (h/muted-dispatch [:audio-player/play-all (:song album) idx] :sync? true)}
   (:title song)])

(defn song-actions [{:keys [song album idx]}]
  [dropdown {:items [{:label "Play next" :event [:audio-player/enqueue-next song]}
                     {:label "Play last" :event [:audio-player/enqueue-last song]}]}])

(defn song-table [{:keys [album]}]
  ;; we subscribe here instead of one level higher up to make this a more
  ;; reusable component; this way we can for example get a list of all songs
  ;; in a search result and easily highlight the currently playing track
  (let [current-song @(subscribe [:audio/current-song])]
    [:table.song-listing-table.table.is-fullwidth
     [:thead>tr
      [:td.is-narrow]
      [:td.song-artist "Artist"]
      [:td.song-title "Title"]
      [:td.song-duration "Duration"]
      [:td.is-narrow]]
     [:tbody
      (for [[idx song] (map-indexed vector (:song album))]
        ^{:key idx}
        [(if (= (:id song) (:id current-song)) :tr.is-playing :tr)
         [:td.song-tracknr.is-narrow (:track song)]
         [:td.song-artist [artist-link song]]
         [:td.song-title [song-link {:album album
                                     :song song
                                     :idx idx}]]
         [:td.song-duration (h/format-duration (:duration song) :brief? true)]
         [:td.song-actions.is-narrow [song-actions {:album album
                                                    :song song
                                                    :idx idx}]]])]]))

(defn detail
  "Shows a detail view of a single album, listing all "
  [{:keys [album]}]
  [:div
   [:section.hero.is-small>div.hero-body
    [:div.container
     [:article.collection-header.media
      [:div.media-left [cover album 128]]
      [:div.media-content
       [:h2.title (:name album)]
       [:h3.subtitle (:artist album)]
       [collection-info album]]]]]
   [:section.section>div.container
    [song-table {:album album}]]])
