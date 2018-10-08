(ns airsonic-ui.views.song
  (:require [airsonic-ui.helpers :refer [muted-dispatch]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.icon :refer [icon]]))

(defn item [songs song idx]
  (let [artist-id (:artistId song)]
    [:div
     (if artist-id
       [:a {:href (url-for ::routes/artist.detail {:id artist-id})} (:artist song)]
       (:artist song))
     " - "
     [:a
      {:href "#" :on-click (muted-dispatch [:audio-player/play-all songs idx])}
      (:title song)]]))

(defn listing [songs]
  [:table.table.is-striped.is-hoverable.is-fullwidth.song-list>tbody
   (for [[idx song] (map-indexed vector songs)]
     ^{:key idx} [:tr
                     [:td.grow [item songs song idx]]
                  [:td>a {:title "Play next"
                          :href "#"
                          :on-click (muted-dispatch [:audio-player/enqueue-next song])}
                   [icon :plus]]
                  [:td>a {:title "Play last"
                          :href "#"
                          :on-click (muted-dispatch [:audio-player/enqueue-last song])}
                   [icon :caret-right]]])])
