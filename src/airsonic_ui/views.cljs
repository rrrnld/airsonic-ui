(ns airsonic-ui.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
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

(defn app [user]
  [:div
   [:h2 (str "Currently logged in as " user)]
   [:a {:on-click #(re-frame/dispatch [::events/initialize-db]) :href "#"} "Logout"]])

(defn main-panel []
  [:div
   [:h1 "Airsonic"]
   (if-let [login @(re-frame/subscribe [::subs/login])]
     [app (:u login)]
     [login-form])])
