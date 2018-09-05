(ns airsonic-ui.components.artist.views
  (:require [airsonic-ui.components.collection.views :as collection]
            [clojure.string :as str]))

(defn link-button [attrs children]
  [:p.control>a.button.is-small (merge attrs {:target "_blank"}) children])

(defn lastfm-bio
  "Displays the last.fm biography without the 'Read more on Last.fm' link"
  [artist-info]
  (when (:biography artist-info)
    (let [biography (str/replace (:biography artist-info) #"<a .*?>$" "")]
      [:p {:dangerouslySetInnerHTML {:__html biography}}])))

(defn lastfm-link [artist-info]
  [link-button {:href (:lastFmUrl artist-info)} "See on last.fm"])

(defn musicbrainz-link [artist-info]
  (let [href (str "https://musicbrainz.org/artist/" (:musicBrainzId artist-info))]
    [link-button {:href href} "See on musicbrainz"]))

(defn detail
  "Creates a nice artist page displaying the artist's name, bio (if available and
  listing) their albums."
  [{:keys [artist artist-info]}]
  [:div
   [:section.hero>div.hero-body
    [:div.container
     [:h2.title (:name artist)]
     [:div.content
      [lastfm-bio artist-info]
      (when-not (empty? (select-keys artist-info [:lastFmUrl :musicBrainzId]))
        [:div.field.is-grouped
         (when (:lastFmUrl artist-info)
           [lastfm-link artist-info])
         (when (:musicBrainzId artist-info)
           [musicbrainz-link artist-info])])]]]
   [:section.section>div.container [collection/listing (:album artist)]]])
