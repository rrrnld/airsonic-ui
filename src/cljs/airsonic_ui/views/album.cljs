(ns airsonic-ui.views.album
  (:require [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.cover :refer [cover]]))

(defn preview [album]
  (let [{:keys [artist artistId name coverArt id]} album]
    [:article.card.album-preview
     [:div.card-image
      [:a {:href (url-for ::routes/album-view {:id id})} [cover album 256]]]
     [:div.card-content
      ;; link to album
      [:div.title.is-5
       [:a {:href (url-for ::routes/album-view {:id id})} name]]
      ;; link to artist page
      [:div.subtitle.is-6 [:a {:href (url-for ::routes/artist-view {:id artistId})} artist]]]]))

(defn listing [albums]
  ;; always show 5 in a row
  [:div
   (for [albums (partition-all 6 albums)]
     [:div.columns
      (for [[idx album] (map-indexed vector albums)]
        [:div.column.is-2 {:key idx} [preview album]])])])
