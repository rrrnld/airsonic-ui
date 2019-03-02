(ns airsonic-ui.components.audio-player.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
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
  [:div.button-controls.playback-controls
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

(defn set-volume [ev]
  (when (= 1 (.-buttons ev)) ;; only on left-click
    (let [y-ratio (/ (.. ev -nativeEvent -offsetY)
                     (.. ev -target getBoundingClientRect -height))]
      (dispatch [:audio-player/set-volume (- 1 y-ratio)]))))

(defonce volume-slider-visible? (r/atom false))

(defn volume-slider [volume]
  (let [y-pos (* (- 1 volume) 100)]
    [:svg.volume-bar {:width "100%", :height "100%"}
     ;; the translate(...) makes the 1px rects look smoother
     [:g {:transform "translate(-0.5,0)"}
      ;; background line
      [:rect.inactive {:x "50%", :y 0, :width 1, :height "100%"}]
      ;; below are the line and circle that show the current volume
      [:rect.active {:x "50%", :y (str y-pos "%"),
                     :width 1, :height (str (- 100 y-pos) "%")}]]
     [:circle.active {:cx "50%", :cy (str y-pos "%"), :r 3}]
     [:rect.click-dummy {:x 0, :y 0, :width "100%", :height "100%"
                         :on-mouse-down set-volume
                         :on-mouse-up set-volume
                         :on-mouse-move set-volume}]]))

(def toggle-volume-slider #(swap! volume-slider-visible? not))
(def hide-volume-slider #(reset! volume-slider-visible? false))

(defn volume-controls [playback-status]
  (let [volume (:volume playback-status)
        volume-icon (cond
                      (> volume 0.66) :volume-high
                      (> volume 0.1)  :volume-low
                      :else           :volume-off)]
    [:div.button-controls.volume-controls
     (when @volume-slider-visible?
       [:div.button-menu
        [:div.button-menu-closer {:on-click hide-volume-slider}]
        [volume-slider volume]])
     [:p.control>button.button.is-light
      {:on-click toggle-volume-slider}
      [icon volume-icon]]]))

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
    [:div.button-controls.playback-mode-controls
     [:div.button-group>div.field.has-addons
      ^{:key :shuffle-button} [shuffle-button {:on-click (toggle-shuffle playback-mode)
                                               :title "Shuffle"} [icon :random]]
      ^{:key :repeat-button} [repeat-button {:on-click (toggle-repeat-mode repeat-mode)
                                             :title repeat-title} [icon :loop]]]]))

(defn audio-player []
  (let [current-song @(subscribe [:audio/current-song])
        current-queue @(subscribe [:audio/current-queue])
        playback-status @(subscribe [:audio/playback-status])
        is-playing? @(subscribe [:audio/is-playing?])]
    [:nav.audio-player
     (if current-song
       ;; show song info, controls, progress bar, etc.
       [:section.audio-interaction
        [playback-info current-song playback-status]
        [progress-indicators current-song playback-status]
        [playback-controls is-playing?]
        [volume-controls playback-status]
        [playback-mode-controls current-queue]]
       ;; not playing anything
       [:p.navbar-item.idle-notification "No audio playing"])]))
