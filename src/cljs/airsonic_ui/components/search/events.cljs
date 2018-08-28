(ns airsonic-ui.components.search.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [airsonic-ui.routes :as routes]))

(reg-event-db
 ;; this is called on navigation and handled in routes.cljs; the reason is that
 ;; when we're navigating to search?query=foo we don't have the term in our db.
 :search/restore-term-from-param
 (fn [db [_ term]]
   (assoc-in db [:search :term] term)))

(reg-event-fx
 :search/do-search
 (fn do-search [fx [_ term]]
   {:dispatch [:routes/do-navigation [::routes/search {} {:query term}]]}))
