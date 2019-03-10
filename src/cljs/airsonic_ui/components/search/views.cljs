(ns airsonic-ui.components.search.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [goog.functions :refer [debounce]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.components.collection.views :refer [song-table]]
            [airsonic-ui.components.debug :refer [debug]]
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

(defn result-cards [items]
  [:div.columns.is-multiline.is-mobile
   (for [[url item] items]
     ^{:key url} [:div.column.is-one-fifth-tablet.is-one-third-mobile
                  [card item
                   :url-fn (constantly url)
                   :content [:div>a
                             {:href url, :title (:name item)}
                             (:name item)]]])])

(defn- artist-url [artist]
  (url-for ::routes/artist.detail (select-keys artist [:id])))

(defn artist-results [{:keys [artist]}]
  [result-cards (map (juxt artist-url identity) artist)])

(defn- album-url [album]
  (url-for ::routes/album.detail (select-keys album [:id])))

(defn album-results [{:keys [album]}]
  [result-cards (map (juxt album-url identity) album)])

(defn song-results [{songs :song}]
  []
  [song-table songs])

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
