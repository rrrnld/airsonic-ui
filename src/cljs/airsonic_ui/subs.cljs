(ns airsonic-ui.subs
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [airsonic-ui.audio.playlist :as playlist]
            [airsonic-ui.utils.api :as api]))

(defn is-booting?
  "The boot process starts with setting up routing and continues if we found
  previous credentials and ends when we receive a response from the server."
  [db _]
  ;; so either we don't have any credentials or they are not verified
  (or (empty? (:current-route db))
      (and (not (empty? (:credentials db)))
           (not (get-in db [:credentials :verified?])))))

(re-frame/reg-sub ::is-booting? is-booting?)

(defn credentials [db _] (:credentials db))
(re-frame/reg-sub ::credentials credentials)

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

;; user notifications

(defn notifications [db _] (:notifications db))
(re-frame/reg-sub ::notifications notifications)
