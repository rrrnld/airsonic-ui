(ns airsonic-ui.api
  (:require [clojure.string :as string]
            [airsonic-ui.config :as config]))

(defn ^:private uri-escape [s]
  (js/encodeURIComponent s))

(defn url
  "Returns an absolute url to an API endpoint"
  [endpoint params]
  (let [query (->> (assoc params
                          :f "json"
                          :c "airsonic-ui-cljs"
                          :v "1.15.0")
                   (map (fn [[k v]]
                          (str (uri-escape (name k)) "=" (uri-escape v))))
                   (string/join "&"))]
    (str config/server "/rest/" endpoint "?" query)))

(defn ^:private api-error?
  "We need to look at the message body because the subsonic api always responds
  with status 200"
  [response]
  (= "failed" (-> response :subsonic-response :status)))

(defn ^:private error-message
  [response]
  (let [{:keys [code message]} (-> response :subsonic-response :error)]
    (str "Code " code ": " message)))
