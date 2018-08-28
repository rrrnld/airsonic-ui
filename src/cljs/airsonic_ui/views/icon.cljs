(ns airsonic-ui.views.icon)

(defn icon [glyph & extra]
  [:span.icon [:span.oi {:data-glyph (name glyph)}]])
