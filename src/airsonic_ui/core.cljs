(ns airsonic-ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [bide.core :as r]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]
            [airsonic-ui.views :as views]
            [airsonic-ui.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn on-navigate
  [name params query]
  (println "Route changed to " name params query)
  (re-frame/dispatch [::events/navigate name params query]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (r/start! routes/router {:default ::routes/login
                           :on-navigate on-navigate})
  (dev-setup)
  (mount-root))
