(ns airsonic-ui.fixtures)

(def credentials {:u "username"
                  :p "cleartext-password"
                  :server "https://demo.airsonic.io"})

(def responses {:error {:subsonic-response
                        {:error {:code 50
                                 :message "Incompatible Airsonic REST protocol version. Server must upgrade."}
                         :status "failed"
                         :version "1.15.0"}}
                :ok {:subsonic-response
                     {:scanStatus {:count 10326
                                   :scanning false}
                      :status "ok"
                      :version "1.15.0"}}
                :auth-success {:subsonic-response {:status "ok"
                                                   :version "1.15.0"}}
                :auth-failure {:subsonic-response {:status "failed"
                                                   :version "1.15.0"
                                                   :error {:code 40
                                                           :message "Wrong username or password."}}}})

(def song
  {:artistId 42,
   :path "DJ Koze/DJ Koze - Reincarnations Part 2, The Remix Chapter 2009-2014/14. Apparat - Black Water (DJ Koze Remix).mp3",
   :suffix "mp3",
   :isDir false,
   :bitRate 320,
   :parent 3556,
   :albumId 382,
   :type "music",
   :created "2017-06-28T19:07:02.000Z",
   :duration 317,
   :artist "Apparat",
   :isVideo false,
   :size 12850290,
   :title "Black Water (DJ Koze Remix)",
   :playCount 0
   :year 2014,
   :id 3562,
   :coverArt 3556,
   :contentType "audio/mpeg",
   :album "Reincarnations, Pt. 2 - The Remix Chapter 2009 - 2014",
   :track 14})

(def playback-status
  {:ended? false
   :loop? false
   :muted? false
   :paused? false
   :current-src "https://londe.arnes.space/rest/stream?f=json&c=airsonic-ui-cljs&v=1.15.0&id=9574&u=arne&p=27h-%25bO%5B8-.ys%40SQ%7Bg%24-%5B5NZkX%7Dw%24NNwY%263DPATi%2CgaFoH%40e"
   :current-time 3.477029})
