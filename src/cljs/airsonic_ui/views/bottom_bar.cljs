(ns airsonic-ui.views.bottom-bar
  (:require [re-frame.core :refer [dispatch subscribe]]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.views.cover :refer [cover]]))

;; currently playing / coming next / audio controls...

(defn current-song-info [{:keys [item status]}]
  [:article
   [:div (:artist item) " - " (:title item)]
   [:progress.progress.is-tiny {:value (:current-time status)
                                 :max (:duration item)}]])

(defn playback-controls []
  [:div.field.has-addons
   (let [buttons [["previous" ::events/previous-song]
                  ["play / pause" ::events/toggle-play-pause]
                  ["next" ::events/next-song]]]
     (map (fn [[label event]]
            [:p.control>button.button.is-light {:on-click #(dispatch [event])} label])
          buttons))])

  (def logo-url "https://airsonic.github.io/airsonic-ui/assets/images/logo/airsonic-light-350x100.png")

(defn bottom-bar []
  (let [currently-playing @(subscribe [::subs/currently-playing])]
    [:nav.navbar.is-fixed-bottom.playback-area
     [:div.navbar-brand
      [:div.navbar-item
       [:img {:src logo-url}]]]
     [:div.navbar-menu.is-active
      (if currently-playing
        ;; show song info
        [:section.level
         [:div.level-left>article.media
          [:div.media-left [cover (:item currently-playing) 48]]
          [:div.media-content [current-song-info currently-playing]]]
         [:div.level-right [playback-controls]]]
        ;; not playing anything
        [:p.idle-notification "Currently no song selected"])]]))
