(ns airsonic-ui.views.cover
  (:require [clojure.string :as str]
            [re-frame.core :refer [subscribe]]
            [reagent.core :as reagent]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.utils.api :as api]
            ["@hugojosefson/color-hash" :as ColorHash]))

(def color-hash (ColorHash.))

(defn palette
  "Generate a hsl palette of two colors that's unique for a given item"
  [item]
  (let [[h s l] (js->clj (.hsl color-hash (str (:name item) (:artist item))))
        s (str (* 100 s) "%")
        l (str (* 100 l) "%")]
    (->>
     [[h s l]
      [(mod (+ h (* h 0.3) 10) 360) s l]]
     (map #(str "hsl(" (str/join "," %) ")")))))

;; FIXME: The direct dependency on these subs is a bit ugly

(defn generate-cover [canvas item]
  (let [ctx (.getContext canvas "2d")
        size (.-clientWidth canvas)
        [a b] (palette item)
        pad (* 0.02 size)
        gradient (doto (.createLinearGradient ctx pad 0 (- size pad) size)
                   (.addColorStop 0 a)
                   (.addColorStop 1 b))]
    (set! (.-fillStyle ctx) gradient)
    (.fillRect ctx 0 0 size size)))

(defn missing-cover
  [item size]
  (let [dom-node (reagent/atom nil)]
    (reagent/create-class
     {:component-did-update
      (fn [this]
        (let [canvas @dom-node]
          (set! (.. canvas -style -width) "100%")
          (set! (. canvas -width) (.-offsetWidth canvas))
          (set! (. canvas -height) (.-offsetWidth canvas))
          (generate-cover canvas item)))

      :component-did-mount
      (fn [this]
        (reset! dom-node (reagent/dom-node this)))

      :reagent-render
      (fn []
        @dom-node
        [:canvas.missing-cover])})))

(defn has-cover? [item]
  (:coverArt item))

(defn cover
  [item size]
  (let [server @(subscribe [::subs/server])
        login @(subscribe [::subs/login])
        url (partial api/cover-url server login item)]
    [:figure {:class-name (str "image is-" size "x" size)}
     (if (has-cover? item)
       [:img {:src (url size)
              :srcSet (str (url size) ", " (url (* 2 size)) " 2x")}]
       [missing-cover item size])]))
