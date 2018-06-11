(ns airsonic-ui.views.loading-spinner
  (:require [airsonic-ui.views.icon :refer [icon]]))

(defn loading-spinner []
  [:span.loading-spinner [icon :reload]])
