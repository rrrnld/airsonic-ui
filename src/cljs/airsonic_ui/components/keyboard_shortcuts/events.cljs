(ns airsonic-ui.components.keyboard-shortcuts.events
  (:require [re-frame.core :as rf]
            [re-pressed.core :as rp]
            [airsonic-ui.components.keyboard-shortcuts.config :as config]))

(rf/reg-event-fx
 ::init-shortcuts
 (fn []
   (let [event-keys (map (juxt #(nth % 2) #(nth % 3)) config/keymap)
         prevent-default-keys (mapcat last event-keys)]
     {:dispatch-n [[::rp/add-keyboard-event-listener "keydown"]
                   [::rp/set-keydown-rules {:event-keys event-keys
                                            :prevent-default-keys prevent-default-keys}]]})))
