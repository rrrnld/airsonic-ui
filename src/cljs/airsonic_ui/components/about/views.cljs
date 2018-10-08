(ns airsonic-ui.components.about.views)

(defn about []
  [:section.section>div.container.content
   [:h1 "About"]
   [:p "This is a frontend for " [:a {:href "https://airsonic.github.io/"
                                      :target "_blank"} "airsonic"] ", a free and open source media server. You can think of airsonic as a Spotify that you can run out of a shoebox in your bedroom, enabling you to listen to your own music wherever you are."]
   [:h2 "Motivation"]
   [:p "The current frontend of airsonic has been written quite a long time ago - eons on a web-development timescale, where the clocks tick a bit different. While it has many features it has unfortunately aged noticeably. It does not work well on mobile and some features, such as sharing parts of your music library, require Adobe Flash, leaving them practically unusable and insecure."]
   [:p "This fronted aims to provide a focused subset. Its focus for now is on playing and sharing music. Setting up the airsonic instance has to be done via the old interface, as does podcast management."]
   [:h2 "Contact"]
   [:p "The airsonic community can be found on " [:a {:href "https://riot.im/app/#/room/#airsonic:matrix.org"
                                                      :target "_blank"} "Matrix"]
    " and IRC (#airsonic on freenode). There is also a " [:a {:href "https://www.reddit.com/r/airsonic/"
                                                              :target "_blank"} "dedicated Subreddit"] ". If you think you found bugs in the frontend, it's probably a good idea to " [:a {:href "https://github.com/heyarne/airsonic-ui/issues"
                                                                                                                                                                                          :target "_blank"} "report them on github"] ". I hope you have fun with the software! If you want to say thanks or have a use case that you feel could be covered, feel free to get in touch. Just know that everybody involved does this in their free time."]])
