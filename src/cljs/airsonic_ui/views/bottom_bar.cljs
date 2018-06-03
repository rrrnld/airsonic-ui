(ns airsonic-ui.views.bottom-bar
  (:require [re-frame.core :refer [dispatch subscribe]]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...

(defn current-song-info [{:keys [item status]}]
  [:article
   [:div (:artist item) " - " (:title item)]
   ;; FIXME: Sometimes items don't have a duration
   [:progress.progress.is-tiny {:value (:current-time status)
                                :max (:duration item)}]])

(defn playback-controls [is-playing?]
  ;; TODO: Toggle play pause icon based on playback status
  [:div.field.has-addons
   (let [buttons [[:media-step-backward ::events/previous-song]
                  [(if is-playing? :media-pause :media-play) ::events/toggle-play-pause]
                  [:media-step-forward ::events/next-song]]]
     (map (fn [[icon-glyph event]]
            ^{:key icon-glyph} [:p.control>button.button.is-light
                                {:on-click #(dispatch [event])}
                                [icon icon-glyph]])
          buttons))])

  (def logo-url "https://airsonic.github.io/airsonic-ui/assets/images/logo/airsonic-light-350x100.png")

(defn bottom-bar []
  (let [currently-playing @(subscribe [::subs/currently-playing])
        is-playing? @(subscribe [::subs/is-playing?])]
    [:nav.navbar.is-fixed-bottom.playback-area
     [:div.navbar-brand
      [:div.navbar-item
       [:img {:src logo-url}]]]
     [:div.navbar-menu.is-active
      (if currently-playing
        ;; show song info
        [:section.level.audio-interaction
         [:div.level-left>article.media
          [:div.media-left [cover (:item currently-playing) 48]]
          [:div.media-content [current-song-info currently-playing]]]
         [:div.level-right [playback-controls is-playing?]]]
        ;; not playing anything
        [:p.idle-notification "Currently no song selected"])]]))
