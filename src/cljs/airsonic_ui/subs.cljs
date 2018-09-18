(ns airsonic-ui.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [airsonic-ui.api.helpers :as api]
            [airsonic-ui.helpers :refer [kebabify]]
            [clojure.string :as str]))

;;
;; app initialization
;;

;; TODO: Computation and extaction is mixed; this could be simpler

(defn- error-notifications [notifications]
  (filter (fn [[_ n]]
            (= :error (:level n))) notifications))

(defn- no-errors? [db]
  (empty? (error-notifications (:notifications db))))

(defn- no-route? [db]
  (empty? (:routes/current-route db)))

(defn- no-credentials? [db]
  (and (not (empty? (:credentials db)))
       (not (get-in db [:credentials :verified?]))))

(defn is-booting?
  "The boot process starts with setting up routing and continues if we found
  previous credentials and ends when we receive a response from the server."
  [db _]
  ;; so either we don't have any credentials or they are not verified
  (and (no-errors? db) (or (no-route? db) (no-credentials? db))))

(reg-sub ::is-booting? is-booting?)

(defn credentials [db _] (:credentials db))
(reg-sub ::credentials credentials)

;; ---
;; user info and roles
;; ---

(defn user-info
  "Returns the response to getUser?username=$name; this isn't cached like the
  other responses because it's not retrieved via :api/request"
  [db _]
  (:user db))

(reg-sub :user/info user-info)

(defn user-roles
  "Takes only the roles out of a getUser response to make it easier to work with"
  [user-info _]
  (->>
   (filter (fn [[k _]] (re-find #"Role$" (name k))) user-info)
   (keep (fn [[role has-role?]]
           (when has-role? (str/replace (name role) #"Role$" ""))))
   (map kebabify)
   (set)))

(reg-sub
 :user/roles
 :<- [:user/info]
 user-roles)

(defn user-role
  "Can be used to determine whether a user is allowed to do certain things"
  [user-roles [_ role]]
  (or (user-roles role) (user-roles :admin)))

(reg-sub
 :user/role
 :<- [:user/roles]
 user-role)

;; ---
;; misc
;; ---

(defn cover-url
  "Provides a convenient way for views to get cover images so they don't have
  to build them themselves and can live a simple and happy life."
  [[{:keys [server u p]}] [_ song size]]
  (api/cover-url server {:u u :p p} song size))

(reg-sub
 ::cover-url
 (fn [_ _] [(subscribe [::credentials])])
 cover-url)

;; user notifications

(defn notifications [db _] (:notifications db))
(reg-sub ::notifications notifications)
