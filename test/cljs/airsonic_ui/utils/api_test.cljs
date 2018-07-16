(ns airsonic-ui.utils.api-test
  (:require [cljs.test :refer [deftest testing is]]
            [clojure.string :as str]
            [airsonic-ui.fixtures :refer [responses]]
            [airsonic-ui.utils.api :as api]))

(defn- url
  "Construct a url with no params"
  [server endpoint]
  (api/url server endpoint {}))

(def fixtures
  {:default-url (url "http://localhost:8080" "ping")})

(deftest general-url-construction
  (testing "Handles missing slashes"
    (is (true? (str/starts-with? (fixtures :default-url)  "http://localhost:8080/rest/ping")))
    (is (true? (str/starts-with? (url "http://localhost:8080/" "ping") "http://localhost:8080/rest/ping"))))
  (testing "Should set correct default parameters"
    (is (string? (re-find #"f=json" (fixtures :default-url))))
    (is (string? (re-find #"v=1\.15\.0" (fixtures :default-url))))))

(deftest song-urls
  (testing "Should construct the url based on a song's id"
    (let [song {:id 1234}]
      (is (true? (str/includes? (api/song-url "http://localhost" {} song) (str "id=" (:id song))))))))

(deftest cover-urls
  (let [album {:coverArt "cover-99999"}]
    (testing "Should construct the url based on an item's cover-id"
      (is (true? (str/includes? (api/cover-url "http://server.tld" {} album -1) (str "id=" (:coverArt album))))))
    (testing "Should scale an image to a given size"
      (is (true? (str/includes? (api/cover-url "http://server.tld" {} album 48) "size=48"))))))

(deftest response-handling
  (testing "Should unwrap responses"
    (let [response (:ok responses)]
      (is (= (get-in response [:subsonic-response :scanStatus])
             (api/unwrap-response response)))))
  (testing "Should detect errors"
    (is (true? (api/is-error? (:error responses))))
    (is (false? (api/is-error? (:ok responses)))))
  (testing "Should throw an informative error when trying to unwrap an erroneous response"
    (let [error-response (:error responses)]
      (is (thrown? ExceptionInfo (api/unwrap-response error-response)))
      (try
        (api/unwrap-response error-response)
        (catch ExceptionInfo e
          (= (:error error-response) (ex-data e)))))))

(deftest error-recognition
  (testing "Should detect error responses"
    (is (true? (api/is-error? (:error responses))))
    (is (true? (api/is-error? (:auth-failure responses)))))
  (testing "Should pass on good responses"
    (is (false? (api/is-error? (:ok responses))))
    (is (false? (api/is-error? (:auth-success responses))))))
