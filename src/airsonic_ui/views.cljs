(ns airsonic-ui.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [airsonic-ui.config :as config]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]

            [airsonic-ui.views.breadcrumbs :refer [breadcrumbs]]
            [airsonic-ui.views.bottom-bar :refer [bottom-bar]]
            [airsonic-ui.views.login :refer [login-form]]
            [airsonic-ui.views.album :as album]
            [airsonic-ui.views.song :as song]))

;; TODO: Find better names and places for these.

(defn album-detail [content]
  [:div
   [:h2.title (str (:artist content) " - " (:name content))]
   [song/listing (:song content)]])

(defn artist-detail [content]
  [:div
   [:h2.title (:name content)]
   [album/listing (:album content)]])

(defn most-recent [content]
  [:div
   [:h2.title "Recently played"]
   [album/listing (:album content)]])

;; putting everything together

(defn app [route params query]
  (let [login @(subscribe [::subs/login])
        content @(subscribe [::subs/current-content])]
    [:div
     [:section.section>div.container
      [:div.level
       [:div.level-left [:span (str "Currently logged in as " (:u login))]]
       [:div.level-right [:a {:on-click #(dispatch [::events/initialize-db]) :href "#"} "Logout"]]]
      [breadcrumbs content]
      (case route
        ::routes/main [most-recent content]
        ::routes/artist-view [artist-detail content]
        ::routes/album-view [album-detail content])]
     [bottom-bar]]))

(defn main-panel []
  (let [[route params query] @(subscribe [::subs/current-route])]
    (case route
      ::routes/login [login-form]
      [app route params query])))
