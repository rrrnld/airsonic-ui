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
