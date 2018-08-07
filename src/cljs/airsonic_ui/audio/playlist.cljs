(ns airsonic-ui.audio.playlist
  "Implements playlist queues that support different kinds of repetition and
  song ordering."
  (:refer-clojure :exclude [peek]))

(defrecord Playlist [queue playback-mode repeat-mode]
  cljs.core/ICounted
  (-count [this]
    (count (:queue this))))

(defmulti ->playlist
  "Creates a new playlist that behaves according to the given playback- and
  repeat-mode parameters."
  (fn [queue playing-idx playback-mode repeat-mode] playback-mode))

(defn- playlist-queue
  [queue playing-idx]
  (concat (take playing-idx queue)
          [(assoc (nth queue playing-idx) :currently-playing? true)]
          (drop (inc playing-idx) queue)))

(defmethod ->playlist :playback-mode/linear
  [queue playing-idx playback-mode repeat-mode]
  (let [queue (->> (playlist-queue queue playing-idx)
                   (mapv (fn [order song] (assoc song :order order))
                         (range (count queue))))]
    (->Playlist queue playback-mode repeat-mode)))

(defn- -shuffle-songs [queue]
  (mapv (fn [song order] (assoc song :order order)) queue (shuffle (range (count queue)))))

(defmethod ->playlist :playback-mode/shuffled
  [queue playing-idx playback-mode repeat-mode]
  (let [queue (->> (playlist-queue queue playing-idx)
                   (-shuffle-songs))]
    (->Playlist queue playback-mode repeat-mode)))

(defn- -current-song [playlist]
  (first (keep-indexed (fn [idx song]
                         (when (:currently-playing? song)
                           [idx song]))
                       (:queue playlist))))

(defn set-playback-mode
  "Changes the playback mode of a playlist and re-shuffles it if necessary"
  [playlist playback-mode]
  (->playlist (:queue playlist) (first (-current-song playlist))
              playback-mode (:repeat-mode playlist)))

(defn set-repeat-mode
  "Allows to change the way the next and previous song of a playlist is selected"
  [playlist repeat-mode]
  (assoc playlist :repeat-mode repeat-mode))

(defn peek
  "Returns the song in a playlist that is currently playing"
  [playlist]
  (->> (:queue playlist)
       (filter :currently-playing?)
       (first)))

(defmulti next-song :repeat-mode)

(defmethod next-song :repeat-mode/none
  [playlist]
  ;; this is pretty easy; get the next song and stop playing at the at
  (let [[current-idx _] (-current-song playlist)
        next-idx (inc current-idx)]
    (update playlist :queue
            (fn [queue] (cond-> queue
                          :always (update current-idx dissoc :currently-playing?)
                          (< next-idx (count playlist)) (assoc-in [next-idx :currently-playing?] true))))))

(defmethod next-song :repeat-mode/single [playlist] playlist)

(defmethod next-song :repeat-mode/all
  [playlist]
  (let [[current-idx _] (-current-song playlist)]
    (-> (update-in playlist [:queue current-idx] dissoc :currently-playing?)
        (update :queue
                (fn [queue]
                  ;; we need special treatment here if we're playing the last song and
                  ;; have a shuffled playlist because we need to re-shuffle
                  (if (and (= current-idx (dec (count playlist)))
                           (= :playback-mode/shuffled (:playback-mode playlist)))
                    (->> (-shuffle-songs queue)
                         (mapv #(if (= (:order %) 0) (assoc % :currently-playing? true) %)))
                    (let [next-idx (mod (inc current-idx) (count playlist))]
                      (assoc-in queue [next-idx :currently-playing?] true))))))))

(defmulti previous-song :repeat-mode)

(defn set-current-song [playlist playing-idx]
  ;; TODO: Implementation
  )

(defn add-song-to-end [playlist]
  ;; TODO: Implementation
  )

(defn add-next-song [playlist]
  ;; TODO: Implementation
  )
