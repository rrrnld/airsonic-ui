(ns airsonic-ui.views.breadcrumbs
  (:require [airsonic-ui.routes :as routes :refer [url-for]]))

;; Breadcrumbs are implemented in such a way that they provide a stringent
;; hierarchy no matter how you came to the url. They should allow easy
;; navigation upwards that hierarchy (e.g. album -> artist)

(defn page-type
  "Helper to see what kind of view we're currently dealing with"
  [content]
  (case (set (keys content))
    #{:artist :artist-info} :artist
    #{:album} :album
    #{:search} :search
    #{:podcasts} :podcast
    :other-content))

(defn- bulma-breadcrumbs [& items]
  [:div.container>nav.breadcrumb {:aria-label "breadcrumbs"}
   [:ul
    (for [[idx [href label]] (map-indexed vector (butlast items))]
      [:li {:key idx} [:a {:href href} label]])
    [:li.is-active>a (last items)]]])

(defmulti breadcrumbs page-type)

(defmethod breadcrumbs :default [content]
  [bulma-breadcrumbs "Start"])

(def start [(url-for ::routes/library) "Start"])

(defmethod breadcrumbs :artist [{:keys [artist]}]
  [bulma-breadcrumbs start
   (:name artist)])

(defmethod breadcrumbs :album [{:keys [album]}]
  [bulma-breadcrumbs start
   [(url-for ::routes/artist.detail {:id (:artistId album)}) (:artist album)]
   (:name album)])

(defmethod breadcrumbs :search [_]
  [bulma-breadcrumbs start "Search"])

(defmethod breadcrumbs :podcast [{:keys [channel]}]
  ;; TODO: Detail view
  [bulma-breadcrumbs start "Podcasts"])
