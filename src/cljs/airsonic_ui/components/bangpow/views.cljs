(ns airsonic-ui.components.bangpow.views)

(defn not-found []
  [:section.section>div.container.content
   [:h1 "Oooops..."]
   [:p "That should not have happened. There are multiple things that might have gone wrong:"]
   [:ul
    [:li "You clicked a wrong link. Maybe you copy and pasted it and missed something."]
    [:li "It's a bug in the user interface. In that case: sorry! You can report it " [:a {:href "https://github.com/heyarne/airsonic-ui/issues"
                                                                                             :target "_blank"} "on github"]"."]]])
