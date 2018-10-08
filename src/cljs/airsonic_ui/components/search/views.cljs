(ns airsonic-ui.components.search.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [goog.functions :refer [debounce]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.views.song :as song]
            [airsonic-ui.views.cover :refer [card]]))

(defn form []
  (let [search-term @(subscribe [:search/current-term])
        throttled-search (debounce #(dispatch [:search/do-search (.. % -target -value)]) 100)]
    (fn []
      [:form {:on-submit #(.preventDefault %)}
       [:div.feld>p.control
        [:input.input {:on-change (fn [e]
                                    ;; the event might be gone when we the dispatched
                                    ;; function is fired, we need to persist it
                                    (.persist e)
                                    (throttled-search e))
                       :default-value search-term
                       :placeholder "Search"}]]])))


(defn artist-results [{:keys [artist]}]
  [:div.columns.is-multiline.is-mobile
   (for [[idx artist] (map-indexed vector artist)]
     (let [url #(url-for ::routes/artist.detail (select-keys % [:id]))]
       ^{:key idx} [:div.column.is-2
                    [card artist
                     :url-fn url
                     :content [:div>a
                               {:href (url artist), :title (:name artist)}
                               (:name artist)]]]))])

(defn album-results [{:keys [album]}]
  [:div.columns.is-multiline.is-mobile
   (for [[idx album] (map-indexed vector album)]
     (let [url #(url-for ::routes/album.detail (select-keys % [:id]))
           title (str (:name album) " (" (:artist album) ")")]
       ^{:key idx} [:div.column.is-2 [card album
                                      :url-fn url
                                      :content [:div>a
                                                {:href (url album), :title title}
                                                title]]]))])

(defn song-results [{:keys [song]}]
  [song/listing song])

(defn results [{:keys [search]}]
  (let [term @(subscribe [:search/current-term])]
    [:section.section>div.container
     [:h2.title (str "Search results for \"" term "\"")]
     (if (empty? search)
       [:p "The server returned no results."]
       [:div.content
        (when-not (empty? (:artist search))
          [:section.section.is-small
           [:h3.subtitle.is-5 "Artists"]
           [artist-results search]])
        (when-not (empty? (:album search))
          [:section.section.is-small
           [:h3.subtitle.is-5 "Albums"]
           [album-results search]])
        (when-not (empty? (:song search))
          [:section.section.is-small
           [:h3.subtitle.is-5 "Songs"]
           [song-results search]])])]))
