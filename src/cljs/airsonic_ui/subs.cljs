(ns airsonic-ui.subs
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [airsonic-ui.utils.api :as api]))

;; can be used to query the user's credentials

(re-frame/reg-sub
 ::credentials
 (fn [db _]
   (:credentials db)))

(re-frame/reg-sub
 ::user
 (fn [_ _] [(subscribe [::credentials])])
 (fn [[credentials] _]
   {:name (:u credentials)}))

(defn cover-url
  "Provides a convenient way for views to get cover images so they don't have
  to build them themselves and can live a simple and happy life."
  [[{:keys [server u p]}] [_ song size]]
  (api/cover-url server {:u u :p p} song size))

(re-frame/reg-sub
 ::cover-url
 (fn [_ _] [(subscribe [::credentials])])
 cover-url)

;; current hashbang

(re-frame/reg-sub
 ::current-route
 (fn [db _]
   (:current-route db)))

;; TODO: Make this nice and clean

(re-frame/reg-sub
 ::current-content
 (fn [db _]
   (:response db)))

(re-frame/reg-sub
 ; returns info on the current song as is (basically the metadata you can read from the file system)
 ::currently-playing
 (fn [db _]
   (:currently-playing db)))

(re-frame/reg-sub
 ::is-playing?
 (fn [query-v _]
   [(re-frame/subscribe [::currently-playing])])
 (fn [[currently-playing] _]
   (let [status (:status currently-playing)]
     (and (not (:paused? status))
          (not (:ended? status))))))

;; user notifications

(re-frame/reg-sub
 ::notifications
 (fn [db _]
   (:notifications db)))
