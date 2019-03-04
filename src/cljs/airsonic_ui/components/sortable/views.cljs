(ns airsonic-ui.components.sortable.views
  (:require [reagent.core :as r]
            [clojure.string :as str]
            ["react-sortable-hoc" :refer [SortableHandle SortableElement
                                          SortableContainer]]))
;; this code is taken and adapted from https://github.com/reagent-project/reagent/blob/72c95257c13e5de1531e16d1a06da7686041d3f4/examples/react-sortable-hoc/src/example/core.cljs

(defn make-wrapper [{:keys [container render-item]}]
  (let [SortableItem (SortableElement.
                      (r/reactify-component render-item))]
    (SortableContainer.
     (r/reactify-component
      (fn [{:keys [items]}]
        (into container
              (for [[idx value] (map-indexed vector items)]
                (r/create-element
                 SortableItem
                 #js {:key (str "item-" idx)
                      :index idx
                      :value value}))))))))

(defn style-map
  "Returns a map representing all currently set css styles; this makes sense
  so we can save a non-updating version of it."
  [node]
  (let [style (js/window.getComputedStyle node)]
    (into {} (keep (fn [idx]
                     (let [property (.item style idx)]
                       [property (.getPropertyValue style property)]))
                   (range (.-length style))))))

(defn node-seq
  "Returns a seq of all of a node's children"
  [node]
  (loop [waiting [node]
         nodes []]
    (if-let [node (first waiting)]
      (if-let [children (array-seq (.-children node))]
        (recur (concat (rest waiting) children) (conj nodes node))
        (recur (rest waiting) (conj nodes node)))
      (rest nodes))))

(defn style-snapshot
  "Recursively grabs the of all of a node's children"
  [node]
  (into [] (map style-map (node-seq node))))

(defn style-from-map!
  "Restores the styling saved in a stylemap"
  [style-map node]
  (let [style (str/join ";" (map (fn [[k v]] (str k ": " v)) style-map))]
    (.setAttribute node "style" style)))

(defn restore-snapshot
  "Recursively restores the styling of all of a nodes children"
  [style-snapshot node]
  (let [nodes (vec (node-seq node))]
    (dotimes [i (count nodes)]
      (style-from-map! (nth style-snapshot i) (nth nodes i)))))

(defonce saved-snapshot (atom nil))

(defn sortable-component [{:keys [container items render-item on-sort-end]}]
  (let [Wrapper  (make-wrapper {:container container
                                :render-item render-item})]
    (r/create-element
     Wrapper
     #js {:items items
          :helperClass "sortable-is-moving"

          ;; save the style of all of the rows children
          :updateBeforeSortStart
          (fn [event]
            (reset! saved-snapshot (style-snapshot (.-node event))))
          :onSortStart
          (fn [_]
            ;; the node we get passed as parameter is the original node unfortunately
            (restore-snapshot @saved-snapshot (js/document.querySelector "body > tr:last-of-type")))

          ;; update the state to reflect the new order
          :onSortEnd
          (fn [event]
            (on-sort-end {:old-idx (.-oldIndex event)
                          :new-idx (.-newIndex event)}))

          :useDragHandle true})))
