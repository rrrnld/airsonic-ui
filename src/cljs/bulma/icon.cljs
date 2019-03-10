(ns bulma.icon)

(defn icon [glyph]
  [:span.icon [:span.oi {:data-glyph (name glyph)}]])
