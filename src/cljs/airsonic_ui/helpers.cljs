(ns airsonic-ui.helpers
  "Assorted helper functions"
  (:require [re-frame.core :as rf]
            [clojure.string :as str])
  (:import [goog.string format]))

(defn muted-dispatch
  "Dispatches a re-frame event while canceling default DOM behavior; to be
  called for example in `:on-click`."
  [ev & {:keys [sync?]}]
  (fn [e]
    (.preventDefault e)
    (if sync?
      (rf/dispatch-sync ev)
      (rf/dispatch ev))))

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

(defn- brief-duration [hours minutes seconds]
  (str (when (> hours 0)
         (format "%02d:" hours))
       (format "%02d:%02d" minutes seconds)))

(defn- long-duration [hours minutes seconds]
  (str/trim
   (cond-> ""
     (> hours 0) (str hours "h ")
     (> minutes 0) (str minutes "m ")
     (> seconds 0) (str seconds "s"))))

(defn format-duration [seconds & {:keys [brief?]}]
  (let [hours (Math/round (quot seconds 3600))
        minutes (Math/round (quot (rem seconds 3600) 60))
        seconds (Math/round (rem seconds 60))]
    (if brief?
      (brief-duration hours minutes seconds)
      (long-duration hours minutes seconds))))
