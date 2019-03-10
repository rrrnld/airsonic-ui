(ns airsonic-ui.views.song
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.helpers :refer [muted-dispatch format-duration]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [bulma.icon :refer [icon]]))

(defn item [songs song idx]
  (let [artist-id (:artistId song)
        duration (:duration song)]
    [:div
     (if artist-id
       [:a {:href (url-for ::routes/artist.detail {:id artist-id})} (:artist song)]
       (:artist song))
     " - "
     [:a
      {:href "#" :on-click (muted-dispatch [:audio-player/play-all songs idx] :sync? true)}
      (:title song)]
     [:span.duration (format-duration duration)]]))

(defn listing [songs]
  (let [current-song @(subscribe [:audio/current-song])]
    [:table.table.is-striped.is-hoverable.is-fullwidth.song-list>tbody
     (for [[idx song] (map-indexed vector songs)]
       (let [tag (if (= (:id song) (:id current-song)) :tr.song.is-playing :tr.song)]
         ^{:key idx} [tag
                      [:td.grow [item songs song idx]]
                      [:td>a {:title "Play next"
                              :href "#"
                              :on-click (muted-dispatch [:audio-player/enqueue-next song])}
                       [icon :plus]]
                      [:td>a {:title "Play last"
                              :href "#"
                              :on-click (muted-dispatch [:audio-player/enqueue-last song])}
                       [icon :caret-right]]]))]))
