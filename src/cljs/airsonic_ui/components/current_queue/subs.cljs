(ns airsonic-ui.components.current-queue.subs
  (:require [re-frame.core :as rf]))

(defn queue-info [playlist]
  {:count (count playlist)
   :duration
   (reduce (fn [acc [_ item]]
             (+ acc (:duration item))) 0 (:items playlist))})

(println "registering the sub")

(rf/reg-sub
 :current-queue/info
 :<- [:audio/current-playlist]
 queue-info)
