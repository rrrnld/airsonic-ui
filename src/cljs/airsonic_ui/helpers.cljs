(ns airsonic-ui.helpers
  "Assorted helper functions"
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(defn find-where
  "Returns the the first item in `coll` with its index for which `(p song)`
  is truthy"
  [p coll]
  (->> (map-indexed vector coll)
       (reduce (fn [_ [idx song]]
                 (when (p song) (reduced [idx song]))) nil)))

(defn muted-dispatch
  "Dispatches a re-frame event while canceling default DOM behavior"
  [ev]
  (fn [e]
    (.preventDefault e)
    (rf/dispatch ev)))

(defn add-classes
  "Adds one or more classes to a hiccup keyword"
  [elem & classes]
  (keyword (apply str (name elem) (->> (filter identity classes)
                                       (map #(str "." (name %)))))))

(defn kebabify
  "Turns camelCased strings and keywords into kebab-cased keywords"
  [x]
  (-> (if (keyword? x) (name x) x)
      (str/replace #"([a-z])([A-Z])" (fn [[_ a b]] (str a "-" b)))
      (str/lower-case)
      (keyword)))

(defn format-duration [seconds]
  (let [hours (quot seconds 3600)
        minutes (quot (rem seconds 3600) 60)
        seconds (rem seconds 60)]
    (-> (cond-> ""
          (> hours 0) (str hours "h ")
          (> minutes 0) (str minutes "m "))
        (str seconds "s"))))
