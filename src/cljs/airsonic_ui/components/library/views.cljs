(ns airsonic-ui.components.library.views
  (:require [re-frame.core :refer [subscribe]]
            [bulma.tabs :refer [tabs]]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.components.collection.views :as collection]))

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

(defn tab-items [[current-id current-params :as current-route]]
  (->>
   [[[::routes/library {:kind "recent"}] "Recently played"]
    [[::routes/library {:kind "newest"}] "Newest additions"]
    [[::routes/library {:kind "starred"}] "Starred"]
    [[::routes/artist.overview] "Artists"]]
   (map (fn [[[id params :as route] label]]
          (cond-> {:href (apply routes/url-for route)
                   :label label}
            (and (= id current-id)
                 (= (:kind params) (:kind current-params)))
            (assoc :active? true))))))

(defn tab-section [current-route]
  [:section.section.ui-tab-bar.is-small>div.container
   [tabs {:items (tab-items current-route)}]])

(defn main
  "Renders the pagination and shows a list of all albums with their cover art.
  The first parameter is the route that's passed in, the second one is the
  content that has been fetched for that route."
  [[_ {:keys [kind]} {:keys [page] :or {page 1}} :as current-route]
   {:keys [scan-status]}]
  (println "scan-status" scan-status)
  (let [library @(subscribe [:library/paginated kind])
        page (int page)
        current-items (get library page)
        url-fn #(url-for ::routes/library {:kind kind} {:page %})
        pagination-links [pagination {:current-page page
                                      :items library
                                      :url-fn url-fn}]]
    [:div
     [tab-section current-route]
     [:section.hero.single-line.is-small>div.hero-body>div.container
      [:h2.title "Your library"]
      (if (:count scan-status)
        [:p.subtitle.is-5.has-text-grey [:strong (:count scan-status)] " items"]
        (when (:scanning scan-status)
          [:p.subtitle.is-5.has-text-grey "Scanning…"]))]
     [:section.section.is-tiny>div.container pagination-links]
     [:section.section.is-tiny>div.container [collection/listing current-items]]
     [:section.section.is-tiny>div.container pagination-links]]))
