(ns airsonic-ui.routes
  (:require [bide.core :as r]
            [re-frame.core :as re-frame]))

(def default-route ::login)

(def routes
  [["/" ::login]
   ["/hello" ::main]])

(def protected-routes #{::main})

(defn is-authorized? [login route]
  (or (not (protected-routes route)) login))

;; shouldn't need to change this

;; TODO: This is kind of ugly because at the moment r/navigate! is called twice.
;; the order is click -> hash-change -> {:navigate [bla] :db [bla]} -> (hash-change) -> ...

(defn start-routing!
  "Registers a :navigate effect that can be used for navigation; opts will be
  passed to bide.core/start!"
  [opts]
  (let [router (r/router routes)]
    (re-frame/reg-fx
     :navigate
     (fn [[login route-id params query]]
       (if (is-authorized? login route-id)
         (r/navigate! router route-id params query)
         (do ;; 403 gets a special event
           (println "Not authorized to navigate to " route-id)
           (re-frame/dispatch [::forbidden-route])))))
    (r/start! router opts)))
