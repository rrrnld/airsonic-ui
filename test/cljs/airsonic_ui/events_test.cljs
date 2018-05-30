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
        (is (= server (get-in fx [:db :server]))))
      (testing "invokes correct success callback"
        (is (= ::events/credentials-verified (first (:on-success request)))))))
  (testing "On succesfull response"
    (let [fx (events/credentials-verified {:db {}} [:_ "user" "pass"])
          credentials {:u "user" :p "pass"}]
      (testing "credentials are sent to the router for access rights"
        (is (= credentials (:routes/set-credentials fx))))
      (testing "credentials are saved in the global state"
        (is (= credentials (get-in fx [:db :login]))))
      (testing "the login process is finalized"
        (is (= [::events/logged-in] (:dispatch fx)))))))
