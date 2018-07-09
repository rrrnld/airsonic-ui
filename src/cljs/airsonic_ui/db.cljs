(ns airsonic-ui.db
  (:require [airsonic-ui.routes :as routes]))

(def default-db
  {:notifications (sorted-map)})
