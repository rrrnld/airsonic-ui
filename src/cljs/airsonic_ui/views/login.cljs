(ns airsonic-ui.views.login
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            [airsonic-ui.events :as events]))

(defn- >reset!
  "Sends an event's target values to the given atom"
  [atom]
  #(reset! atom (.. % -target -value)))

;; login form

(defn login-form []
  (let [user (r/atom "")
        pass (r/atom "")
        server (r/atom (.. js/window -location -origin))
        submit (fn [e]
                 (.preventDefault e)
                 (dispatch [:credentials/verification-request @user @pass @server]))]
    (fn []
      [:section.hero.is-fullheight>div.hero-body
       [:div.container.has-text-centered>div.column.is-4.is-offset-4
        [:h3.title.has-text-grey "Airsonic"]
        [:p.subtitle.has-text-grey "Please login to proceed"]
        [:div.box
         [:form {:on-submit submit}
          [:div.field>div.control
           [:input.input.is-large {:type "text"
                                   :name "user"
                                   :placeholder "Username"
                                   :on-change (>reset! user)}]]
          [:div.field>div.control
           [:input.input.is-large {:type "password"
                                   :name "pass"
                                   :placeholder "Password"
                                   :on-change (>reset! pass)}]]
          [:div.field>div.control
           [:input.input.is-large {:type "text"
                                   :name "server"
                                   :on-change (>reset! server)
                                   :value @server}]]
          [:button.button.is-block.is-info.is-large.is-fullwidth {:type "submit"} "Submit"]]]]])))
