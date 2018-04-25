(ns airsonic-ui.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [airsonic-ui.config :as config]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]))

(defn- >reset!
  "Sends all target values to the given atom"
  [atom]
  #(reset! atom (.. % -target -value)))

;; login form

(defn login-form []
  (let [user (r/atom "")
        pass (r/atom "")
        server (r/atom config/server)]
    (fn []
      [:div
       [:div
        [:span "User"]
        [:input {:type "text"
                 :name "user"
                 :on-change (>reset! user)}]]
       [:div
        [:span "Password"]
        [:input {:type "password"
                 :name "pass"
                 :on-change (>reset! pass)}]]
       [:div
        [:span "Server"]
        [:input {:type "text"
                 :name "server"
                 :on-change (>reset! server)
                 :value @server}]]
       [:div
        [:button {:on-click #(dispatch [::events/authenticate @user @pass @server])} "Submit"]]])))

;; single album

(defn song-item [songs song]
  (let [artist-id (:artistId song)]
    [:div
     [:a
      (when artist-id {:href (url-for ::routes/artist-view {:id artist-id})})
      (:artist song)]
     " - "
     [:a
      {:href "#" :on-click (fn [e]
                             (.preventDefault e)
                             (dispatch [::events/play-songs songs song]))}
      (:title song)]]))

(defn album-detail [content]
  [:div
   [:h2 (str (:artist content) " - " (:name content))]
   (let [songs (:song content)]
     [:ul (for [[idx song] (map-indexed vector songs)]
            [:li {:key idx} [song-item songs song]])])])

;; single artist

(defn album-item [album]
  (let [{:keys [artist artistId name coverArt year id]} album]
    [:div
     ;; link to artist page
     [:a {:href (url-for ::routes/artist-view {:id artistId})} artist]
     " - "
     ;; link to album
     [:a {:href (url-for ::routes/album-view {:id id})} name] (when year (str " (" year ")"))]))

(defn artist-detail [content]
  [:div
   [:h2 (:name content)]
   [:ul (for [[idx album] (map-indexed vector (:album content))]
          [:li {:key idx} [album-item album]])]])

;; TODO: album-list shouldn't know about the structure of content and should just get a list

(defn most-recent [content]
  [:div
   [:h2 "Recently played"]
   [:ul (for [[idx album] (map-indexed vector (:album content))]
          [:li {:key idx} [album-item album]])]])

;; top navigation

(defn content-type
  "Helper to see what kind of server response"
  [content]
  (cond
    (and (vector? (:album content)) (:id content)) :artist
    (vector? (:song content)) :album
    :else :unknown-content))

(defmulti breadcrumbs content-type)

(defmethod breadcrumbs :default [content]
  [:div [:span "Start"]])

(defmethod breadcrumbs :artist [content] 
  [:div
   [:span [:a {:href (url-for ::routes/main)} "Start"]]
   [:span " · " (:name content)]])

(defmethod breadcrumbs :album [content]
  [:div
   [:span [:a {:href (url-for ::routes/main)} "Start"]]
   [:span " · " [:a {:href (url-for ::routes/artist-view {:id (:artistId content)})} (:artist content)]]
   [:span " · " (:name content)]])

;; currently playing / coming next / audio controls...

(defn current-song-info [{:keys [item status]}]
  [:div
   [:b "Currently playing: "]
   [:div (:artist item) " - " (:title item)]
   [:div (:current-time status) "s / " (:duration item) "s"]])

(defn playback-controls []
  [:div
   [:button {:on-click #(dispatch [::events/previous-song])} "previous"]
   [:button {:on-click #(dispatch [::events/toggle-play-pause])} "play / pause"]
   [:button {:on-click #(dispatch [::events/next-song])} "next"]
   [:label [:input {:type "checkbox"}] "shuffle"]
   [:label [:input {:type "checkbox"}] "repeat"]])

(defn bottom-bar []
  [:div
   (if-let [currently-playing @(subscribe [::subs/currently-playing])]
     [current-song-info currently-playing]
     [:span "Currently no song selected"])
   [playback-controls]])

;; putting everything together

(defn app [route params query]
  (let [login @(subscribe [::subs/login])
        content @(subscribe [::subs/current-content])]
    [:div
     [:span (str "Currently logged in as " (:u login))]
     [breadcrumbs content]
     (case route
       ::routes/main [most-recent content]
       ::routes/artist-view [artist-detail content]
       ::routes/album-view [album-detail content])
     [:a {:on-click #(dispatch [::events/initialize-db]) :href "#"} "Logout"]
     [bottom-bar]]))

(defn main-panel []
  (let [[route params query] @(subscribe [::subs/current-route])]
    [:div
     [:h1 "Airsonic"]
     (case route
       ::routes/login [login-form]
       [app route params query])]))
