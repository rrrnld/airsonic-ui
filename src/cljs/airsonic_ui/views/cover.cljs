(ns airsonic-ui.views.cover
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.subs :as subs]
            ["@hugojosefson/color-hash" :as ColorHash]))

(def color-hash (ColorHash.))

(defn hsl->css [h s l]
  (str "hsl(" h "," (* 100 s) "%," (* 100 l) "%)"))

(defn palette
  "Generate a unique hsl palette of two colors"
  [identifier]
  (let [[h s l] (js->clj (.hsl color-hash identifier))]
    [(hsl->css h s l)
     (hsl->css (mod (+ h (* h 0.3) 10) 360) s l)]))

(defn missing-cover
  [item size]
  (let [identifier (str (:artistId item) "-" (or (:albumId item) (:id item)))
        [color-a color-b] (palette identifier)]
    [:svg.missing-cover {:viewBox "0 0 256 256"
                         :xmlns "http://www.w3.org/2000/svg"}
     [:defs [:linearGradient {:id (str "cover-gradient-" identifier)
                              :x1 0, :y1 0,
                              :x2 1, :y2 1}
             [:stop {:offset "2%", :stop-color color-a}]
             [:stop {:offset "98%", :stop-color color-b}]]]
     [:rect {:x 0, :y 0, :width 256, :height 256
             :fill (str "url(#cover-gradient-" identifier ")")}]]))

(defn has-cover? [item]
  (some? (:coverArt item)))

(defn cover
  [item size]
  (let [original @(subscribe [::subs/cover-url item size])
        retina @(subscribe [::subs/cover-url item (* 2 size)])]
    [:figure {:class (str "image is-" size "x" size)}
     (if (has-cover? item)
       [:img {:src original
              :srcSet (str original ", " retina " 2x")}]
       [missing-cover item size])]))

(defn card [item & {:keys [url-fn content size] :or {size 256}}]
  [:article.card.preview-card
   [:div.card-image [:a {:href (url-fn item)} [cover item size]]]
   [:div.card-content content]])
