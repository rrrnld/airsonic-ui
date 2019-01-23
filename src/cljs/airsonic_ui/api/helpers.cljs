(ns airsonic-ui.api.helpers
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def default-params {:f "json"
                     :c "airsonic-ui-cljs"
                     :v "1.15.0"})

(defn- unroll-variadic-params
  "Turns {:id [1 2 3], :foo :bar} into [[:id 1] [:id 2] [:id 3] [:foo :bar]]"
  [params]
  (->>
   (map (fn [[k vs]]
          (if (sequential? vs)
            (map (fn [v] [k v]) vs)
            [k vs])) params)
   (flatten)
   (partition 2)))

(def ^:private encode js/encodeURIComponent)

(defn url
  "Returns an absolute url to an API endpoint"
  [credentials endpoint params]
  (let [server (:server credentials)
        auth (select-keys credentials [:u :p])
        query (->> (merge default-params auth params)
                   (unroll-variadic-params)
                   (map (fn [[k v]] (str (encode (name k)) "=" (encode v))))
                   (str/join "&"))]
    (str (str/replace server #"/+$" "") "/rest/" endpoint "?" query)))

(defn stream-url [credentials song-or-episode]
  ;; podcasts have a stream-id, normal songs just use their id
  (let [params {:id (or (:streamId song-or-episode)
                        (:id song-or-episode))}]
    (url credentials "stream" params)))

(defn cover-url [credentials item size]
  (url credentials "getCoverArt" {:id (:coverArt item) :size size}))

(defn is-error? [response]
  (= "failed" (get-in response [:subsonic-response :status])))

(defn- unwrap-response* [response]
  (-> (:subsonic-response response)
      (dissoc :status :version)
      vals
      first))

(defn ->exception
  "Takes an erroneous response and makes it a real exception"
  [response]
  (let [error (unwrap-response* response)]
    (ex-info (:message response) error)))

(defn unwrap-response
  "Retrieves the actual response body"
  [response]
  (if (is-error? response)
    (throw (->exception response))
    (unwrap-response* response)))

(defn error-msg
  [exception-info]
  (let [{:keys [code message]} (ex-data exception-info)]
    (str "Error " code ": " message)))

(defn content-type
  "Given some piece of data returned by the api, returns a keyword that
  describes what we look at"
  [data]
  (keyword :content-type
           (condp set/subset? (set (keys data))
             #{:path} :song
             #{:artistId :name :songCount :artist} :album
             #{:id :name :albumCount} :artist
             :unknown)))
