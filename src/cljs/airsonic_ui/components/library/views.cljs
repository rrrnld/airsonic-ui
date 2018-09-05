(ns airsonic-ui.components.library.views
  (:require [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.components.collection.views :as collection]
            [airsonic-ui.helpers :refer [add-classes]]))

(defn tabs [{:keys [items active-item]}]
  [:div.tabs
   [:ul (for [[idx [route label]] (map-indexed vector items)]
          (let [[_ params _] route]
            ^{:key idx} [:li (when (= params active-item)
                               {:class-name "is-active"})
                         [:a {:href (apply url-for route)} label]]))]])

(defn pagination
  "Builds a pagination, calling `url-fn` for every rendered page link with the
  page as its argument. When `max-pages` is `nil` an infinite pagination
  will be rendered."
  [{:keys [url-fn max-pages current-page]}]
  [:nav.pagination {:role "pagination", :aria-label "pagination"}
   [:a.pagination-previous (if (> current-page 1)
                             {:href (url-fn (dec current-page))}
                             {:disabled true}) "Previous page"]
   [:a.pagination-next (if (= max-pages current-page)
                         {:disabled true}
                         {:href (url-fn (inc current-page))}) "Next page"]
   [:ul.pagination-list
    (when (> current-page 3)
      ^{:key "ellipsis-before"} [:li>span.pagination-ellipsis "…"])
    (for [page (range (max 1 (- current-page 2))
                      (if max-pages
                        (min (+ current-page 3) (inc max-pages))
                        (+ current-page 3)))]
      (let [current-page? (= page current-page)]
        ^{:key page} [(cond-> :li>a.pagination-link
                        current-page? (add-classes :is-current))
                      (cond-> {:href (url-fn page), :aria-label (str "Page " page)}
                        (= page current-page) (assoc :aria-current "page")) page]))
    (when (or (not max-pages) (< current-page (- max-pages 2)))
      ^{:key "ellipsis-after"} [:li>span.pagination-ellipsis "…"])]])

(defn main [route {:keys [scan-status album-list]}]
  (let [[_ {:keys [criteria]} {:keys [page] :or {page 1}}] route
        tab-items [[[::routes/library {:criteria "recent"} nil] "Recently played"]
                   [[::routes/library {:criteria "newest"} nil] "Newest additions"]
                   [[::routes/library {:criteria "starred"} nil] "Starred"]]
        pagination [pagination {:current-page (int page)
                                :max-pages 5
                                :url-fn #(url-for ::routes/library {:criteria criteria} {:page %})}]]
    [:div
     [:section.hero.is-small>div.hero-body>div.container
      [:h2.title "Your library"]
      (if (:count scan-status)
        [:p.subtitle.is-5.has-text-grey [:strong (:count scan-status)] " items"]
        (when (:scanning scan-status)
          [:p.subtitle.is-5.has-text-grey "Scanning…"]))]
     [:section.section>div.container
      [tabs {:items tab-items :active-item {:criteria criteria}}]
      pagination
      [:section.section [collection/listing (:album album-list)]]
      pagination]]))
