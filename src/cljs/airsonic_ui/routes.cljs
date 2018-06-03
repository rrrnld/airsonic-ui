(ns airsonic-ui.routes
  (:require [bide.core :as r]
            [re-frame.core :as re-frame]))

(def default-route ::login)

(def router
  (r/router [["/" ::login]
             ["/hello" ::main]
             ["/artist/:id" ::artist-view]
             ["/album/:id" ::album-view]]))

; use this in views to construct a url
(defn url-for
  ([k] (url-for k {}))
  ([k params] (str "#" (r/resolve router k params))))

; which routes need valid login credentials?
(def protected-routes #{::main ::artist-view ::album-view})

; which data should be requested for which route? can either be a vector or a function returning a vector

(defmulti route-data
  "Returns the events that take care of correct data being fetched."
  (fn [route-id & _] route-id))

(defmethod route-data :default [route-id params query] []) ; no data

(defmethod route-data ::main
  [route-id params query]
  [:api-request "getAlbumList2" :albumList2 {:type "recent"
                                             :size 18}])

(defmethod route-data ::artist-view
  [route-id params query]
  [:api-request "getArtist" :artist (select-keys params [:id])])

(defmethod route-data ::album-view
  [route-id params query]
  [:api-request "getAlbum" :album (select-keys params [:id])])

;; shouldn't need to change anything below

;; these are helper effects we can use to navigate; the first two manage an atom
;; holding credentials, which is necessary to restrict certain routes, and the
;; last one is used for actual navigation

(def credentials (atom nil))

(re-frame/reg-fx
 :routes/set-credentials
 (fn [credentials']
   (reset! credentials credentials')))

(re-frame/reg-fx
 :routes/unset-credentials
 (fn []
   (reset! credentials nil)))

(re-frame/reg-fx
 :routes/navigate
 (fn [[route-id params query]]
   (println "calling ::navigate with" route-id params query)
   (r/navigate! router route-id params query)))

(defn can-access? [route]
  (or (not (protected-routes route)) @credentials))

(defn on-navigate
  [route-id params query]
  (if (can-access? route-id)
    (re-frame/dispatch [:routes/navigation route-id params query])
    (re-frame/dispatch [:routes/unauthorized route-id params query])))

(defn start-routing!
  "Initializes the router and makes sure the correct events get dispatched."
  []
  (r/start! router {:default default-route
                    :on-navigate on-navigate}))
