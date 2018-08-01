(ns airsonic-ui.test-helpers)

(defn dispatches?
  "Helper to see whether an event is dispatched in a coeffect; `ev` can either
  be a whole vector or a keyword which is interpreted as the event name."
  [cofx ev]
  (let [all-events (conj (get cofx :dispatch-n []) (:dispatch cofx))]
    (boolean (some #(= ev (if (vector? ev) % (first %))) all-events))))
