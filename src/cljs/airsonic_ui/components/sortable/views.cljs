(ns airsonic-ui.components.sortable.views
  (:require [reagent.core :as r]
            ["react-sortable-hoc" :refer [SortableHandle SortableElement
                                          SortableContainer]]))

(defonce state
  (r/atom {:items (vec (map #(str "Item " %) (range 1 51)))
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

(def SortableItem
  (SortableElement.
    (r/reactify-component
      (fn [{:keys [value]}]
        [:tr
         [:td "Hi, I am a table"]
         [:td value]
         (when (:sort-enabled? @state)
           [:td [:> DragHandle]])]))))

(def SortableList
  (SortableContainer.
   (r/reactify-component
    (fn [{:keys [items]}]
      [:table.table
       (for [[idx value] (map-indexed vector items)]
         ;; No :> or adapt-react-class here because that would convert value to JS
         (r/create-element
          SortableItem
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

(defn sortable-component []
  (fn []
    (r/create-element
     SortableList
     #js {:items (:items @state)
          :onSortEnd (fn [event]
                       (swap! state update :items vector-move (.-oldIndex event) (.-newIndex event)))
          :useDragHandle true})))
