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

(defn sidebar [user]
  [:aside.menu.section
   [:p.menu-label "Music"]
   [:ul.menu-list
    [:li [:a "By artist"]]
    [:li [:a "Top rated"]]
    [:li [:a "Most played"]]]
   [:p.menu-label "Playlists"]
   [:p.menu-label "Shares"]
   [:p.menu-label "Podcasts"]
   [:p.menu-label "User area"]
   [:ul.menu-list
    [:li [:a "Settings"]]
    ;; FIXME: Create proper logout event
    [:li [:a
          {:on-click #(dispatch [::events/initialize-db]) :href "#"}
          (str "Logout (" (:name user) ")")]]]])

;; putting everything together

(defn app [route params query]
  (let [user @(subscribe [::subs/user])
        content @(subscribe [::subs/current-content])]
    [:div
     [:main.columns
      [:div.column.is-2.sidebar
       [sidebar user]]
      [:div.column
       [:section.section
        [breadcrumbs content]
        (case route
          ::routes/main [most-recent content]
          ::routes/artist-view [artist-detail content]
          ::routes/album-view [album-detail content])]]]
     [bottom-bar]]))

(defn main-panel []
  (let [[route params query] @(subscribe [::subs/current-route])]
    (case route
      ::routes/login [login-form]
      [app route params query])))
