(ns airsonic-ui.db
  (:require [airsonic-ui.routes :as routes]))

(def default-db
  {:active-requests 0
   ;; because navigate! executes asynchronously we force to display the login screen first
   :current-route [routes/default-route]})
