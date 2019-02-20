(ns airsonic-ui.components.keyboard-shortcuts.config)

;; this keymap has the following structure:
;; [[readable-key readable-description event-vector event-keys]
;;  ...]

(def keymap
  [["Space" "Toggle play / pause"
    [:audio-player/toggle-play-pause]
    [{:keyCode 32}]]
   ["←" "Previous song"
    [:audio-player/previous-song]
    [{:keyCode 37}]]
   ["→" "Next song"
    [:audio-player/next-song]
    [{:keyCode 39}]]
   ["+" "Increase volume"
    [:audio-player/increase-volume]
    [{:keyCode 171}]]
   ["-" "Decrease volume"
    [:audio-player/decrease-volume]
    [{:keyCode 173}]]
   ["?" "Show / hide keyboard shortcut help"
    [:bulma.modal.events/toggle :keyboard-shortcuts-help]
    [{:keyCode 63}]]])
