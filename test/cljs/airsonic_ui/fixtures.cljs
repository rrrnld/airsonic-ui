(ns airsonic-ui.fixtures)

(def responses {:error {:subsonic-response
                        {:error {:code 40
                                 :message "Wrong username or password"}
                         :status "failed"
                         :version "1.15.0"}}
                :ok {:subsonic-response
                     {:scanStatus {:count 10326
                                   :scanning false}
                      :status "ok"
                      :version "1.15.0"}}
                :auth-success {:subsonic-response {:status "ok"
                                                   :version "1.15.0"}}})
