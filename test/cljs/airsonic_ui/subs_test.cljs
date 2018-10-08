(ns airsonic-ui.subs-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.api.helpers :as api]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]))

(deftest booting
  (let [route [:some-route nil nil]
        verified-credentials (assoc fixtures/credentials :verified? true)
        is-booting? (fn is-booting? [db]
                      (subs/is-booting? db [:subs/is-booting?]))]
    (testing "Should be false when we don't have previous credentials"
      (is (not (is-booting? {:routes/current-route route})))
      (is (not (is-booting? {:routes/current-route route
                             :credentials {}}))) )
    (testing "Should be true when we have unverified credentials"
      (is (true? (is-booting? {:routes/current-route route
                               :credentials fixtures/credentials}))))
    (testing "Should be false when we have verified credentials"
      (is (not (is-booting? {:routes/current-route route
                             :credentials verified-credentials}))))
    (testing "Should be true when routing is not yet set up"
      (is (true? (is-booting? {:routes/current-route nil
                               :credentials verified-credentials}))))
    (testing "Should be false when an error occurred"
      (is (false? (is-booting? (:db (events/show-notification {} [:_ :error "Something bad happened"]))))))))

(deftest cover-images
  (let [credentials {:server "https://foo.bar"
                     :u "test-user"
                     :p "some-random-password"}]
    (testing "Should give the correct path once the credentials are set"
      (is (= (api/cover-url credentials fixtures/song 48)
             (subs/cover-url credentials [:subs/cover-image fixtures/song 48]))))))

(def successful-auth-db
  "For the details see event_test.cljs"
  (-> {:store {:credentials fixtures/credentials}}
      (events/initialize-app [::events/initialize-app])
      (events/authentication-response [:credentials/authentication-response (:auth-success fixtures/responses)])
      (events/authentication-success [:credentials/authentication-success fixtures/credentials (:auth-success fixtures/responses)])
      (:db)))

(deftest user-roles
  (testing "Should be available after a successful authentication"
    (let [user-roles (-> (subs/user-info successful-auth-db [:user/info])
                         (subs/user-roles [:user/roles]))]
      (is (set? user-roles))
      (is (every? keyword? user-roles))
      (is (not (user-roles :username)) "and contain only roles")))
  (testing "Should indicate whether a user has a given role"
    (letfn [(role [role]
              (-> (subs/user-info successful-auth-db [:user/info])
                  (subs/user-roles [:user/roles])
                  (disj :admin) ; <- makes sure we're not allowed everything
                  (subs/user-role [:user/role role])))]
      (is (some? (role :stream)))
      (is (not (some? (role :video-conversion))))))
  (testing "Should allow everything to an admin"
    (letfn [(admin-role [role]
              (-> (subs/user-info successful-auth-db [:user/info])
                  (subs/user-roles [:user/roles])
                  (subs/user-role [:user/role role])))]
      (is (some? (admin-role :stream)))
      (is (some? (admin-role :video-conversion))))))
