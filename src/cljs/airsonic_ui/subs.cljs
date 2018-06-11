(ns airsonic-ui.subs
  (:require [re-frame.core :as re-frame]))

;; can be used to query the user's credentials

;; FIXME: this is used for cover images and it's quite ugly tbh
(re-frame/reg-sub
 ::login
 (fn [db _]
   (select-keys (:credentials db) [:u :p])))

(re-frame/reg-sub
 ::user
 (fn [{:keys [credentials]} [_]]
   {:name (:u credentials)}))

(re-frame/reg-sub
 ::server
 (fn [db _]
   (get-in db [:credentials :server])))

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
 (fn [[currently-playing]]
   (let [status (:status currently-playing)]
     (and (not (:paused? status))
          (not (:ended? status))))))

;; user notifications

(re-frame/reg-sub
 ::notifications
 (fn [db _]
   (:notifications db)))
