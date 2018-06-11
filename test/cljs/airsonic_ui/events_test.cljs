(ns airsonic-ui.events-test
  (:require [cljs.test :refer [deftest testing is]]
            [clojure.string :as str]
            [airsonic-ui.fixtures :refer [responses]]
            [airsonic-ui.events :as events]))

(enable-console-print!)

(deftest authentication
  (testing "Credential verification"
    (let [server "https://localhost"
          fx (events/authenticate {:db {}} [:_ "user" "pass" server])
          request (:http-xhrio fx)]
      (testing "uses correct server url"
        (is (str/starts-with? (:uri request) server))
        (is (str/includes? (:uri request) "/ping")))
      (testing "saves the given server location"
        (is (= server (get-in fx [:db :credentials :server]))))
      (testing "invokes correct success callback"
        (is (= ::events/verify-auth-response (first (:on-success request)))))))
  (testing "Auth response verification"
    (is (= :notification/show
           (first (:dispatch (events/verify-auth-response {} [:_ "user" "pass" (:error responses)]))))
        "shows an error when we have an error response")
    (let [event (:dispatch (events/verify-auth-response {} [:_ "username" "password" (:auth-success responses)]))]
      (is (= [::events/credentials-verified "username" "password"] event))))
  (testing "On succesful response"
    (let [creds-before {:server "https://localhost"}
          fx (events/credentials-verified {:db {:credentials creds-before}}
                                          [:_ "user" "pass"])
          auth {:u "user" :p "pass"}]
      (testing "credentials are sent to the router for access rights"
        (is (= auth (:routes/set-credentials fx))))
      (testing "credentials are saved in the global state"
        (is (= auth (-> (get-in fx [:db :credentials])
                               (select-keys [:u :p])))))
      (testing "credentials are persisted together with the server address"
        (is (= (merge creds-before auth) (get-in fx [:store :credentials]))))
      (testing "the login process is finalized"
        (is (= [::events/logged-in] (:dispatch fx))))))
  (testing "When remembering previous login data"
    (let [credentials {:server "http://localhost"
                       :u "another-user"
                       :p "some_random_password123"}
          fx (events/try-remember-user {:store {:credentials credentials}} [:_])]
      (testing "the auth request is skipped"
        (is (nil? (:http-xhrio fx))))
      (testing "we get sent straight to the home page"
        (is (= ::events/credentials-verified (first (:dispatch fx)))))))
  (testing "When there's no previous login data"
    (testing "remembering has no effect"
      (is (nil? (events/try-remember-user {} [:_]))))))

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
