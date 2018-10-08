(ns airsonic-ui.components.audio-player.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.helpers :refer [add-classes muted-dispatch]]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...

(defn current-song-info [song status]
  [:article.current-song-info
   [:span (:artist song) " - " (:title song)]
   ;; FIXME: Sometimes items don't have a duration
   [:progress.progress.is-tiny {:value (:current-time status)
                                :max (:duration song)}]])

(defn song-controls [is-playing?]
  [:div.field.has-addons
   (let [buttons [[:media-step-backward :audio-player/previous-song]
                  [(if is-playing? :media-pause :media-play) :audio-player/toggle-play-pause]
                  [:media-step-forward :audio-player/next-song]]
         title {:media-step-backward "Previous"
                :media-play "Play"
                :media-pause "Pause"
                :media-step-forward "Next"}]
     (map (fn [[icon-glyph event]]
            ^{:key icon-glyph} [:p.control>button.button.is-light
                                {:on-click (muted-dispatch [event])
                                 :title (title icon-glyph)}
                                [icon icon-glyph]])
          buttons))])

(defn- toggle-shuffle [playback-mode]
  (muted-dispatch [:audio-player/set-playback-mode (if (= playback-mode :shuffled)
                                          :linear :shuffled)]))

(defn- toggle-repeat-mode [current-mode]
  (let [modes (cycle '(:repeat-none :repeat-all :repeat-single))
        next-mode (->> (drop-while (partial not= current-mode) modes)
                       (second))]
    (muted-dispatch [:audio-player/set-repeat-mode next-mode])))

(defn playback-mode-controls [playlist]
  (let [{:keys [repeat-mode playback-mode]} playlist
        button :p.control>button.button.is-light
        shuffle-button (add-classes button (when (= playback-mode :shuffled) :is-primary))
        repeat-button (add-classes button (case repeat-mode
                                            :repeat-single :is-info
                                            :repeat-all :is-primary
                                            nil))
        repeat-title (case repeat-mode
                       :repeat-all "Click to repeat current track"
                       :repeat-single "Click to repeat all"
                       "Click to repeat current track")]
    [:div.field.has-addons
     ^{:key :shuffle-button} [shuffle-button {:on-click (toggle-shuffle playback-mode)
                                              :title "Shuffle"} [icon :random]]
     ^{:key :repeat-button} [repeat-button {:on-click (toggle-repeat-mode repeat-mode)
                                            :title repeat-title} [icon :loop]]]))

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
         [:div.level-right
          [:div.button-group [:p.control>a.button.is-light {:href (routes/url-for ::routes/current-queue) :title "Go to current queue"} [icon :menu]]]
          [:div.button-group [song-controls is-playing?]]
          [:div.button-group [playback-mode-controls playlist]]]]
        ;; not playing anything
        [:p.navbar-item.idle-notification "No audio playing"])]]))
