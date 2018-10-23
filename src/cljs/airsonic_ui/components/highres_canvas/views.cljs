(ns airsonic-ui.components.highres-canvas.views
  "This module provides a reusable canvas component. You can provide a drawing
  function via the `:draw` attribute which will be passed a 2d rendering
  context. It will automatically be drawn in high resolution on retina displays."
  (:require [reagent.core :as reagent]))

(defn redraw [this]
  (let [draw (:draw (reagent/props this))
        canvas (reagent/dom-node this)
        width (.-clientWidth canvas)
        height (.-clientHeight canvas)
        ctx (.getContext canvas "2d")
        pixel-ratio (.-devicePixelRatio js/window)]
    (set! (. canvas -width) width)
    (set! (. canvas -height) height)
    (set! (.. canvas -style -width) (str width "px"))
    (set! (.. canvas -style -height) (str height "px"))
    ;; retina drawing code:
    ;; set up dimensions, reset the transform matrix to the identity
    ;; matrix and automatically scale up
    (when (> pixel-ratio 1)
      (set! (. canvas -width) (* pixel-ratio width))
      (set! (. canvas -height) (* pixel-ratio height))
      (.setTransform ctx 1 0 0 1 0 0)
      (.scale ctx pixel-ratio pixel-ratio))
    (draw ctx)))

(defn canvas [attrs & _]
  (reagent/create-class
   {:component-did-update redraw
    :component-did-mount redraw
    :render (fn render []
              [:canvas.highres-canvas (dissoc attrs :draw)])}))
