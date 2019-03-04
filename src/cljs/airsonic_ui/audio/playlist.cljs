(ns airsonic-ui.audio.playlist
  "Implements playlist queues that support different kinds of repetition and
  song ordering.")

;; Turns out we can nicely implement this by thinly wrapping a sequence of items
;; We re-use the core ClojureScript protocols internally but provide a nice and
;; explicit API to consume

(defprotocol IPlaylist
  (current-song [this])
  (next-song [this])
  (previous-song [this])

  (set-current-song [this song-idx]
    "Advances the queue to the song given by song-idx")
  (set-playback-mode [this playback-mode]
    "Changes the playback mode of a playlist and re-shuffles it if necessary")
  (set-repeat-mode [this repeat-mode]
    "Allows you to change how the next and previous song are selected")

  (enqueue-last [this song])
  (enqueue-next [this song]))

;; helpers to manage creating playlists

(defn- mark-original-order
  "This function is used if we switch from linear to shuffled; it allows us to
  restore the order of the queue when it was created."
  [items]
  (->> (sort-by (comp meta :playlist/linear-order) items)
       (map-indexed (fn [idx item]
                      (vary-meta item assoc :playlist/linear-order idx)))))

(defn- linear-queue
  [items]
  (->> (mark-original-order items)
       (map-indexed vector)
       (into (sorted-map))))

(defn- shuffled-queue
  [items]
  (let [shuffled-indices (shuffle (range (count items)))]
    (->> (mark-original-order items)
         (map vector shuffled-indices)
         (into (sorted-map)))))

;; the exported interface:

(defrecord Playlist [items current-idx playback-mode repeat-mode]
  cljs.core/ICounted
  (-count [this]
    (count items))

  IPlaylist
  (current-song [_]
    (get items current-idx))

  (next-song [this]
    (update this :current-idx
            (fn [current-idx]
              (cond
                (= repeat-mode :repeat-single) current-idx

                (or (= repeat-mode :repeat-all)
                    (< current-idx (dec (count this))))
                (mod (inc current-idx) (count this))))))

  (previous-song [this]
    (update this :current-idx
            (fn [current-idx]
              (cond
                (= repeat-mode :repeat-single) current-idx

                (or (= repeat-mode :repeat-all)
                    (> current-idx 0))
                (mod (dec current-idx) (count this))

                :else nil))))

  (set-current-song [playlist song-idx]
    (assoc playlist :current-idx song-idx))

  (set-playback-mode [playlist playback-mode]
    (let [current-song (current-song playlist)
          queue-fn (case playback-mode
                     :shuffled shuffled-queue
                     :linear linear-queue)
          next-playlist (update playlist :items (comp queue-fn vals))
          next-idx (first (keep (fn [[idx song]]
                                  (when (= song current-song)
                                    idx))
                                (:items next-playlist)))]
      ;; we have to find out the index of the currently playing song after the
      ;; playlist was created because it might change when shuffling / unshuffling
      (set-current-song next-playlist next-idx)))

  (set-repeat-mode [playlist repeat-mode]
    (assoc playlist :repeat-mode repeat-mode))

  (enqueue-last [this song]
    (let [order (inc (key (last items)))]
      ;; Arguably this is a bit weird; but if you want to play something last in
      ;; a shuffled playlist, you want to play it last I guess.
      (assoc-in this [:items order]
                (vary-meta song assoc :playlist/linear-order order))))

  (enqueue-next [this song]
    ;; we slice the songs up until the currently playing one and increase the
    ;; order for all the songs after
    (let [songs (vec (vals items))
          reordered (-> (subvec songs 0 (inc current-idx))
                        (conj (vary-meta song assoc :playlist/linear-order (inc current-idx)))
                        (concat (subvec songs (inc current-idx))))]
      (assoc this :items (->> (map-indexed vector reordered)
                              (into (sorted-map)))))))

;; constructor wrapper

(defmulti ->playlist
  "Creates a new playlist that behaves according to the given playback- and
  repeat-mode parameters."
  (fn [_ & {:keys [playback-mode]}] playback-mode))

(defmethod ->playlist :linear
  [items & {:keys [playback-mode repeat-mode]}]
  (->Playlist (linear-queue items) 0 playback-mode repeat-mode))

(defmethod ->playlist :shuffled
  [items & {:keys [playback-mode repeat-mode]}]
  (->Playlist (shuffled-queue items) 0 playback-mode repeat-mode))
