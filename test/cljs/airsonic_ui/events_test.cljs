(ns airsonic-ui.events-test
  (:require [cljs.test :refer [deftest testing is]]
            [clojure.string :as str]
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
        (is (= ::events/credentials-verified (first (:on-success request)))))))
  (testing "On succesfull response"
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
          fx (events/try-remember-user {:store {:credentials credentials}})]
      (testing "the auth request is skipped"
        (is (nil? (:http-xhrio fx))))
      (testing "we get sent straight to the home page"
        (is (= ::events/credentials-verified (first (:dispatch fx)))))))
  (testing "When there's no previous login data"
    (testing "remembering has no effect"
      (is (nil? (events/try-remember-user {}))))))
