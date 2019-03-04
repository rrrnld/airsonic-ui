(ns airsonic-ui.components.sortable.views
  (:require [reagent.core :as r]
            [clojure.string :as str]
            ["react-sortable-hoc" :refer [SortableHandle SortableElement
                                          SortableContainer]]))

(defonce state
  (r/atom {:items (vec (map #(str "Item " %) (range 1 11)))
           :sort-enabled? true}))

;; this code is taken from https://github.com/reagent-project/reagent/blob/72c95257c13e5de1531e16d1a06da7686041d3f4/examples/react-sortable-hoc/src/example/core.cljs
(def DragHandle
  (SortableHandle.
    ;; Alternative to r/reactify-component, which doens't convert props and hiccup,
    ;; is to just provide fn as component and use as-element or create-element
    ;; to return React elements from the component.
    (fn []
      (r/as-element [:span {:style {:WebkitTouchCallout "none"
                                    :WebkitUserSelect "none"
                                    :KhtmlUserSelect "none"
                                    :MozUserSelect "none"
                                    ;; NOTE: lowercase "ms" prefix
                                    ;; https://www.andismith.com/blogs/2012/02/modernizr-prefixed/
                                    :msUserSelect "none"
                                    :userSelect "none"}}
                     "::"]))))

(def SortableRow
  (SortableElement.
   (r/reactify-component (fn [{:keys [value]}]
                           [:tr
                            [:td value]
                            (when (:sort-enabled? @state)
                              [:td [:> DragHandle]])]))))

(def SortableTable
  (SortableContainer.
   (r/reactify-component
    (fn [{:keys [items]}]
      [:table.table.is-fullwidth>tbody
       (for [[idx value] (map-indexed vector items)]
         ;; No :> or adapt-react-class here because that would convert value to JS
         (r/create-element
          SortableRow
          #js {:key (str "item-" idx)
               :index idx
               :value value}))]))))

(defn vector-move [coll prev-index new-index]
  (let [items (into (subvec coll 0 prev-index)
                    (subvec coll (inc prev-index)))]
    (-> (subvec items 0 new-index)
        (conj (get coll prev-index))
        (into (subvec items new-index)))))

(comment
  (= [0 2 3 4 1 5] (vector-move [0 1 2 3 4 5] 1 4)))

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

(defn sortable-component []
  (r/create-element
   SortableTable
   #js {:items (:items @state)
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
          (swap! state update :items vector-move (.-oldIndex event) (.-newIndex event)))
        :useDragHandle true}))
