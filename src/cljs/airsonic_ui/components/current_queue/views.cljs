(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.views.song :as song]
            [airsonic-ui.routes :as r]))

(defn current-queue []
  [:section.section>div.container
   [:h1.title "Current Queue"]
   (if-let [playlist @(subscribe [:audio/playlist])]
     [song/listing (:queue playlist)]
     [:p "You are currently not playing anything. Use the search or go to your "
      [:a {:href (r/url-for ::r/library)} "Library"] " to start playing some music."])])
