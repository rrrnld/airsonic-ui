(ns airsonic-ui.components.library.views
  (:require [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.album :as album]))

(defn tabs [items active-item]
  [:div.tabs
   [:ul (for [[idx [route label]] (map-indexed vector items)]
          (do
            (println route label active-item)
            ^{:key idx} [:li (when (= route active-item)
                               {:class-name "is-active"})
                         [:a {:href (apply url-for route)} label]]))]])

(defn main [route {:keys [album-list]}]
  [:div
   [:h2.title "Your library"]
   (let [items [[[::routes/library {:criteria "recent"} nil] "Recently played"]
                [[::routes/library {:criteria "newest"} nil] "Newest additions"]
                [[::routes/library {:criteria "starred"} nil] "Starred"]]]
     [tabs items route])
   [album/listing (:album album-list)]])
