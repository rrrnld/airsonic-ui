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
  ;; the first parameter is always the current route, the second parameter is
  ;; whatever the subscriptions return as the current content (e.g. album title)
  (fn dispatch-on [[route-id] _] route-id))

(defmethod breadcrumbs :default [_ _]
  [bulma-breadcrumbs "Airsonic"])

(defmethod breadcrumbs ::routes/library [[_ params] _]
  [bulma-breadcrumbs
   [(url-for ::routes/library {:kind "recent"}) "Library"]
   (case (:kind params)
     "recent" "Recently Played"
     "newest" "Newest Additions"
     "starred" "Starred"
     "…")])

(defmethod breadcrumbs ::routes/artist.overview [_ _]
  [bulma-breadcrumbs
   [(url-for ::routes/library {:kind "recent"}) "Library"]
   "Artists"])

(defmethod breadcrumbs ::routes/artist.detail [_ {:keys [artist]}]
  [bulma-breadcrumbs
   [(url-for ::routes/library {:kind "recent"}) "Library"]
   [(url-for ::routes/artist.overview) "Artists"]
   (:name artist)])

(defmethod breadcrumbs ::routes/album.detail [_ {:keys [album]}]
  [bulma-breadcrumbs
   [(url-for ::routes/library {:kind "recent"}) "Library"]
   [(url-for ::routes/artist.overview) "Artists"]
   [(url-for ::routes/artist.detail {:id (:artistId album)}) (:artist album)]
   (:name album)])

(defmethod breadcrumbs ::routes/search [_ _]
  [bulma-breadcrumbs "Search"])

(defmethod breadcrumbs ::routes/podcast.overview [_ _]
  ;; TODO: Detail view
  [bulma-breadcrumbs "Podcasts"])

(defmethod breadcrumbs ::routes/current-queue [_ _]
  [bulma-breadcrumbs "Current Queue"])

(defmethod breadcrumbs ::routes/about [_ _]
  [bulma-breadcrumbs "About"])
