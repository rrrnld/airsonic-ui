(ns airsonic-ui.components.library.subs
  (:require [re-frame.core :as re-frame]
            [airsonic-ui.config :as conf]))

(defn complete-library
  "Concatenates all responses of one type of library to make paging through
  it a bit easier."
  [responses [_ kind]]
  (->> (filter (fn [[[_ params] _]]
                 (= kind (:type params))) responses)
       (sort-by (fn [[[_ params] _]] (:offset params)))
       (mapcat (fn [[_ vals]] (:album vals)))))

(re-frame/reg-sub
 :library/complete
 :<- [:api/responses-for-endpoint "getAlbumList2"]
 complete-library)
