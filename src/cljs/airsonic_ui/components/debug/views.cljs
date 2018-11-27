(ns airsonic-ui.components.debug.views
  (:require [fipp.edn :refer [pprint]]))

(defn debug
  "Returns a nicely formatted debug view of any given data structure"
  [data]
  [:pre (with-out-str (pprint data))])
