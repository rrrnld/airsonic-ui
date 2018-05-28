(ns airsonic-ui.views.song
  (:require [re-frame.core :refer [dispatch]]
            [airsonic-ui.events :as events]
            [airsonic-ui.routes :as routes :refer [url-for]]))

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

;; FIXME: This is very similar to album-listing

(defn listing [songs]
  [:ul (for [[idx song] (map-indexed vector songs)]
         [:li {:key idx} [item songs song]])])
