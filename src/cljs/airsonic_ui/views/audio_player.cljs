(ns airsonic-ui.views.audio-player
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.utils.helpers :refer [dispatch]]
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

(defn song-controls [is-playing?]
  [:div.field.has-addons
   (let [buttons [[:media-step-backward ::events/previous-song]
                  [(if is-playing? :media-pause :media-play) ::events/toggle-play-pause]
                  [:media-step-forward ::events/next-song]]]
     (map (fn [[icon-glyph event]]
            ^{:key icon-glyph} [:p.control>button.button.is-light
                                {:on-click (dispatch [event])}
                                [icon icon-glyph]])
          buttons))])

(defn- add-classes
  "Adds one or more classes to a hiccup keyword"
  [elem & classes]
  (keyword (apply str (name elem) (->> (filter identity classes)
                                       (map #(str "." (name %)))))))

(defn- toggle-shuffle [playback-mode]
  (dispatch [::events/set-playback-mode (if (= playback-mode :shuffled)
                                          :linear :shuffled)]))

(defn- advance-repeat-mode [current-mode]
  (let [modes (cycle '(:repeat-none :repeat-all :repeat-single))
        next-mode (->> (drop-while (partial not= current-mode) modes)
                       (second))]
    (dispatch [::events/set-repeat-mode next-mode])))

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
     ^{:key :repeat-button} [repeat-button {:on-click (advance-repeat-mode repeat-mode)} [icon :loop]]]))

(def logo-url "./img/airsonic-light-350x100.png")

(defn audio-player []
  (let [current-song @(subscribe [:audio/current-song])
        playlist @(subscribe [:audio/playlist])
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
         [:div.level-right [song-controls is-playing?]]
         [:div.level-right [playback-mode-controls playlist]]]
        ;; not playing anything
        [:p.idle-notification "Currently no song selected"])]]))
