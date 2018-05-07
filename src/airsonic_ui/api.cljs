(ns airsonic-ui.api
  (:require [clojure.string :as str]
            [airsonic-ui.config :as config]))

(defn- encode [c]
  (js/encodeURIComponent c))

(defn url
  "Returns an absolute url to an API endpoint"
  [server endpoint params]
  (let [query (->> (assoc params
                          :f "json"
                          :c "airsonic-ui-cljs"
                          :v "1.15.0")
                   (map (fn [[k v]] (str (encode (name k)) "=" (encode v))))
                   (str/join "&"))]
    (str server (when-not (str/ends-with? server "/") "/") "rest/" endpoint "?" query)))

(defn song-url [server credentials song]
  (url server "stream" (merge {:id (:id song)} credentials)))

(defn- api-error?
  "We need to look at the message body because the subsonic api always responds
  with status 200"
  [response]
  (= "failed" (-> response :subsonic-response :status)))

(defn- error-message
  [response]
  (let [{:keys [code message]} (-> response :subsonic-response :error)]
    (str "Code " code ": " message)))
