(ns airsonic-ui.components.library.subs
  (:require [re-frame.core :as re-frame]
            [airsonic-ui.config :as conf]))

(defn complete-library
  "Concatenates all responses of one type of library to make paging through
  it a bit easier."
  [responses [_ kind]]
  (let [sorted-albums (->> (filter (fn [[[_ params] _]]
                                     (= kind (:type params))) responses)
                           (sort-by (fn [[[_ params] _]] (:offset params)))
                           (map (comp :album val)))]
    ;; NOTE: we concatenate this manually to avoid duplication; we have to do
    ;; this because fetch more than conf/albums-per-page per page, otherwise we
    ;; can't know whether to show a link to the next page
    (concat (mapcat (partial take conf/albums-per-page) (butlast sorted-albums))
            (last sorted-albums))))

(re-frame/reg-sub
 :library/complete
 :<- [:api/responses-for-endpoint "getAlbumList2"]
 complete-library)
