(ns airsonic-ui.components.audio-player.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.components.highres-canvas.views :refer [canvas]]
            [airsonic-ui.helpers :refer [add-classes muted-dispatch]]
            [airsonic-ui.views.cover :refer [cover]]
            [airsonic-ui.views.icon :refer [icon]]))

;; currently playing / coming next / audio controls...
;; FIXME: Sometimes items don't have a duration

(def progress-bar-color "rgb(93,93,93)")
(def progress-bar-color-buffered "rgb(123,123,123)")
(def progress-bar-color-active "whitesmoke")

(defn draw-progress [ctx current-time seekable duration]
  (let [width (.. ctx -canvas -clientWidth)
        height (.. ctx -canvas -clientHeight)
        padding 5
        seekable-x (+ padding (* (- width (* 2 padding)) (min 1 (/ seekable duration))))
        current-x (+ padding (* (- width (* 2 padding)) (min 1 (/ current-time duration))))]
    ;; vertically center everything
    (.translate ctx 0.5 (+ (Math/ceil (/ height 2)) 0.5))
    ;; draw complete bar
    (set! (.-strokeStyle ctx) progress-bar-color)
    (doto ctx
      (.beginPath)
      (.moveTo padding 0)
      (.lineTo (- width (* 2 padding)) 0)
      (.stroke))
    ;; draw the buffered part
    (set! (.-strokeStyle ctx) progress-bar-color-buffered)
    (doto ctx
      (.beginPath)
      (.moveTo padding 0)
      (.lineTo seekable-x 0)
      (.stroke))
    ;; draw the part that's already played
    (set! (.-strokeStyle ctx) progress-bar-color-active)
    (doto ctx
      (.beginPath)
      (.moveTo padding 0)
      (.lineTo current-x 0)
      (.stroke))
    ;; draw a dot marking the current time
    (set! (.-fillStyle ctx) progress-bar-color-active)
    (doto ctx
      (.beginPath)
      (.arc current-x 0 (/ padding 2) 0 (* Math/PI 2))
      (.fill))))

(defn current-progress [current-time seekable duration]
  [canvas {:class "current-progress-canvas"
           :draw #(draw-progress % current-time seekable duration)}])

;; FIXME: It's ugly to have the canvas padding and styling scattered everywhere (sass, drawing code above, and here)

(defn seek
  "Calculates the position of the click and sets current playback accordingly"
  [ev]
  (let [x (- (.. ev -nativeEvent -pageX)
             (.. ev -target getBoundingClientRect -left))
        width (- (.. ev -target -nextElementSibling -clientWidth) 10)] ;; <- 10 = 2 * canvas-padding
    (dispatch [:audio-player/seek (/ x width)])))

(defn buffered-part
  [seekable duration]
  (let [width (min 100 (* (/ seekable duration) 100))]
    [:div.buffered-part {:on-click seek
                         :style {:width (str "calc(" width "% - 1rem - 10px)")}}]))

(defn current-song-info [song status]
  (let [current-time (:current-time status)
        seekable (:seekable status)
        duration (:duration song)]
    [:article.current-song-info
     [:div.current-name (:artist song) [:br] (:title song)]
     [:div.current-progress
      [buffered-part seekable duration]
      [current-progress current-time seekable duration]]]))

(defn song-controls [is-playing?]
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
                                       {:on-click (muted-dispatch [event])
                                        :title (title icon-glyph)}
                                       [icon icon-glyph]]]))])

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
                       :repeat-all "Repeating current queue, click to repeat current track"
                       :repeat-single "Repeating current track, click to repeat none"
                       "Click to repeat current queue")]
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
