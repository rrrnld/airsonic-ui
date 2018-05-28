(ns airsonic-ui.views.cover
  (:require [re-frame.core :refer [subscribe]]
            [airsonic-ui.subs :as subs]
            [airsonic-ui.utils.api :as api]))

;; FIXME: The direct dependency on these subs is a bit ugly

(defn cover
  [item size]
  (let [server @(subscribe [::subs/server])
        login @(subscribe [::subs/login])
        url (partial api/cover-url server login item)]
    [:figure {:class-name (str "image is-" size "x" size)}
     [:img {:src (url size)
            :srcset (str (url size) ", " (url (* 2 size)) " 2x")}]]))
