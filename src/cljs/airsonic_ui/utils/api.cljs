(ns airsonic-ui.utils.api
  (:require [clojure.string :as str]
            [airsonic-ui.config :as config]))

(def default-params {:f "json"
                     :c "airsonic-ui-cljs"
                     :v "1.15.0"})

(defn- encode [c]
  (js/encodeURIComponent c))

(defn url
  "Returns an absolute url to an API endpoint"
  [server endpoint params]
  (let [query (->> (merge default-params params)
                   (map (fn [[k v]] (str (encode (name k)) "=" (encode v))))
                   (str/join "&"))]
    (str server (when-not (str/ends-with? server "/") "/") "rest/" endpoint "?" query)))

(defn song-url [server credentials song]
  (url server "stream" (merge (select-keys song [:id]) credentials)))

(defn cover-url [server credentials item size]
  (url server "getCoverArt" (merge {:id (:coverArt item) :size size} credentials)))
