(ns airsonic-ui.views
  (:require [re-frame.core :as re-frame]
            [airsonic-ui.config :as config]
            [airsonic-ui.subs :as subs]))

(defn login-form []
  [:form {:method "get"
          :action config/server
          :on-click #(js/alert "bang bang! TODO: implement login via form")}
   [:div
    [:span "User"]
    [:input {:type "text" :name "user"}]]
   [:div
    [:span "Password"]
    [:input {:type "password" :name "pass"}]]
   [:div
    [:input {:type "submit" :value "submit"}]]])

(defn app [user]
  [:div
   [:h2 (str "Currently logged in as " user)]])

(defn main-panel []
  [:div
   [:h1 "Airsonic"]
   (if-let [login @(re-frame/subscribe [::subs/login])]
     [app (:u login)]
     [login-form])])
