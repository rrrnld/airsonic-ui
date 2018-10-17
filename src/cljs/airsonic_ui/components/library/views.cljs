(ns airsonic-ui.components.library.views
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.config :as conf]
            [airsonic-ui.routes :as routes :refer [url-for]]
            [airsonic-ui.components.collection.views :as collection]
            [airsonic-ui.helpers :refer [add-classes]]))

(defn tabs [{:keys [items active-item]}]
  [:div.tabs
   [:ul (for [[idx [route label]] (map-indexed vector items)]
          (let [[_ params _] route]
            ^{:key idx} [:li (when (= params active-item)
                               {:class-name "is-active"})
                         [:a {:href (apply url-for route)} label]]))]])

;; the pagination should be used like this
;; [pagination {:per-page 12
;;              :max-pages nil
;;              :url-fn generate-url
;;              :current-page 0
;;              :items [,,,]}]

(defn num-pages [items per-page max-pages]
  (min (Math/ceil (/ (count items) per-page)) max-pages))

(defn pagination
  "Builds a pagination, calling `url-fn` for every rendered page link with the
  page as its argument. When `max-pages` is `nil` an infinite pagination
  will be rendered."
  [{:keys [items per-page max-pages current-page url-fn]
    :or {max-pages (.-MAX_VALUE js/Number)}}]
  (let [num-pages (num-pages items per-page max-pages)
        first-page? (= current-page 1)
        last-page? (= current-page num-pages)]
    (println "range"
             (count items)
             "num-pages"
             num-pages)
    [:nav.pagination {:role "pagination", :aria-label "pagination"}
     [:a.pagination-previous (if first-page?
                               {:disabled true}
                               {:href (url-fn (dec current-page))}) "Previous page"]
     [:a.pagination-next (if last-page?
                           {:disabled true}
                           {:href (url-fn (inc current-page))}) "Next page"]
     [:ul.pagination-list
      (when (> current-page 3)
        ^{:key "ellipsis-before"} [:li>span.pagination-ellipsis "…"])
      (for [page (range (max 1 (- current-page 2))
                        (min (+ current-page 3) (inc num-pages)))]
        (let [current-page? (= page current-page)]
          ^{:key page} [(cond-> :li>a.pagination-link
                          current-page? (add-classes :is-current))
                        (cond-> {:href (url-fn page), :aria-label (str "Page " page)}
                          current-page? (assoc :aria-current "page")) page]))
      (when (< current-page (- num-pages 2))
        ^{:key "ellipsis-after"} [:li>span.pagination-ellipsis "…"])]]))

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
  (let [library @(subscribe [:library/complete kind])
        ;; FIXME: vv Views shouldn't do calculations vv
        visible (->> (drop (* (dec page) conf/albums-per-page) library)
                     (take conf/albums-per-page))
        url-fn #(url-for ::routes/library {:kind kind} {:page %})
        pagination [pagination {:current-page (int page)
                                :per-page conf/albums-per-page
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
      [:section.section [collection/listing visible]]
      pagination]]))
