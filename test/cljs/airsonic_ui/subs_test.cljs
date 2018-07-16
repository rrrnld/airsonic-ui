(ns airsonic-ui.subs-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.db :as db]
            [airsonic-ui.fixtures :refer [song] :as fixtures]
            [airsonic-ui.utils.api :as api]
            [airsonic-ui.events :as ev]
            [airsonic-ui.subs :as subs]))

(def creds {:credentials {:u "test"
                          :p "test"
                          :server "https://demo.airsonic.io/"}})

(deftest is-booting
  (testing "Should be true when provided the initial state"
    (is (true? (subs/is-booting? db/default-db [:_]))))
  (testing "Should be true when we have credentials but no response yet"
    (is (true? (-> (ev/restore-previous-session {:store creds} [:_])
                   (ev/credentials-found [:_])
                   :db
                   (subs/is-booting? [:_])))))
  (testing "Should be false when the login screen is shown"
    (is (false? (-> (ev/restore-previous-session {} [:_])
                    (ev/credentials-not-found [:_])
                    :db
                    (subs/is-booting? [:_])))))
  (let [{:keys [u p server]} (:credentials creds)]
    (testing "Should be false after we verified our credentials with the server"
      (is (false? (-> (ev/credentials-verified {:db {}} [:_ u p server])
                      :db
                      (subs/is-booting? [:_])))))
    (testing "Should be false after the server rejected our credentials"
      (is (false? (-> (ev/credentials-verification-failure {} [:_ (:auth-failure fixtures/responses)])
                      :db
                      (subs/is-booting? [:_]))))))
  (testing "Should be false when a user logged out voluntarily"
    (is (false? (-> (ev/logout {} [:_])
                    :db
                    (subs/is-booting? [:_]))))))

(deftest cover-images
  (let [credentials {:server "https://foo.bar"
                     :u "test-user"
                     :p "some-random-password"}]
    (testing "Should give the correct path once the credentials are set"
      (is (= (api/cover-url (:server credentials)
                            (select-keys credentials [:u :p])
                            song
                            48)
             (subs/cover-url [credentials] [:_ song 48]))))))
