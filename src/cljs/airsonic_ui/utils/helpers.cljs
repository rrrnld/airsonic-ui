(ns airsonic-ui.utils.helpers
  "Assorted helper functions")

(defn find-where
  "Returns the the first item in `coll` with its index for which `(p song)`
  is truthy"
  [p coll]
  (->> (map-indexed vector coll)
       (reduce (fn [_ [idx song]]
                 (when (p song) (reduced [idx song]))) nil)))
