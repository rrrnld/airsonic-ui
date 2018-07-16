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
            (events/restore-previous-session {:store {:credentials {:u "test"
                                                                    :p "test"
                                                                    :server "https://demo.airsonic.io/"}}} [:_]))]
    (testing "Should initialize routing after checking for previous credentials"
      (is (contains? (no-previous-session) :routes/start-routing))
      (is (contains? (has-previous-session) :routes/start-routing)))
    (testing "Should indicate success or failure"
      (is (true? (dispatches? (no-previous-session) :init-flow/credentials-not-found)))
      (is (true? (dispatches? (has-previous-session) :init-flow/credentials-found))))
    (testing "Should send an auth request on success"
      (is (true? (dispatches? (events/credentials-found {} [:_]) :credentials/verification-request))))))

(deftest authentication
  (testing "Server ping for verifications"
    (let [server "https://localhost"
          fx (events/credentials-verification-request {} [:_ "user" "pass" server])
          request (:http-xhrio fx)]
      (testing "uses correct server url"
        (let [uri (:uri request)]
          (is (true? (str/starts-with? uri server)))
          (is (true? (str/includes? uri "/ping")))
          (is (true? (str/includes? uri "p=pass")))
          (is (true? (str/includes? uri "u=user")))))
      (testing "invokes correct success callback"
        (is (= :credentials/verification-response (first (:on-success request)))))))
  (testing "Auth response"
    (testing "verification for bad responses"
      (let [ev [:_ "user" "pass" "https://localhost"]
            invalid-credentials (events/credentials-verification-response {} (conj ev (:auth-failure responses)))
            verification-failure (events/credentials-verification-failure {} [:_ (:auth-failure responses)])]
        (is (true? (dispatches? invalid-credentials :credentials/verification-failure)) "fails for bad responses")
        (is (true? (dispatches? verification-failure :notification/show)) "shows the failure the the user")))
    (let [server "https://localhost"
          fx (events/credentials-verification-response {} [:_ "username" "password" server (:auth-success responses)])]
      (is (true? (dispatches? fx [:credentials/verified "username" "password" server])))))
  (testing "On succesful response"
    (let [credentials {:u "user" :p "pass" :server "https://localhost"}
          fx (events/credentials-verified {} [:_ (:u credentials) (:p credentials) (:server credentials)])]
    (testing "credentials are sent to the router for access rights"
      (is (= credentials (:routes/set-credentials fx))))
    (testing "credentials are saved in the global state"
      (is (= credentials (get-in fx [:db :credentials]))))
    (testing "the login process is finalized"
      (is (true? (dispatches? fx ::events/logged-in)))))))

(deftest logout
  (let [fx (events/logout {} [:_])]
    (testing "Should clear all stored data"
      (is (nil? (:store fx))))
    (testing "Should redirect to the login screen"
      (is (= [::routes/login] (:routes/navigate fx))))
    (testing "Should unset authentication in the router"
      (is (contains? fx :routes/unset-credentials)))
    (testing "Should reset the app-db"
      (is (= (every? #(= (get db/default-db %) (get-in fx [:db %])) (keys db/default-db))))))
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
