(ns airsonic-ui.components.collection.views
  (:require
   [airsonic-ui.views.icon :refer [icon]]
   [airsonic-ui.views.song :as song]))

(defn format-duration [seconds]
  (let [hours (quot seconds 3600)
        minutes (quot (rem seconds 3600) 60)
        seconds (rem seconds 60)]
    (-> (cond-> ""
          (> hours 0) (str hours "h ")
          (> minutes 0) (str minutes "m "))
        (str seconds "s"))))

(defn collection-info [{:keys [songCount duration year]}]
  (vec (cond-> [:ul.is-smaller.collection-info
                [:li [icon :audio-spectrum] (str songCount " tracks")]
                [:li [icon :clock] (format-duration duration)]]
         year (conj [:li [icon :calendar] (str "Released in " year)]))))

(defn detail
  "Lists all songs in an album"
  [{:keys [album]}]
  [:div
   [:section.hero>div.hero-body
    [:div.container
     [:h2.title (:name album)]
     [:h3.subtitle (:artist album)]
     [collection-info album]]]
   [:section.section>div.container [song/listing (:song album)]]])
