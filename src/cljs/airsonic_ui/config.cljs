(ns airsonic-ui.config)

(def debug?
  ^boolean goog.DEBUG)

;; how many covers are shown per page when browsing the library
(def albums-per-page 20)
(def albums-prefetch-factor 5)
