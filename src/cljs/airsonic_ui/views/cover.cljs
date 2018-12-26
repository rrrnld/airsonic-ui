(ns airsonic-ui.views.cover
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.components.highres-canvas.views :refer [canvas]]
            ["@hugojosefson/color-hash" :as ColorHash]))

(def color-hash (ColorHash.))

(defn hsl->css [h s l]
  (str "hsl(" h "," (* 100 s) "%," (* 100 l) "%)"))

(defn palette
  "Generate a hsl palette of two colors that's unique for a given item"
  [item]
  (let [identifier (str (:artistId item) "-" (or (:albumId item) (:id item)))
        [h s l] (js->clj (.hsl color-hash identifier))]
    [(hsl->css h s l)
     (hsl->css (mod (+ h (* h 0.3) 10) 360) s l)]))

(defn generate-cover [ctx item]
  (let [[a b] (palette item)
        size (.. ctx -canvas -offsetWidth)
        pad (* 0.02 size)
        gradient (doto (.createLinearGradient ctx pad 0 (- size pad) size)
                   (.addColorStop 0 a)
                   (.addColorStop 1 b))]
    (set! (.. ctx -canvas -height) (.. ctx -canvas -width))
    (set! (.. ctx -canvas -style -height) (.. ctx -canvas -style -width))
    ;; we have to re-scale everything because resizing messes with the content
    (.scale ctx (.-devicePixelRatio js/window) (.-devicePixelRatio js/window))
    (set! (.-fillStyle ctx) gradient)
    (.fillRect ctx 0 0 (.. ctx -canvas -width) (.. ctx -canvas -height))))

(defn missing-cover
  [item size]
  [canvas {:class "missing-cover"
           :draw #(generate-cover % item)}])

(defn has-cover? [item]
  (:coverArt item))

;; FIXME: The direct dependency on these subs is a bit ugly

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
