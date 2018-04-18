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
  [id params query]
  (println "Route changed to " id params query)
  (re-frame/dispatch [::events/hash-change id params query]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))

(defn ^:export init []
  (routes/start-routing! {:default routes/default-route
                          :on-navigate on-navigate})
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
