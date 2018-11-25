(ns airsonic-ui.components.artist.views
  (:require [airsonic-ui.components.collection.views :as collection]
            [airsonic-ui.routes :as routes]
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

(defn artist-links [artist-info]
  (when-not (empty? (select-keys artist-info [:lastFmUrl :musicBrainzId]))
    [:div.field.is-grouped
     (when (:lastFmUrl artist-info)
       [lastfm-link artist-info])
     (when (:musicBrainzId artist-info)
       [musicbrainz-link artist-info])]))

(defn similar-artists
  "Given an artist-info response will return a list of similar artists"
  [{similar-artists :similarArtist}]
  [:div.tags.similar-artists
   (for [{:keys [id name]} similar-artists]
     ^{:key id} [:a.tag {:href (routes/url-for ::routes/artist.detail {:id id})} name])])

(defn detail
  "Creates a nice artist page displaying the artist's name, bio (if available and
  listing) their albums."
  [{:keys [artist artist-info]}]
  [:div
   [:section.hero.is-small>div.hero-body>div.container
    [:h1.title (:name artist)]
    [:div.content
     [lastfm-bio artist-info]
     [artist-links artist-info]]]
   [:section.section.is-small>div.container
    [:h2.subtitle "Albums"]
    [collection/listing (:album artist)]]
   (when (:similarArtist artist-info)
     [:section.section.is-small>div.container
      [:h2.subtitle "Similar artists in your collection"]
      [similar-artists artist-info]])])

(defn alphabetical-listing
  [artists]
  [:div.alphabetical-list
   (for [group artists]
     ^{:key (:name group)}
     [:div.group
      [:h1.subtitle.is-4 (:name group)]
      [:ol.artist-links
       (for [artist (:artist group)]
         (let [href (routes/url-for ::routes/artist.detail (select-keys artist [:id]))]
           ^{:key (:id artist)} [:li [:a {:href href} (:name artist)]]))]])])

(defn overview
  "Displays the alphabetical listing of all artists along with some additional
  information about the collection"
  [{:keys [artists]}]
  (let [artists (:index artists)
        ;; TODO: Calculations in views should be avoided
        artists-count (count (mapcat :artist artists))
        album-count (->> (mapcat :artist artists)
                         (map :albumCount)
                         (reduce +))]
    [:div
     [:section.hero.is-small>div.hero-body
      [:div.container
       [:h1.title "Artists"]
       [:p.subtitle.is-5.has-text-grey [:strong artists-count] " artists in your collection with " [:strong album-count] " albums"]]]
     [:section.section>div.container [alphabetical-listing artists]]]))
