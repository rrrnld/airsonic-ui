(ns airsonic-ui.components.audio-player.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.helpers :as h]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...

(defn seek
  "Calculates the position of the click and sets current playback accordingly"
  [ev]
  (let [x-ratio (/ (.. ev -nativeEvent -layerX)
                   (.. ev -target -parentElement getBoundingClientRect -width))]
    (dispatch [:audio-player/seek x-ratio])))

(defn- ratio->width [ratio]
  (str (.toFixed (min 100 (* 100 ratio)) 2) "%"))

(defn progress-bars [buffered-width played-width]
  [:svg.progress-bars {:aria-hidden "true"}
   [:svg.complete-song-bar
    [:rect {:x 0, :y "50%", :width "100%", :height 1}]]
   [:svg.buffered-part-bar
    [:rect.click-dummy {:on-click seek
                        :x 0, :y 0, :width buffered-width, :height "100%"}]
    [:rect {:x 0, :y "50%", :width buffered-width, :height 1}]]
   [:svg.played-back-bar
    [:rect {:x 0, :y "50%", :width played-width, :height 1}]
    [:circle {:cx played-width, :cy "50%", :r 2.5}]]])

(defn progress-indicators [song status]
  (let [current-time (:current-time status)
        buffered (:buffered status)
        duration (:duration song)
        progress-text (str (h/format-duration current-time :brief? true)
                           " / "
                           (h/format-duration duration :brief? true))
        buffered-width (ratio->width (/ buffered duration))
        played-width (ratio->width (/ current-time duration))]
    [:article.progress-indicators
     [progress-bars buffered-width played-width]
     [:div.progress-info-text.duration-text progress-text]]))

(defn playback-info [song status]
  [:a.playback-info.media
   {:href (routes/url-for ::routes/current-queue)
    :title "Go to current queue"}
   [:div.media-left [cover song 64]]
   [:div.media-content
    [:div.artist-and-title
     [:span.artist(:artist song)]
     [:span.song-title (:title song)]]]])

(defn playback-controls [is-playing?]
  [:div.playback-controls
   [:div.field.has-addons
    (let [buttons [[:media-step-backward :audio-player/previous-song]
                   [(if is-playing? :media-pause :media-play) :audio-player/toggle-play-pause]
                   [:media-step-forward :audio-player/next-song]]
          title {:media-step-backward "Previous"
                 :media-play "Play"
                 :media-pause "Pause"
                 :media-step-forward "Next"}]
      (for [[icon-glyph event] buttons]
        ^{:key icon-glyph} [:p.control [:button.button.is-light
                                        {:on-click (h/muted-dispatch [event])
                                         :title (title icon-glyph)}
                                        [icon icon-glyph]]]))]])

(defn- toggle-shuffle [playback-mode]
  (h/muted-dispatch [:audio-player/set-playback-mode (if (= playback-mode :shuffled)
                                                     :linear :shuffled)]))

(defn- toggle-repeat-mode [current-mode]
  (let [modes (cycle '(:repeat-none :repeat-all :repeat-single))
        next-mode (->> (drop-while (partial not= current-mode) modes)
                       (second))]
    (h/muted-dispatch [:audio-player/set-repeat-mode next-mode])))

(defn playback-mode-controls [playlist]
  (let [{:keys [repeat-mode playback-mode]} playlist
        button :p.control>button.button.is-light
        shuffle-button (h/add-classes button (when (= playback-mode :shuffled) :is-primary))
        repeat-button (h/add-classes button (case repeat-mode
                                            :repeat-single :is-info
                                            :repeat-all :is-primary
                                            nil))
        repeat-title (case repeat-mode
                       :repeat-all "Repeating current queue, click to repeat current track"
                       :repeat-single "Repeating current track, click to repeat none"
                       "Click to repeat current queue")]
    [:div.playback-mode-controls
     [:div.button-group>div.field.has-addons
      ^{:key :shuffle-button} [shuffle-button {:on-click (toggle-shuffle playback-mode)
                                               :title "Shuffle"} [icon :random]]
      ^{:key :repeat-button} [repeat-button {:on-click (toggle-repeat-mode repeat-mode)
                                             :title repeat-title} [icon :loop]]]]))

(defn audio-player []
  (let [current-song @(subscribe [:audio/current-song])
        playlist @(subscribe [:audio/playlist])
        playback-status @(subscribe [:audio/playback-status])
        is-playing? @(subscribe [:audio/is-playing?])]
    [:nav.audio-player
     (if current-song
       ;; show song info, controls, progress bar, etc.
       [:section.audio-interaction
        [playback-info current-song playback-status]
        [progress-indicators current-song playback-status]
        [playback-controls is-playing?]
        [playback-mode-controls playlist]]
       ;; not playing anything
       [:p.navbar-item.idle-notification "No audio playing"])]))
