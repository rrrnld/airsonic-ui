(ns airsonic-ui.routes
  (:require [bide.core :as r]
            [re-frame.core :as re-frame]))

(def default-route ::login)

(def router
  (r/router [["/" ::login]
             ["/hello" ::main]
             ["/album/:id" ::album-view]
             ["/artist/:id" ::artist-view]]))

; use this in views to construct a url
(defn url-for [k params]
  (str "#" (r/resolve router k params)))

; which routes need valid login credentials?
(def protected-routes #{::main ::album-view})

; which data should be requested for which route? can either be a vector or a function returning a vector
(def route-data
  {::main [:api-request "getAlbumList2" :albumList2 {:type "recent"}]
   ::album-view (fn [route-id params _]
                  [:api-request "getAlbum" :album {:id (:id params)}])})


(defn data-for
  "Wrapper around route-data so we can call it like a function no matter whether
  the value associated with the route key is a function or not."
  [route params query]
  (if-let [route-data' (route-data route)]
    (if (vector? route-data')
      route-data'
      (route-data' route params query))
    []))

;; shouldn't need to change anything below

;; these are helper effects we can use to navigate; the first two manage an atom
;; holding credentials, which is necessary to restrict certain routes, and the
;; last one is used for actual navigation

(def login (atom nil))

(re-frame/reg-fx
 ::set-credentials
 (fn [credentials]
   (reset! login credentials)))

(re-frame/reg-fx
 ::unset-credentials
 (fn [credentials]
   (reset! login nil)))

(re-frame/reg-fx
 ::navigate
 (fn [[route-id params query]]
   (println "calling ::navigate with" route-id params query)
   (r/navigate! router route-id params query)))

(defn can-access? [route]
  (or (not (protected-routes route)) @login))

(defn on-navigate
  [route-id params query]
  (if (can-access? route-id)
    (re-frame/dispatch [::navigation route-id params query])
    (re-frame/dispatch [::unauthorized route-id params query])))

(defn start-routing!
  "Initializes the router and makes sure the correct events get dispatched."
  []
  (r/start! router {:default default-route
                    :on-navigate on-navigate}))
