(ns airsonic-ui.events-test
  (:require [cljs.test :refer [deftest testing is]]
            [clojure.string :as str]
            [airsonic-ui.test-helpers :refer [dispatches?]]
            [airsonic-ui.fixtures :refer [responses]]
            [airsonic-ui.db :as db]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]))

(enable-console-print!)

(deftest session-restoration
  (letfn [(no-previous-session []
            (events/restore-previous-session {} [:_]))
          (has-previous-session []
            (events/restore-previous-session {:store {:u "test"
                                                      :p "test"
                                                      :server "https://demo.airsonic.io/"}} [:_]))]
    (testing "Should initialize routing after checking for previous credentials"
      (is (contains? (no-previous-session) :routes/start-routing))
      (is (contains? (has-previous-session) :routes/start-routing)))
    (testing "Should indicate success or failure"
      (is (dispatches? (no-previous-session)) :init-flow/credentials-missing)
      (is (dispatches? (has-previous-session)) :init-flow/credentials-found))
    (testing "Should send an auth request on success"
      (is (dispatches? (events/credentials-found {} [:_]) :credentials/verification-request)))
    (testing "Should redirect to the login form when there's no previous session to be restored")))

(deftest authentication
  (testing "Server ping for verifications"
    (let [server "https://localhost"
          fx (events/credentials-verification-request {} [:_ "user" "pass" server])
          request (:http-xhrio fx)]
      (testing "uses correct server url"
        (is (str/starts-with? (:uri request) server))
        (is (str/includes? (:uri request) "/ping"))
        (is (str/includes? (:uri request) "p=pass"))
        (is (str/includes? (:uri request) "u=user")))
      (testing "invokes correct success callback"
        (is (= :credentials/verification-response (first (:on-success request)))))))
  (testing "Auth response verification"
    (let [server "https://localhost"
          fx (events/credentials-verification-response {} [:_ "user" "pass" server (:error responses)])]
      (is (= (dispatches? fx :notification/show))
          "shows an error when we have a bad response"))
    (let [server "https://localhost"
          fx (events/credentials-verification-response {} [:_ "username" "password" server (:auth-success responses)])]
      (is (dispatches? fx [:credentials/verified "username" "password" server]))))
  (testing "On succesful response"
    (let [credentials {:u "user" :p "pass" :server "https://localhost"}
          fx (events/credentials-verified {} [:_ (:u credentials) (:p credentials) (:server credentials)])]
    (testing "credentials are sent to the router for access rights"
      (is (= credentials (:routes/set-credentials fx))))
    (testing "credentials are saved in the global state"
      (is (= credentials (get-in fx [:db :credentials]))))
    (testing "the login process is finalized"
      (is (dispatches? fx ::events/logged-in))))))

(deftest logout
  (let [fx (events/logout {} [:_])]
    (testing "Should clear all stored data"
      (is (nil? (:store fx))))
    (testing "Should redirect to the login screen"
      (is (= [::routes/login] (:routes/navigate fx))))
    (testing "Should unset authentication in the router"
      (is (contains? fx :routes/unset-credentials)))
    (testing "Should reset the app-db"
      (is (= db/default-db (:db fx)))))
  (testing "Should be able to keep a redirection parameter"
    (let [redirect [:route {:with-data #{1 2 3 4 5}}]
          fx (events/logout {} [:_ :redirect-to redirect])]
      (is (= [::routes/login {:redirect redirect}])))))

(defn- first-notification [fx]
  (-> (get-in fx [:db :notifications]) vals first))

(deftest api-interaction
  (testing "Should show an error notification when airsonic responds with an error"
    (let [fx (events/good-api-response {} [:_ (:error responses)])]
      (is (= :error (-> fx :dispatch second))))))

(deftest user-notifications
  (testing "Should be able to display a message with an assigned level"
    (is (= :error (:level (first-notification (events/show-notification {} [:_ :error "foo"])))))
    (is (= :info (:level (first-notification (events/show-notification {} [:_ :info "some other message"]))))))
  (testing "Should default to level :info"
    (is (= :info (:level (first-notification (events/show-notification {} [:_ "and another one"]))))))
  (testing "Should create a unique id for each message"
    (let [state (->
                 {}
                 (events/show-notification [:_ :info "Something something"])
                 (events/show-notification [:_ :error "Something important"]))
          ids (keys (:notifications state))]
      (is (= (count ids) (count (set ids))))))
  (testing "Should remove a message, given it's id"
    (let [state (events/show-notification {} [:_ "This is a notification"])
          id (-> (:notifications state)
                 keys
                 first)]
      (is (empty? (:notifications (events/hide-notification state [:_ id]))))))
  (testing "Should automatically remove a message after a while"
    (let [fx (events/show-notification {} [:_ :info "This is a notification"])]
      (is (= :notification/hide (-> (:dispatch-later fx) first :dispatch first))))))
