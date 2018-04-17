(ns airsonic-ui.events
  (:require [re-frame.core :as re-frame]
            [airsonic-ui.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_]
   db/default-db))
