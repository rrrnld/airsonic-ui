(ns airsonic-ui.subs
  (:require [re-frame.core :as re-frame]))

;; can be used to query the user's credentials

(re-frame/reg-sub
 ::login
 (fn [db]
   (:login db)))

;; current hashbang

(re-frame/reg-sub
 ::current-route
 (fn [db]
   (:current-route db)))

;; ---

;; TODO: Make this nice and clean
(re-frame/reg-sub
 ::current-album-list
 (fn [db]
   (-> db :response :album)))
