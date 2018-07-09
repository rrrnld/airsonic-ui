(ns airsonic-ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            ;; 3rd party effects / coeffects
            [day8.re-frame.http-fx]
            [akiroz.re-frame.storage :as storage]
            ;; our app
            [airsonic-ui.audio] ; <- just registers effects here
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]
            [airsonic-ui.views :as views]
            [airsonic-ui.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))

(defn ^:export init []
  (storage/reg-co-fx! :airsonic-ui {:fx :store
                                    :cofx :store})
  (re-frame/dispatch-sync [::events/initialize-app])
  (dev-setup)
  (mount-root))
