(ns airsonic-ui.components.library.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.components.collection.views :as collection]))

(defn tabs [{:keys [items active-item]}]
  [:div.tabs
   [:ul (for [[idx [route label]] (map-indexed vector items)]
          (let [[_ params _] route]
            ^{:key idx} [:li (when (= params active-item)
                               {:class-name "is-active"})
                         [:a {:href (apply url-for route)} label]]))]])

;; this variable determines how many pages before the first known page we should list
(def page-padding 2)

(defn pagination-link
  "One of many numbered links to a page"
  [current-page page href]
  (let [current-page? (= page current-page)]
    [(if current-page?
       :a.pagination-link.is-current
       :a.pagination-link)
     (cond-> {:href href, :aria-label (str "Page " page)}
       current-page? (assoc :aria-current "page")) page]))

(defn pagination
  "Builds a pagination, calling `url-fn` for every rendered page link with the
  page as its argument. When `max-pages` is `nil` an infinite pagination
  will be rendered."
  [{:keys [items current-page url-fn]}]
  ;; NOTE: This is currently slightly flawed. We don't have any good way to
  ;; know whether we're on the last possible page so we take the last loaded
  ;; page instead
  (let [num-pages (last (keys items))
        first-page? (= current-page 1)
        pages (range (max 1 (- current-page page-padding))
                     (min (inc (+ current-page page-padding)) (inc num-pages)))]
    [:nav.pagination.is-centered {:role "pagination", :aria-label "pagination"}
     ;; now we add buttons to progress one page in each direction
     [:a.pagination-previous (if first-page?
                               {:disabled true}
                               {:href (url-fn (dec current-page))}) "Previous page"]
     [:a.pagination-next {:href (url-fn (inc current-page))} "Next page"]
     ;; and here we modify the links around our current page
     [:ul.pagination-list
      ;; some indication that there are previous pages
      (when (> current-page (+ page-padding 2))
        [:li [pagination-link current-page 1 (url-fn 1)]])
      (when (> current-page (+ page-padding 1))
        [:li>span.pagination-ellipsis "…"])
      ;; all pagination links around our current page
      (for [page pages]
        ^{:key page} [:li [pagination-link current-page page (url-fn page)]])
      ;; some indication that there are more pages after
      (when (< current-page (- num-pages page-padding))
        [:li>span.pagination-ellipsis "…"])
      (when (< current-page (- num-pages page-padding))
        [:li [pagination-link current-page num-pages (url-fn num-pages)]])]]))

(def tab-items [[[::routes/library {:kind "recent"} nil] "Recently played"]
                [[::routes/library {:kind "newest"} nil] "Newest additions"]
                [[::routes/library {:kind "starred"} nil] "Starred"]])

(defn main
  "Renders the pagination and shows a list of all albums with their cover art.
  The first parameter is the route that's passed in, the second one is the
  content that has been fetched for that route."
  [[_ {:keys [kind]} {:keys [page]
                      :or {page 1}}]
   {:keys [scan-status]}]
  (let [page (int page)
        library @(subscribe [:library/paginated kind])
        current-items (get library page)
        url-fn #(url-for ::routes/library {:kind kind} {:page %})
        pagination [pagination {:current-page page
                                :items library
                                :url-fn url-fn}]]
    [:div
     [:section.hero.is-small>div.hero-body>div.container
      [:h2.title "Your library"]
      (if (:count scan-status)
        [:p.subtitle.is-5.has-text-grey [:strong (:count scan-status)] " items"]
        (when (:scanning scan-status)
          [:p.subtitle.is-5.has-text-grey "Scanning…"]))]
     [:section.section>div.container
      [tabs {:items tab-items :active-item {:kind kind}}]
      pagination
      [:section.section [collection/listing current-items]]
      pagination]]))
