(ns airsonic-ui.test-helpers)

(defn dispatches?
  "Helper to see whether an event is dispatched in a coeffect; `ev` can either
  be a whole vector or a keyword which is interpreted as the event name."
  [cofx ev]
  (let [all-events (conj (get cofx :dispatch-n []) (:dispatch cofx))]
    (boolean (some #(= ev (if (vector? ev) % (first %))) all-events))))

(defn rand-str
  "Generates a random string; ported from https://stackoverflow.com/a/27747377/2345852"
  ([] (rand-str 40))
  ([len]
   (let [arr (js/Uint8Array. (/ len 2))]
     (.. js/window -crypto (getRandomValues arr))
     (.. js/Array
         (from arr #(-> (str 0 (.toString % 16))
                        (.substr -2)))
         (join "")))))
