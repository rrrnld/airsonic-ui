(ns airsonic-ui.audio.playlist
  "Implements playlist queues that support different kinds of repetition and
  song ordering."
  (:refer-clojure :exclude [peek])
  (:require [airsonic-ui.helpers :refer [find-where]]))

(defrecord Playlist [queue playback-mode repeat-mode]
  cljs.core/ICounted
  (-count [this]
    (count (:queue this))))

(defmulti ->playlist
  "Creates a new playlist that behaves according to the given playback- and
  repeat-mode parameters."
  (fn [queue & {:keys [playback-mode #_repeat-mode]}]
    playback-mode))

(defn- mark-first-song [queue]
  (let [[first-idx _] (find-where #(= 0 (:playlist/order %)) queue)]
    (assoc-in queue [first-idx :playlist/currently-playing?] true)))

(defmethod ->playlist :linear
  [queue & {:keys [playback-mode repeat-mode]}]
  (let [queue (-> (mapv (fn [order song] (assoc song :playlist/order order)) (range) queue)
                  (mark-first-song))]
    (->Playlist queue playback-mode repeat-mode)))

(defn- -shuffle-songs [queue]
  (->> (shuffle (range (count queue)))
       (mapv (fn [song order] (assoc song :playlist/order order)) queue)))

(defmethod ->playlist :shuffled
  [queue & {:keys [playback-mode repeat-mode]}]
  (let [queue (conj (mapv #(update % :playlist/order inc) (-shuffle-songs (rest queue)))
                    (assoc (first queue) :playlist/order 0 :playlist/currently-playing? true))]
    (->Playlist queue playback-mode repeat-mode)))

(defn set-current-song
  "Marks a song in the queue as currently playing, given its ID"
  [playlist next-idx]
  (let [[current-idx _] (find-where :playlist/currently-playing? (:queue playlist))]
    (-> (if current-idx
          (update-in playlist [:queue current-idx] dissoc :playlist/currently-playing?)
          playlist)
        (assoc-in [:queue next-idx :playlist/currently-playing?] true))))

(defn set-playback-mode
  "Changes the playback mode of a playlist and re-shuffles it if necessary"
  [playlist playback-mode]
  (if (= playback-mode :shuffled)
    ;; for shuffled playlists we reorder the songs make sure that the currently
    ;; playing song has order 0
    (let [playlist (->playlist (:queue playlist) :playback-mode playback-mode :repeat-mode (:repeat-mode playlist))
          [current-idx current-song] (find-where :playlist/currently-playing? (:queue playlist))
          [swap-idx _] (find-where #(= 0 (:playlist/order %)) (:queue playlist))]
      (-> (assoc-in playlist [:queue current-idx :playlist/order] 0)
          (assoc-in [:queue swap-idx :playlist/order] (:playlist/order current-song))))
    ;; for linear songs we just make sure that the current does not change
    (let [[current-idx _] (find-where :playlist/currently-playing? (:queue playlist))]
      (-> (->playlist (:queue playlist) :playback-mode playback-mode :repeat-mode (:repeat-mode playlist))
          (set-current-song current-idx)))))

(defn set-repeat-mode
  "Allows to change the way the next and previous song of a playlist is selected"
  [playlist repeat-mode]
  (assoc playlist :repeat-mode repeat-mode))

(defn peek
  "Returns the song in a playlist that is currently playing"
  [playlist]
  (->> (:queue playlist)
       (filter :playlist/currently-playing?)
       (first)))

(defmulti next-song "Advances the currently playing song" :repeat-mode)

(defmethod next-song :repeat-none
  [playlist]
  ;; this is pretty easy; get the next song and stop playing at the at
  (let [[current-idx current-song] (find-where :playlist/currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:playlist/order %) (inc (:playlist/order current-song))) (:queue playlist))]
    (update playlist :queue
            (fn [queue]
              (cond-> queue
                current-idx (update current-idx dissoc :playlist/currently-playing?)
                next-idx (assoc-in [next-idx :playlist/currently-playing?] true))))))

(defmethod next-song :repeat-single [playlist] playlist)

(defmethod next-song :repeat-all
  [playlist]
  (let [[current-idx current-song] (find-where :playlist/currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:playlist/order %) (inc (:playlist/order current-song))) (:queue playlist))]
    (-> (update-in playlist [:queue current-idx] dissoc :playlist/currently-playing?)
        (update :queue
                (fn [queue]
                  ;; we need special treatment here if we're playing the last song and
                  ;; have a shuffled playlist because we need to re-shuffle
                  (if next-idx
                    (assoc-in queue [next-idx :playlist/currently-playing?] true)
                    (case (:playback-mode playlist)
                      :linear (assoc-in queue [0 :playlist/currently-playing?] true)
                      :shuffled (let [queue' (-shuffle-songs queue)
                                                    [next-idx _] (find-where #(= (:playlist/order %) 0) queue')]
                                                (assoc-in queue' [next-idx :playlist/currently-playing?] true)))))))))

(defmulti previous-song "Goes back along the playback queue" :repeat-mode)

(defmethod previous-song :repeat-single [playlist] playlist)

(defmethod previous-song :repeat-none [playlist]
  (let [[current-idx current-song] (find-where :playlist/currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:playlist/order %) (dec (:playlist/order current-song))) (:queue playlist))]
    (set-current-song playlist (or next-idx current-idx))))

(defmethod previous-song :repeat-all [playlist]
  (let [[_ current-song] (find-where :playlist/currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:playlist/order %)
                                     (rem (dec (:playlist/order current-song)) (count playlist)))
                                 (:queue playlist))]
    (if next-idx
      (set-current-song playlist next-idx)
      (if (= :shuffled (:playback-mode playlist))
        (let [highest-order (dec (count playlist))
              playlist (update playlist :queue -shuffle-songs)
              [last-idx _] (find-where #(= (:playlist/order %) highest-order) (:queue playlist))]
          (set-current-song playlist last-idx))
        (set-current-song playlist (mod (dec (:playlist/order current-song)) (count playlist)))))))

(defn enqueue-last [playlist song]
  (let [highest-order (last (sort (map :playlist/order (:queue playlist))))]
    (update playlist :queue conj (assoc song :playlist/order (inc highest-order)))))

(defn enqueue-next [playlist song]
  (let [[_ current-song] (find-where :playlist/currently-playing? (:queue playlist))]
    (update playlist :queue
            (fn [queue]
              (-> (mapv #(if (> (:playlist/order %) (:playlist/order current-song)) (update % :playlist/order inc) %) queue)
                  (conj (assoc song :playlist/order (inc (:playlist/order current-song)))))))))
