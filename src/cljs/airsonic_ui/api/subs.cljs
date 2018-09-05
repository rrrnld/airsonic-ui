(ns airsonic-ui.api.subs
  (:require [clojure.string :as str]
            [re-frame.core :refer [reg-sub]]
            [airsonic-ui.helpers :refer [kebabify]]))

(defn response-for
  "Returns the cached response for a single endpoint"
  [db [_ endpoint params]]
  (get-in db [:api/responses [endpoint params]]))

(reg-sub :api/response-for response-for)

(defn endpoint->kw
  "Given an endpoint like `getAlbumList2`, returns a cleaned keyword like
  `:album-list``.

  Rules: Kebab-case everything, remove prefixes like `get`, `create`, `delete`,
  `update` and strip trailing numbers."
  [endpoint-str]
  (-> (str/replace endpoint-str #"^(get|create|update|delete)" "")
      (str/replace #"\d+$" "")
      (kebabify)))

(defn route-data
  "Given a list of event vectors, returns that responses for all API requests."
  [db [_ route-events]]
  (->> (filter #(= :api/request (first %)) route-events)
       (mapcat (fn [[_ endpoint params]]
                 [(endpoint->kw endpoint) (get-in db [:api/responses [endpoint params]])]))
       (apply hash-map)))

(reg-sub :api/route-data route-data)
