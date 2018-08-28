(ns airsonic-ui.components.search.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub :search/current-term (fn current-term [db _]
                                (get-in db [:search :term])))
