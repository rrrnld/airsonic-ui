(ns airsonic-ui.views.album
  (:require [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.cover :refer [cover card]]))

(defn preview [album]
  (let [{:keys [artist artistId name id]} album]
    [card album
     :url-fn #(url-for ::routes/album-view {:id id})
     :content [:div
               ;; link to album
               [:div.title.is-5
                [:a {:href (url-for ::routes/album-view {:id id})
                     :title name} name]]
               ;; link to artist page
               [:div.subtitle.is-6 [:a {:href (url-for ::routes/artist-view {:id artistId})
                                        :title artist} artist]]]]))

(defn listing [albums]
  ;; always show 5 in a row
  [:div.columns.is-multiline.is-mobile
   (for [[idx album] (map-indexed vector albums)]
     ^{:key idx} [:div.column.is-one-fifth-desktop.is-one-quarter-tablet.is-half-mobile
                  [preview album]])])
