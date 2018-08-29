(ns airsonic-ui.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [airsonic-ui.api.helpers :as api]))

(defn is-booting?
  "The boot process starts with setting up routing and continues if we found
  previous credentials and ends when we receive a response from the server."
  [db _]
  ;; so either we don't have any credentials or they are not verified
  (or (empty? (:routes/current-route db))
      (and (not (empty? (:credentials db)))
           (not (get-in db [:credentials :verified?])))))

(reg-sub ::is-booting? is-booting?)

(defn credentials [db _] (:credentials db))
(reg-sub ::credentials credentials)

(reg-sub
 ::user
 (fn [_ _] [(subscribe [::credentials])])
 (fn [[credentials] _]
   (when credentials {:name (:u credentials)})))

(defn cover-url
  "Provides a convenient way for views to get cover images so they don't have
  to build them themselves and can live a simple and happy life."
  [[{:keys [server u p]}] [_ song size]]
  (api/cover-url server {:u u :p p} song size))

(reg-sub
 ::cover-url
 (fn [_ _] [(subscribe [::credentials])])
 cover-url)

;; user notifications

(defn notifications [db _] (:notifications db))
(reg-sub ::notifications notifications)
