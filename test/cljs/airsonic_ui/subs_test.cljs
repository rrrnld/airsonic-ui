(ns airsonic-ui.subs-test
  (:require [cljs.test :refer [deftest testing is]]
            [airsonic-ui.fixtures :refer [song]]
            [airsonic-ui.utils.api :as api]
            [airsonic-ui.subs :as subs]))

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
