(ns airsonic-ui.components.current-queue.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.views.song :as song]
            [airsonic-ui.components.sortable.views :as sortable]
            [airsonic-ui.routes :as r]))

(defn current-queue []
  [:section.section>div.container
   [:h1.title "Current Queue"]
   [sortable/sortable-component]
   (let [current-queue @(subscribe [:audio/current-queue])
         #_#_ current-song @(subscribe [:audio/current-song])]
     (if (some? current-queue)
       [song/listing (:items current-queue)]
       [:p "You are currently not playing anything. Use the search or go to your "
        [:a {:href (r/url-for ::r/library)} "Library"] " to start playing some music."]))])
