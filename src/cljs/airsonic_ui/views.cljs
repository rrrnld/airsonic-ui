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
            [bulma.icon :refer [icon]]

            [airsonic-ui.components.about.views :refer [about]]
            [airsonic-ui.components.artist.views :as artist]
            [airsonic-ui.components.audio-player.views :refer [audio-player]]
            [airsonic-ui.components.bangpow.views :refer [not-found]]
            [airsonic-ui.components.collection.views :as collection]
            [airsonic-ui.components.current-queue.views :refer [current-queue]]
            [airsonic-ui.components.keyboard-shortcuts.views :as keyboard]
            [airsonic-ui.components.library.views :as library]
            [airsonic-ui.components.podcast.views :as podcast]
            [airsonic-ui.components.search.views :as search]))

(def logo-url "./img/airsonic-light-350x100.png")

;; ---
;; top navigation
;; ---

(defonce navbar-active? (r/atom false))
(def toggle-navbar-active! #(swap! navbar-active? not))

(defn navbar-item [{:keys [href]} label]
  [:a.navbar-item {:href href :on-click toggle-navbar-active!} label])

(defn navbar-dropdown
  ([label items] (navbar-dropdown label {} items))
  ([label label-opts items]
   [:div.navbar-item.has-dropdown.is-hoverable
    [:div.navbar-link label-opts label]
    [:div.navbar-dropdown
     (for [[idx [opts label]] (map-indexed vector items)]
       ^{:key (str "navbar-dropdown-" idx)}
       [navbar-item
        (merge {:on-click toggle-navbar-active!} opts)
        label])]]))

(defn navbar-top
  "Contains search, some navigational links and the logo"
  []
  (let [user @(subscribe [:user/info])
        stream-role @(subscribe [:user/roles :stream])
        podcast-role @(subscribe [:user/roles :podcast])
        playlist-role @(subscribe [:user/roles :playlist])
        share-role @(subscribe [:user/roles :share])
        settings-role @(subscribe [:user/roles :settings])]
    [:nav.navbar.is-fixed-top.is-dark {:role "navigation", :aria-label "search and navigation"}
     ;; user is `nil` when we're not logged in, we can hide the extended navigation
     [:div.navbar-brand
      [:div.navbar-item>img {:src logo-url}]
      [:div.navbar-burger.burger {:on-click toggle-navbar-active!}
       (for [idx (range 3)] ^{:key (str "burger-" idx)} [:span])]]
     (when user
       [(if @navbar-active? :div.navbar-menu.is-active :div.navbar-menu)
        [:div.navbar-start
         [:div.navbar-item [search/form]]]
        [:div.navbar-end
         [:a.navbar-item {:href (url-for ::routes/current-queue)
                          :title "Current queue"} [icon :audio-spectrum]]
         (when stream-role
           [navbar-dropdown "Library"
            [[{:href (url-for ::routes/library {:kind "recent"})} "Recently played"]
             [{:href (url-for ::routes/library {:kind "newest"})} "Newest additions"]
             [{:href (url-for ::routes/library {:kind "starred"})} "Starred"]
             [{:href (url-for ::routes/artist.overview)} "By artist"]]])
         (when podcast-role
           #_(let [podcast-url (url-for ::routes/podcast.overview)]
             [navbar-dropdown "Podcast" {:href podcast-url}
              [[{:href podcast-url} "Overview"]]]))
         (when playlist-role
           #_[navbar-item {} "Playlists"])
         (when share-role
           #_[navbar-item {} "Shares"])
         [:div.navbar-item.has-dropdown.is-hoverable
          [:div.navbar-link "More"]
          [:div.navbar-dropdown.is-right
           (when settings-role
             #_[navbar-item {} "Settings"])
           [:a.navbar-item {:href (url-for ::routes/about)} "About"]
           [:a.navbar-item
            {:on-click (fn [_]
                         (toggle-navbar-active!)
                         (dispatch [::events/logout]))
             :href "#"}
            (str "Logout (" (:username user) ")")]]]]])]))

;; ---
;; this is the section the user mainly interacts with
;; ---

(defn media-content
  "Provides the complete UI to browse the media library, interact with search
  results etc"
  [[route-id :as route]]
  (let [content @(subscribe [:api/current-route-data])]
    [:div
     [:section.section
      [breadcrumbs route content]
      (case route-id
        ::routes/library [library/main route content]
        ::routes/artist.overview [artist/overview content]
        ::routes/artist.detail [artist/detail content]
        ::routes/album.detail [collection/detail content]
        ::routes/search [search/results content]
        ::routes/podcast.overview [podcast/overview content]
        ::routes/podcast.detail [podcast/detail content]
        ::routes/current-queue [current-queue]
        ::routes/about [about]
        [not-found])]
     [audio-player]]))

(defn main-panel
  "The outermost wrapper; handles display of the login form if necessary,
  makes the code in media-content a bit easier to follow"
  []
  (let [notifications @(subscribe [::subs/notifications])
        is-booting? @(subscribe [::subs/is-booting?])
        [route-id :as route] @(subscribe [:routes/current-route])]
    [(add-classes :div route-id)
     [notification-list notifications]
     [keyboard/help-modal]
     (if is-booting?
       [:div.app-loading>div.loader]
       [:div
        [navbar-top]
        (case route-id
          ::routes/login [login-form]
          [media-content route])])]))
