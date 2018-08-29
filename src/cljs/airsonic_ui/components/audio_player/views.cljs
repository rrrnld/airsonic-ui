(ns airsonic-ui.components.audio-player.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.helpers :refer [add-classes dispatch]]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...

(defn current-song-info [song status]
  [:article
   [:div (:artist song) " - " (:title song)]
   ;; FIXME: Sometimes items don't have a duration
   [:progress.progress.is-tiny {:value (:current-time status)
                                :max (:duration song)}]])

(defn song-controls [is-playing?]
  [:div.field.has-addons
   (let [buttons [[:media-step-backward :audio-player/previous-song]
                  [(if is-playing? :media-pause :media-play) :audio-player/toggle-play-pause]
                  [:media-step-forward :audio-player/next-song]]]
     (map (fn [[icon-glyph event]]
            ^{:key icon-glyph} [:p.control>button.button.is-light
                                {:on-click (dispatch [event])}
                                [icon icon-glyph]])
          buttons))])

(defn- toggle-shuffle [playback-mode]
  (dispatch [:audio-player/set-playback-mode (if (= playback-mode :shuffled)
                                          :linear :shuffled)]))

(defn- toggle-repeat-mode [current-mode]
  (let [modes (cycle '(:repeat-none :repeat-all :repeat-single))
        next-mode (->> (drop-while (partial not= current-mode) modes)
                       (second))]
    (dispatch [:audio-player/set-repeat-mode next-mode])))

(defn playback-mode-controls [playlist]
  (let [{:keys [repeat-mode playback-mode]} playlist
        button :p.control>button.button.is-light
        shuffle-button (add-classes button (when (= playback-mode :shuffled) :is-primary))
        repeat-button (add-classes button (case repeat-mode
                                            :repeat-single :is-info
                                            :repeat-all :is-primary
                                            nil))]
    [:div.field.has-addons
     ^{:key :shuffle-button} [shuffle-button {:on-click (toggle-shuffle playback-mode)} [icon :random]]
     ^{:key :repeat-button} [repeat-button {:on-click (toggle-repeat-mode repeat-mode)} [icon :loop]]]))

(defn audio-player []
  (let [current-song @(subscribe [:audio/current-song])
        playlist @(subscribe [:audio/playlist])
        playback-status @(subscribe [:audio/playback-status])
        is-playing? @(subscribe [:audio/is-playing?])]
    [:nav.navbar.is-fixed-bottom.audio-player
     [:div.navbar-menu.is-active
      (if current-song
        ;; show song info
        [:section.level.audio-interaction
         [:div.level-left>article.media
          [:div.media-left [cover current-song 48]]
          [:div.media-content [current-song-info current-song playback-status]]]
         [:div.level-right [song-controls is-playing?]]
         [:div.level-right [playback-mode-controls playlist]]]
        ;; not playing anything
        [:p.has-text-light.navbar-item.idle-notification "Select a song to start playing"])]]))
