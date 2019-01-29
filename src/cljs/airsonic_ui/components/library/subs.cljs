(ns airsonic-ui.components.library.subs
  (:require [re-frame.core :as rf]
            [airsonic-ui.config :as conf]))

;; first some helper functions to make the structure a bit clearer

(defn filter-response-kind
  "Takes all library responses and returns only the ones matching a specific kind"
  [kind responses]
  (filter (fn [[[_ params] _]]
            (= kind (:type params))) responses))

(defn partition-responses
  "Returns a map of responses, where each response is neatly mapped to the page
  it show on."
  [kind responses]
  (->> (filter-response-kind kind responses)
       (sort-by (fn [[[_ params] _]] (:offset params)))
       (mapcat (fn [[[_ params] {albums :album}]]
                 (let [start-page (/ (:offset params) conf/albums-per-page)]
                   (zipmap (drop start-page (range))
                           (partition-all conf/albums-per-page albums)))))
       (into (sorted-map))))

;; `complete-library` is the subscription that is actually exported

(defn paginated-library
  "Returns a sorted map that can be used to access the library content loaded
  from the server. Each key represents a page and the associated value
  represents the page's content."
  [responses [_ kind]]
  ;; note that we "humanize" the keys, meaning page 1 is the page with offset 0
  (->> (partition-responses kind responses)
       (map (fn [[k v]] [(inc k) v]))
       (into (sorted-map))))

(rf/reg-sub
 :library/paginated
 :<- [:api/responses-for-endpoint "getAlbumList2"]
 paginated-library)

