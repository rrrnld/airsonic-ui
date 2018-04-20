(ns airsonic-ui.views
  (:require [re-frame.core :as re-frame]
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
        [:button {:on-click #(re-frame/dispatch [::events/authenticate @user @pass])} "Submit"]]])))

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
   [:ul
    (map-indexed
     (fn [idx album]
       [:li {:key idx} [album-item album]])
     (:album content))]])

;; single album

(defn song-item [song]
  [:div (str (:artist song) " - " (:title song))])

(defn song-list [songs]
  [:ul
   (map-indexed
    (fn [idx song] [:li {:key idx} [song-item song]])
    songs)])

(defn album-detail [content]
  [:div
   [:h2 (str (:artist content) " - " (:name content))]
   [song-list (:song content)]])

;; putting everything together

(defn app [route params query]
  (let [login @(re-frame/subscribe [::subs/login])
        content @(re-frame/subscribe [::subs/current-content])]
    [:div
     [:span (str "Currently logged in as " (:u login))]
     (case route
       ::routes/main [album-list content]
       ::routes/album-view [album-detail content])
     [:a {:on-click #(re-frame/dispatch [::events/initialize-db]) :href "#"} "Logout"]]))

(defn main-panel []
  (let [[route params query] @(re-frame/subscribe [::subs/current-route])]
    [:div
     [:h1 "Airsonic"]
     (case route
       ::routes/login [login-form]
       [app route params query])]))
