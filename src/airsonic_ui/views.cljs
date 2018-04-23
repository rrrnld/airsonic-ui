(ns airsonic-ui.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]))

;; login form

(defn login-form []
  (let [user (r/atom "")
        pass (r/atom "")]
    (fn []
      [:div
       [:div
        [:span "User"]
        [:input {:type "text"
                 :name "user"
                 :on-change #(reset! user (-> % .-target .-value))}]]
       [:div
        [:span "Password"]
        [:input {:type "password" :name "pass" :on-change #(reset! pass (-> % .-target .-value))}]]
       [:div
        [:button {:on-click #(dispatch [::events/authenticate @user @pass])} "Submit"]]])))

;; album list (start page)

(defn album-item [album]
  (let [{:keys [artist artistId name coverArt year id]} album]
    [:div
     ;; link to artist page
     [:a {:href (routes/url-for ::routes/artist-view {:id artistId})} artist]
     " - "
     ;; link to album
     [:a {:href (routes/url-for ::routes/album-view {:id id})} name] (when year (str " (" year ")"))]))

;; TODO: album-list shouldn't know about the structure of content and should just get a list
(defn album-list [content]
  [:div
   [:h2 (str "Recently played")]
   [:ul (for [[idx album] (map-indexed vector (:album content))]
          [:li {:key idx} [album-item album]])]])

;; single album

(defn song-item [songs song]
  [:div (str (:artist song) " - ")
   [:a
    {:on-click #(dispatch [::events/play-songs songs song])}
    (:title song)]])

(defn song-list [songs]
  [:ul (for [[idx song] (map-indexed vector songs)]
         [:li {:key idx} [song-item songs song]])])

(defn album-detail [content]
  [:div
   [:h2 (str (:artist content) " - " (:name content))]
   [song-list (:song content)]])

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
     (case route
       ::routes/main [album-list content]
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
