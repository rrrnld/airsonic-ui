(ns airsonic-ui.views
  "This module contains the outmost layer of our app views. It makes sure that
  the proper subscriptions are run and arranges the complete layout."
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.helpers :refer [add-classes]]

            [airsonic-ui.views.notifications :refer [notification-list]]
            [airsonic-ui.views.breadcrumbs :refer [breadcrumbs]]
            [airsonic-ui.views.login :refer [login-form]]
            [airsonic-ui.components.audio-player.views :refer [audio-player]]
            [airsonic-ui.components.search.views :as search]
            [airsonic-ui.components.library.views :as library]
            [airsonic-ui.components.artist.views :as artist]
            [airsonic-ui.components.collection.views :as collection]))

(def logo-url "./img/airsonic-light-350x100.png")

(defn navbar-top
  "Contains search, some navigational links and the logo"
  [_]
  (let [active? (r/atom false)
        toggle-active #(swap! active? not)
        navbar-item (fn navbar-item [{:keys [href]} label]
                      [:a.navbar-item {:href href :on-click toggle-active} label])]
    (fn [{:keys [user]}]
      [:nav.navbar.is-fixed-top.is-dark {:role "navigation", :aria-label "search and navigation"}
       ;; user is `nil` when we're not logged in, we can hide the extended navigation
       [:div.navbar-brand
        [:div.navbar-item>img {:src logo-url}]
        [:div.navbar-burger.burger {:on-click toggle-active} (repeat 3 [:span])]]
       (when user
         [(if @active? :div.navbar-menu.is-active :div.navbar-menu)
          [:div.navbar-start
           [:div.navbar-item [search/form]]]
          [:div.navbar-end
           [:div.navbar-item.has-dropdown.is-hoverable
            [:div.navbar-link "Library"]
            [:div.navbar-dropdown
             [navbar-item {:href (url-for ::routes/library {:criteria "recent"})} "Recently played"]
             [navbar-item {:href (url-for ::routes/library {:criteria "newest"})} "Newest additions"]
             [navbar-item {:href (url-for ::routes/library {:criteria "starred"})} "Starred"]]]
           [navbar-item {} "Podcasts"]
           [navbar-item {} "Playlists"]
           [navbar-item {} "Shares"]
           [:div.navbar-item.has-dropdown.is-hoverable
            [:div.navbar-link "More"]
            [:div.navbar-dropdown.is-right
             [navbar-item "Settings"]
             [:a.navbar-item
              {:on-click (fn [_]
                           (toggle-active)
                           (dispatch [::events/logout]))
               :href "#"}
              (str "Logout (" (:name user) ")")]]]]])])))

(defn media-content
  "Provides the complete UI to browse the media library, interact with search
  results etc"
  [route-id params query]
  (let [;; TODO: Move this to a layer 3 subscription ↓
        route-events @(subscribe [:routes/events-for-current-route])
        content @(subscribe [:api/route-data route-events])]
    [:div
     [:section.section
      [breadcrumbs content]
      (case route-id
        ::routes/library [library/main [route-id params query] content]
        ::routes/artist-view [artist/detail content]
        ::routes/album-view [collection/detail content]
        ::routes/search [search/results content])]
     [audio-player]]))

(defn main-panel []
  (let [notifications @(subscribe [::subs/notifications])
        is-booting? @(subscribe [::subs/is-booting?])
        [route-id params query] @(subscribe [:routes/current-route])
        user @(subscribe [::subs/user])]
    [(add-classes :div route-id)
     [notification-list notifications]
     (if is-booting?
       [:div.app-loading>div.loader]
       [:div
        [navbar-top {:user user}]
        (case route-id
          ::routes/login [login-form]
          [media-content route-id params query])])]))
