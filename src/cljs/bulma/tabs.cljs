(ns bulma.tabs)

(defn tabs [{:keys [items]}]
  [:div.tabs.is-boxed
   [:ul
    (for [[idx {:keys [href label active?]}] (map-indexed vector items)]
      ^{:key idx} [:li (when active? {:class "is-active"})
                   [:a {:href href} label]])]])
