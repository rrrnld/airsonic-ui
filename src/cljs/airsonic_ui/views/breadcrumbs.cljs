(ns airsonic-ui.views.breadcrumbs
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.routes :as routes :refer [url-for]]))

;; Breadcrumbs are implemented in such a way that they provide a stringent
;; hierarchy no matter how you came to the url. They should allow easy
;; navigation upwards that hierarchy (e.g. album -> artist)

(defn- bulma-breadcrumbs [& items]
  (let [content-pending? @(subscribe [:api/content-pending?])]
    [:div.container
     [:nav.breadcrumb {:aria-label "breadcrumbs"}
      [:ul
       (for [[idx [href label]] (map-indexed vector (butlast items))]
         [:li {:key idx} [:a {:href href} label]])
       [:li.is-active>a (last items)
        (when content-pending? [:span.loader])]]]]))

(defmulti breadcrumbs
  (fn dispatch-on [[route-id] content] route-id))

(defmethod breadcrumbs :default [_ _]
  [bulma-breadcrumbs "Start"])

(def start [(url-for ::routes/library) "Start"])

(defmethod breadcrumbs ::routes/artist.overview [_ _]
  [bulma-breadcrumbs start "Artists"])

(defmethod breadcrumbs ::routes/artist.detail [_ {:keys [artist]}]
  [bulma-breadcrumbs start
   [(url-for ::routes/artist.overview) "Artists"]
   (:name artist)])

(defmethod breadcrumbs ::routes/album.detail [_ {:keys [album]}]
  [bulma-breadcrumbs start
   [(url-for ::routes/artist.overview) "Artists"]
   [(url-for ::routes/artist.detail {:id (:artistId album)}) (:artist album)]
   (:name album)])

(defmethod breadcrumbs ::routes/search [_ _]
  [bulma-breadcrumbs start "Search"])

(defmethod breadcrumbs ::routes/podcast.overview [_ _]
  ;; TODO: Detail view
  [bulma-breadcrumbs start "Podcasts"])

(defmethod breadcrumbs ::routes/current-queue [_ _]
  [bulma-breadcrumbs start "Current Queue"])

(defmethod breadcrumbs ::routes/about [_ _]
  [bulma-breadcrumbs start "About"])
