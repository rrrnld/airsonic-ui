(ns airsonic-ui.views.song
  (:require [airsonic-ui.utils.helpers :refer [dispatch]]
            [airsonic-ui.events :as events]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.icon :refer [icon]]))

(defn item [songs song idx]
  (let [artist-id (:artistId song)]
    [:div
     [:a
      (when artist-id {:href (url-for ::routes/artist-view {:id artist-id})})
      (:artist song)]
     " - "
     [:a
      {:href "#" :on-click (dispatch [::events/play-songs songs idx])}
      (:title song)]]))

(defn listing [songs]
  [:table.table.is-striped.is-hoverable.is-fullwidth>tbody
   (for [[idx song] (map-indexed vector songs)]
     ^{:key idx} [:tr
                     [:td.grow [item songs song idx]]
                     ;; FIXME: Not implemented yet
                  [:td>a {:title "Play next"
                          :href "#"
                          :on-click (dispatch [::events/enqueue-next song])}
                   [icon :plus]]
                  [:td>a {:title "Play last"
                          :href "#"
                          :on-click (dispatch [::events/enqueue-last song])}
                   [icon :arrow-thick-right]]])])
