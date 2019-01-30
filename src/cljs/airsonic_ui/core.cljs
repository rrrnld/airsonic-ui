(ns airsonic-ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            ;; 3rd party effects / coeffects
            [day8.re-frame.http-fx]
            [akiroz.re-frame.storage :as storage]

            ;; our app; namespaces that are just required but not used register
            ;; event handlers, effect handlers or subscriptions
            [airsonic-ui.audio.core]
            [airsonic-ui.api.events]
            [airsonic-ui.api.subs]
            [airsonic-ui.components.audio-player.events]
            [airsonic-ui.components.keyboard-shortcuts.events :as keyboard]
            [airsonic-ui.components.library.subs]
            [airsonic-ui.components.search.events]
            [airsonic-ui.components.search.subs]
            [airsonic-ui.events :as events]
            [airsonic-ui.views :as views]
            [airsonic-ui.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))

(defn ^:export init []
  (storage/reg-co-fx! :airsonic-ui {:fx :store, :cofx :store})
  (rf/dispatch-sync [::events/initialize-app])
  (rf/dispatch [::keyboard/init-shortcuts])
  (dev-setup)
  (mount-root))
