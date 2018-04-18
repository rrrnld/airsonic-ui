(ns airsonic-ui.routes
  (:require [bide.core :as r]))

;; routing is started in core.cljs

(def default ::login)

(def router
  (r/router [["/" ::login]
             ["/hello" ::main]]))

;; routes that need valid credentials

(def protected #{::main})
