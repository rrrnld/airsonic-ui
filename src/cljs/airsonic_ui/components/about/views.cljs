(ns airsonic-ui.components.about.views)

(defn- link
  "Creates an external link"
  [url label]
  [:a {:href url, :target "_blank"} label])

(defn about []
  [:section.section>div.container.content
   [:h1 "About"]
   [:p "This is a frontend for " [link "https://airsonic.github.io/" "Airsonic"] ", a free and open source media server. You can think of Airsonic as a Spotify that you can run out of a shoebox in your bedroom, enabling you to listen to your own music wherever you are."]
   [:h2 "Motivation"]
   [:p "The current frontend of airsonic has been written quite a long time ago - eons on a web development timescale, where the clocks tick a bit different. While it has many features it has unfortunately aged noticeably. It does not work well on mobile and some features, such as sharing parts of your music library, require Adobe Flash, leaving them practically unusable and insecure."]
   [:p "This fronted aims to provide a focused subset. Its focus for now is on playing and sharing music. Setting up the airsonic instance has to be done via the old interface, as does podcast management."]
   [:h2 "Thank you"]
   [:p "This web application is built upon the work of many others. A special thank you goes out to…"]
   [:ul
    [:li "The authors of " [link "https://github.com/facebook/react" "React"] ", " [link "https://github.com/reagent-project/reagent" "reagent"] " and " [link "https://github.com/Day8/re-frame" "re-frame"]]
    [:li "Everybody who has contributed to " [link "https://github.com/jgthms/bulma" "Bulma"]]
    [:li "Everyone involved in bringing " [link "https://clojure.org/" "Clojure and ClojureScript"] " into the world, also thheller for creating " [link "https://shadow-cljs.github.io/docs/UsersGuide.html" "shadow-cljs"]]
    [:li "Of course, the people behind " [link "http://www.subsonic.org/pages/index.jsp" "Sub-"] ", " [link "https://github.com/Libresonic/libresonic" "Libre-"] " and " [link "https://airsonic.github.io/" "especially Airsonic"]]
    [:li "Many others that have been creating tooling or libraries that I use in some way or another."]]
   [:h2 "Contact"]
   [:p "The airsonic community can be found on " [link "https://riot.im/app/#/room/#airsonic:matrix.org" "Matrix"]
    " and IRC (#airsonic on freenode). There is also a " [link "https://www.reddit.com/r/airsonic/" "dedicated Subreddit"] ". If you think you found a bug in the frontend, it's probably a good idea to " [link "https://github.com/heyarne/airsonic-ui/issues" "report it on github"] ". I hope you have fun with the software! If you want to say thanks or have a use case that you feel could be covered, feel free to get in touch. Just know that everybody involved does this in their free time."]])
