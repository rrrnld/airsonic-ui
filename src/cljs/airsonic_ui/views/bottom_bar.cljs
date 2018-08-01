(ns airsonic-ui.views.bottom-bar
  (:require [re-frame.core :refer [dispatch subscribe]]
            [airsonic-ui.events :as events]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...

(defn current-song-info [song status]
  [:article
   [:div (:artist song) " - " (:title song)]
   ;; FIXME: Sometimes items don't have a duration
   [:progress.progress.is-tiny {:value (:current-time status)
                                :max (:duration song)}]])

(defn playback-controls [is-playing?]
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
  (let [current-song @(subscribe [:audio/current-song])
        playback-status @(subscribe [:audio/playback-status])
        is-playing? @(subscribe [:audio/is-playing?])]
    [:nav.navbar.is-fixed-bottom.playback-area
     [:div.navbar-brand
      [:div.navbar-item
       [:img {:src logo-url}]]]
     [:div.navbar-menu.is-active
      (if current-song
        ;; show song info
        [:section.level.audio-interaction
         [:div.level-left>article.media
          [:div.media-left [cover current-song 48]]
          [:div.media-content [current-song-info current-song playback-status]]]
         [:div.level-right [playback-controls is-playing?]]]
        ;; not playing anything
        [:p.idle-notification "Currently no song selected"])]]))
