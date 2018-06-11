(ns airsonic-ui.views.song
  (:require [re-frame.core :refer [dispatch]]
            [airsonic-ui.events :as events]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.icon :refer [icon]]))

(defn item [songs song]
  (let [artist-id (:artistId song)]
    [:div
     [:a
      (when artist-id {:href (url-for ::routes/artist-view {:id artist-id})})
      (:artist song)]
     " - "
     [:a
      {:href "#" :on-click (fn [e]
                             (.preventDefault e)
                             (dispatch [::events/play-songs songs song]))}
      (:title song)]]))

(defn listing [songs]
  [:table.table.is-striped.is-hoverable.is-fullwidth>tbody
   (for [[idx song] (map-indexed vector songs)]
     ^{:key idx} [:tr
                     [:td.grow [item songs song]]
                     ;; FIXME: Not implemented yet
                     [:td>a {:title "Play next"} [icon :plus]]
                     [:td>a {:title "Play last"} [icon :arrow-thick-right]]])])
