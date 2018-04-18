(ns airsonic-ui.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]))

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

(defn app [current-page]
  (let [login @(re-frame/subscribe [::subs/login])]
    [:div
     [:h2 (str "Currently logged in as " (:u login))]
     [:a {:on-click #(re-frame/dispatch [::events/initialize-db]) :href "#"} "Logout"]]))

(defn main-panel []
  (let [current-page @(re-frame/subscribe [::subs/current-page])]
    [:div
     [:h1 "Airsonic"]
     (case current-page
       ::routes/login [login-form]
       [app current-page])]))
