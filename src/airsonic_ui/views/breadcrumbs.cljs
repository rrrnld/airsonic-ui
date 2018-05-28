(ns airsonic-ui.views.breadcrumbs
  (:require [airsonic-ui.routes :as routes :refer [url-for]]))

;; Breadcrumbs are implemented in such a way that they provide a stringent
;; hierarchy no matter how you came to the url. They should allow easy
;; navigation upwards that hierarchy (e.g. album -> artist)

(defn content-type
  "Helper to see what kind of server response"
  [content]
  (cond
    (and (vector? (:album content)) (:id content)) :artist
    (vector? (:song content)) :album
    :else :unknown-content))

(defn- bulma-breadcrumbs [& items]
  [:nav.breadcrumb {:aria-label "breadcrumbs"}
   [:ul
    (for [[idx [href label]] (map-indexed vector (butlast items))]
      [:li {:key idx} [:a {:href href} label]])
    [:li.is-active>a (last items)]]])

(defmulti breadcrumbs content-type)

(defmethod breadcrumbs :default [content]
  [bulma-breadcrumbs "Start"])

(defmethod breadcrumbs :artist [content]
  [bulma-breadcrumbs
   [(url-for ::routes/main) "Start"]
   (:name content)])

(defmethod breadcrumbs :album [content]
  [bulma-breadcrumbs
   [(url-for ::routes/main) "Start"]
   [(url-for ::routes/artist-view {:id (:artistId content)}) (:artist content)]
   (:name content)])
