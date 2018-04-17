(ns airsonic-ui.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::login
 (fn [db]
   (:login db)))
