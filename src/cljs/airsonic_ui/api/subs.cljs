(ns airsonic-ui.api.subs
  (:require [clojure.string :as str]
            [re-frame.core :refer [reg-sub]]
            [airsonic-ui.helpers :refer [kebabify]]))

(defn responses
  "Returns the response cache"
  [db _]
  (:api/responses db))

(reg-sub :api/responses responses)

(defn response-for
  "Returns the cached response for a single endpoint, respecting passed
  url pramters."
  [responses [_ endpoint params]]
  (get responses [endpoint params]))

(reg-sub
 :api/response-for
 :<- [:api/responses]
 response-for)

(defn responses-for-endpoint
  "Returns a seq of all responses for an endpoint, ignoring url parameters and
  looking only at the path"
  [responses [_ endpoint]]
  (into {} (filter (fn [[[k _] _]] (= endpoint k)) responses)))

(reg-sub
 :api/responses-for-endpoint
 :<- [:api/responses]
 responses-for-endpoint)

(defn endpoint->kw
  "Given an endpoint like `getAlbumList2`, returns a cleaned keyword like
  `:album-list`.

  Rules: Kebab-case everything, remove prefixes like `get`, `create`, `delete`,
  `update` and strip trailing numbers."
  [endpoint-str]
  (-> (str/replace endpoint-str #"^(get|create|update|delete)" "")
      (str/replace #"\d+$" "")
      (kebabify)))

(defn current-route-data
  "Returns all responses for the current route"
  [[responses current-route-events] _]
  (->> (filter #(= :api/request (first %)) current-route-events)
       (mapcat (fn [[_ endpoint params]]
                 [(endpoint->kw endpoint) (get responses [endpoint params])]))
       (apply hash-map)))

(reg-sub
 :api/current-route-data
 :<- [:api/responses]
 :<- [:routes/events-for-current-route]
 current-route-data)

(defn content-pending?
  "Tells us if any of the requests fired for the current route are
  awaiting responses."
  [current-route-data _]
  (->> (vals current-route-data)
       (map :api/is-loading?)
       (some true?)))

(reg-sub
 :api/content-pending?
 :<- [:api/current-route-data]
 content-pending?)
