(ns airsonic-ui.events-test
  (:require [cljs.test :refer [deftest testing is]]
            [clojure.string :as str]
            [airsonic-ui.test-helpers :refer [dispatches?]]
            [airsonic-ui.fixtures :as fixtures]
            [airsonic-ui.db :as db]
            [airsonic-ui.routes :as routes]
            [airsonic-ui.events :as events]
            [airsonic-ui.subs :as subs]))

(enable-console-print!)

;; the event tests are actually quite nice to write:
;; because everything in re-frame is described as data, we pass on coeffects
;; to event handler after event handler and check if the final coeffect map
;; looks as expected.

(defn no-previous-session [] (events/initialize-app {} [::events/initialize-app]))
(defn has-previous-session [] (-> {:store {:credentials fixtures/credentials}}
                                  (events/initialize-app [::events/initialize-app])))

(deftest app-initialization
  (testing "Should set up notifications"
    (is (map? (subs/notifications (:db (no-previous-session))
                                  [::subs/notifications])))
    (is (map? (subs/notifications (:db (has-previous-session))
                                  [::subs/notifications]))))
  (testing "Should set up the default database")
  (testing "Should initialize credential verification"
    (is (false? (dispatches? (no-previous-session) :credentials/verify)))
    (is (true? (dispatches? (has-previous-session) [:credentials/verify fixtures/credentials]))))
  (testing "Should initialize the router"
    (is (contains? (no-previous-session) :routes/start-routing))
    (is (contains? (has-previous-session) :routes/start-routing))))

(deftest credential-verification
  (testing "Should fail when there are no credentials"
    (is (false? (dispatches? (-> (no-previous-session)
                                 (events/verify-credentials [:credentials/verify nil])) [::subs/is-booting?]))))
  (testing "Should happen server-side when we have credentials"
    (let [cofx (-> (has-previous-session)
                   (events/verify-credentials [:credentials/verify fixtures/credentials]))]
      (is (true? (dispatches? cofx :credentials/send-authentication-request)))))
  (testing "Should verify the structure of credentials"
    (let [empty-creds  {:store {:credentials {}}}]
      (is (false? (boolean (dispatches? empty-creds :credentials/send-authentication-request)))))
    (let [malformed {:store {:credentials {:xyz #{12 34 56}}}}]
      (is (false? (boolean (dispatches? malformed :credentials/send-authentication-request)))))))

(deftest authentication-request
  (let [event [:credentials/send-authentication-request fixtures/credentials]
        fx (events/authentication-request {} event)
        request (:http-xhrio fx)]
    (testing "uses correct server url"
      (let [uri (:uri request)]
        (is (true? (str/starts-with? uri (:server fixtures/credentials))))
        (is (true? (str/includes? uri "/ping")))
        (is (true? (str/includes? uri (str "p=" (:p fixtures/credentials)))))
        (is (true? (str/includes? uri (str "u=" (:u fixtures/credentials)))))))
    (testing "invokes correct callback on server response"
      (is (= [:credentials/authentication-response fixtures/credentials] (:on-success request))))
    (testing "invokes correct callback when server is not reachable"
      (is (= [:api/bad-response] (:on-failure request))))))

(deftest authentication-response
  (testing "On success"
    (let [cofx (-> (has-previous-session)
                   (events/authentication-response [:credentials/authentication-response (:auth-success fixtures/responses)])
                   (events/authentication-success [:credentials/authentication-success]))]
      (testing "should mark the credentials as verified"
        (is (true? (get-in cofx [:db :credentials :verified?]))))))
  (testing "On failure"
    (let [cofx (-> (has-previous-session)
                   (events/authentication-response [:credentials/authentication-response (:auth-failure fixtures/responses)])
                   (events/authentication-failure [:credentials/authentication-failure (:auth-failure fixtures/responses)]))]
      (testing "should display a notification to the user"
        (is (true? (dispatches? cofx :notification/show)))))))

(deftest manual-login
  (let [{:keys [u p server]} fixtures/credentials
        credentials (assoc fixtures/credentials :verified? false)
        effect (events/user-login {} [:credentials/user-login u p server])]
    (testing "Should save the credentials as unverified"

      (is (= credentials (get-in effect [:db :credentials]))))
    (testing "Should start the authentication request"
      (is (true? (dispatches? effect [:credentials/send-authentication-request credentials]))))))

(deftest logout
  (let [fx (events/logout {} [:_])]
    (testing "Should clear all stored data"
      (is (nil? (:store fx))))
    (testing "Should redirect to the login screen"
      (is (dispatches? fx [:routes/do-navigation [::routes/login]])))
    (testing "Should reset the app-db"
      (is (= db/default-db (:db fx))))
    (testing "Should stop currently playing songs"
      (is (contains? fx :audio/stop))))
  (testing "Should be able to keep a redirection parameter"
    (let [redirect [:route {:with-data #{1 2 3 4 5}}]
          navigation-event (:dispatch (events/logout {} [:_ :redirect-to redirect]))]
      (is (= :routes/do-navigation (first navigation-event)))
      (let [[route-id _ query] (second navigation-event)]
        (is (= ::routes/login route-id))
        (is (contains? query :redirect))))))

(deftest api-interaction
  (testing "Should show an error notification when airsonic responds with an error"
    (let [fx (events/good-api-response {} [:_ (:error fixtures/responses)])
          ev (:dispatch fx)]
      (is (= :notification/show (first ev)))
      (is (= :error (second ev))))))

(defn- first-notification [fx]
  (-> (get-in fx [:db :notifications]) vals first))

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

(deftest song-has-ended
  (testing "Should play the next song when current song has ended"
    (is (not (dispatches? (events/audio-update {} [:audio/update {:ended? false}]) ::events/next-song)))
    (is (dispatches? (events/audio-update {} [:audio/update {:ended? true}]) ::events/next-song))))
