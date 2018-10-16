(ns airsonic-ui.routes
  (:require [bide.core :as r]
            [cljs.reader :refer [read-string]]
            [re-frame.core :as re-frame]))

(def default-route ::login)

(defonce router
  (r/router [["/" ::login]
             ["/library" ::library]
             ["/library/:criteria" ::library]
             ["/artists" ::artist.overview]
             ["/artists/:id" ::artist.detail]
             ["/album/:id" ::album.detail]
             ["/search" ::search]
             ["/podcast" ::podcast.overview]
             ["/podcast/:id" ::podcast.detail]
             ["/current-queue" ::current-queue]
             ["/about" ::about]]))

;; use this in views to construct a url
(defn url-for
  ([k] (url-for k {} nil))
  ([k params] (url-for k params nil))
  ([k params query] (str "#" (r/resolve router k params query))))

;; which routes need valid login credentials?
(def protected-routes
  #{::library ::artist.overview ::artist.detail ::album.detail ::search
    ::podcast.overview ::podcast.detail})

;; which data should be requested for which route? can either be a vector or a function returning a vector

;; TODO: It's not so nice to have this all so close to the routing logic;
;; it would be nicer to abstract this away, so the components themselves
;; could tell what kind of events they expect

(defmulti -route-events
  "Returns the events that take care of correct data being fetched."
  (fn [route-id & _] route-id))

(defmethod -route-events :default [route-id params query])

(defmethod -route-events ::library
  [route-id {:keys [criteria]} {:keys [page]}]
  (if criteria
    [[:api/request "getScanStatus"]
     [:api/request "getAlbumList2" {:type criteria, :size 20, :offset (* 20 (dec page))}]]
    [:routes/do-navigation [route-id {:criteria "recent"} {:page 1}]]))

(defmethod -route-events ::artist.overview
  [route-id params query]
  [:api/request "getArtists"])

(defmethod -route-events ::artist.detail
  [route-id params query]
  (let [params (select-keys params [:id])]
    [[:api/request "getArtist" params]
     [:api/request "getArtistInfo2" params]]))

(defmethod -route-events ::album.detail
  [route-id params query]
  [:api/request "getAlbum" (select-keys params [:id])])

(defmethod -route-events ::search
  [route-id params query]
  [[:search/restore-term-from-param (:query query)]
   [:api/request "search3" query]])

(defmethod -route-events ::podcast.overview
  [route-id params query]
  [[:api/request "getPodcasts"]])

(defmethod -route-events ::podcast.detail
  [route-id params query]
  ;; this is identical to ::podcast.overview on purpose
  [[:api/request "getPodcasts"]])

;; shouldn't need to change anything below

(defn- n-events?
  "Predicate that tells us whether a vector is suitable for :dispatch-n"
  [ev-vec]
  (or (vector? (first ev-vec))))

(defn route-events
  "Returns a normalized list of event vectors for a given route."
  [route-id params query]
  (let [ev-vec (-route-events route-id params query)]
    (if (n-events? ev-vec) ev-vec [ev-vec])))

;; subscription returning the matched route for the current hashbang

(re-frame/reg-sub :routes/current-route (fn [db _] (:routes/current-route db)))

;; NOTE: There is some duplication here. The route events are provided as a
;; subscription but they are also invoked directly in events.cljs. It didn't
;; seem to justify pulling in a whole library and we need it in our top most view

(re-frame/reg-sub
 :routes/events-for-current-route
 (fn [db _] (re-frame/subscribe [:routes/current-route]))
 (fn [current-route _] (apply route-events current-route)))

;; these are helper effects we can use to navigate; the first two manage an atom
;; holding credentials, which is necessary to restrict certain routes, and the
;; last one is used for actual navigation

;; the event to initialize navigation is implemented so the coeffect map is
;; returned unaltered, we just need access to the current app database for
;; authentication, which we get with an interceptor

(defonce ^:private credentials (atom nil))

(def do-navigation
  "An interceptor which performs the navigation after looking up current
  credentials in the app database"
  (re-frame.core/->interceptor
   :id :routes/do-navigation
   :after (fn do-navigation [context]
            (let [[_ & [route]] (get-in context [:coeffects :event])
                  ;; because :routes/do-navigation is both an event handler and
                  ;; an interceptor, we know that when handling the event (see
                  ;; below) the credentials aren't altered anymore
                  credentials' (get-in context [:coeffects :db :credentials])]
              #_(println "calling do-navigation with" route credentials')
              (reset! credentials credentials')
              (apply r/navigate! router route)
              (dissoc context :event)))))

(re-frame/reg-event-fx :routes/do-navigation do-navigation (fn [& _] nil))

(defn can-access? [route]
  (or (not (protected-routes route))
      (:verified? @credentials)))

(defn on-navigate
  [route-id params query]
  #_(println "calling on-navigate with" route credentials')
  (if (can-access? route-id)
    (re-frame/dispatch [:routes/did-navigate route-id params query])
    (re-frame/dispatch [:routes/unauthorized route-id params query])))

(defn encode-route
  "Takes a parsed route and returns a representation that's suitable for
  transportation in a uri component"
  [route]
  (js/encodeURIComponent (str route)))

(defn decode-route
  "Decodes and encoded route from a uri component into a parsed route"
  [encoded-route]
  (read-string (js/decodeURIComponent encoded-route)))

(defn current-route
  "Returns the parsed route for window.location.hash"
  []
  (r/match router (subs (.. js/window -location -hash) 1)))

;; add the current route to our coeffect map
(re-frame/reg-cofx
 :routes/current-route
 (fn [coeffects _]
   (assoc coeffects :routes/current-route (current-route))))

;; add route into from a URL parameter to our coeffect map
(re-frame/reg-cofx
 :routes/from-query-param
 (fn [coeffects param]
   ;; this allows us to encode a complete route in a url fragment; useful for
   ;; doing redirects
   (let [[_ _ query] (current-route)
         from-param (some-> (get query param) (decode-route))]
     (assoc-in coeffects [:routes/from-query-param param] from-param))))

(defn start-routing!
  "Initializes the router and makes sure the correct events get dispatched."
  ([] (r/start! router {:default default-route
                        :on-navigate on-navigate}))
  ([_] (start-routing!))) ;; <- 1-arity is for the re-frame effect exposed below

(re-frame/reg-fx
 :routes/start-routing start-routing!)
