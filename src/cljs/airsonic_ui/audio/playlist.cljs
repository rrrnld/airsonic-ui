(ns airsonic-ui.audio.playlist
  "Implements playlist queues that support different kinds of repetition and
  song ordering."
  (:refer-clojure :exclude [peek])
  (:require [airsonic-ui.utils.helpers :refer [find-where]]
            [debux.cs.core :refer-macros [dbg]]))

(defrecord Playlist [queue playback-mode repeat-mode]
  cljs.core/ICounted
  (-count [this]
    (count (:queue this))))

(defmulti ->playlist
  "Creates a new playlist that behaves according to the given playback- and
  repeat-mode parameters."
  (fn [queue playback-mode repeat-mode] playback-mode))

(defn- mark-first-song [queue]
  (let [[first-idx _] (find-where #(= 0 (:order %)) queue)]
    (assoc-in queue [first-idx :currently-playing?] true)))

(defmethod ->playlist :playback-mode/linear
  [queue playback-mode repeat-mode]
  (let [queue (-> (mapv (fn [order song] (assoc song :order order)) (range) queue)
                  (mark-first-song))]
    (->Playlist queue playback-mode repeat-mode)))

(defn- -shuffle-songs [queue]
  (->> (shuffle (range (count queue)))
       (mapv (fn [song order] (assoc song :order order)) queue)))

(defmethod ->playlist :playback-mode/shuffled
  [queue playback-mode repeat-mode]
  (let [queue (conj (mapv #(update % :order inc) (-shuffle-songs (rest queue)))
                    (assoc (first queue) :order 0 :currently-playing? true))]
    (->Playlist queue playback-mode repeat-mode)))

(defn set-current-song
  "Marks a song in the queue as currently playing, given its ID"
  [playlist next-idx]
  (let [[current-idx _] (find-where :currently-playing? (:queue playlist))]
    (-> (if current-idx
          (update-in playlist [:queue current-idx] dissoc :currently-playing?)
          playlist)
        (assoc-in [:queue next-idx :currently-playing?] true))))

(defn set-playback-mode
  "Changes the playback mode of a playlist and re-shuffles it if necessary"
  [playlist playback-mode]
  (if (= playback-mode :playback-mode/shuffled)
    ;; for shuffled playlists we reorder the songs make sure that the currently
    ;; playing song has order 0
    (let [playlist (->playlist (:queue playlist) playback-mode (:repeat-mode playlist))
          [current-idx current-song] (find-where :currently-playing? (:queue playlist))
          [swap-idx _] (find-where #(= 0 (:order %)) (:queue playlist))]
      (-> (assoc-in playlist [:queue current-idx :order] 0)
          (assoc-in [:queue swap-idx :order] (:order current-song))))
    ;; for linear songs we just make sure that the current does not change
    (let [[current-idx _] (find-where :currently-playing? (:queue playlist))]
      (-> (->playlist (:queue playlist) playback-mode (:repeat-mode playlist))
          (set-current-song current-idx)))))

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

(defmulti next-song "Advances the currently playing song" :repeat-mode)

(defmethod next-song :repeat-mode/none
  [playlist]
  ;; this is pretty easy; get the next song and stop playing at the at
  (let [[current-idx current-song] (find-where :currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:order %) (inc (:order current-song))) (:queue playlist))]
    (update playlist :queue
            (fn [queue]
              (cond-> queue
                current-idx (update current-idx dissoc :currently-playing?)
                next-idx (assoc-in [next-idx :currently-playing?] true))))))

(defmethod next-song :repeat-mode/single [playlist] playlist)

(defmethod next-song :repeat-mode/all
  [playlist]
  (let [[current-idx current-song] (find-where :currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:order %) (inc (:order current-song))) (:queue playlist))]
    (-> (update-in playlist [:queue current-idx] dissoc :currently-playing?)
        (update :queue
                (fn [queue]
                  ;; we need special treatment here if we're playing the last song and
                  ;; have a shuffled playlist because we need to re-shuffle
                  (if next-idx
                    (assoc-in queue [next-idx :currently-playing?] true)
                    (case (:playback-mode playlist)
                      :playback-mode/linear (assoc-in queue [0 :currently-playing?] true)
                      :playback-mode/shuffled (let [queue' (-shuffle-songs queue)
                                                    [next-idx _] (find-where #(= (:order %) 0) queue')]
                                                (assoc-in queue' [next-idx :currently-playing?] true)))))))))

(defmulti previous-song "Goes back along the playback queue" :repeat-mode)

(defmethod previous-song :repeat-mode/single [playlist] playlist)

(defmethod previous-song :repeat-mode/none [playlist]
  (let [[current-idx current-song] (find-where :currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:order %) (dec (:order current-song))) (:queue playlist))]
    (set-current-song playlist (or next-idx current-idx))))

(defmethod previous-song :repeat-mode/all [playlist]
  (let [[_ current-song] (find-where :currently-playing? (:queue playlist))
        [next-idx _] (find-where #(= (:order %)
                                     (rem (dec (:order current-song)) (count playlist)))
                                 (:queue playlist))]
    (if next-idx
      (set-current-song playlist next-idx)
      (if (= :playback-mode/shuffled (:playback-mode playlist))
        (let [highest-order (dec (count playlist))
              playlist (update playlist :queue -shuffle-songs)
              [last-idx _] (find-where #(= (:order %) highest-order) (:queue playlist))]
          (set-current-song playlist last-idx))
        (set-current-song playlist (mod (dec (:order current-song)) (count playlist)))))))

(defn enqueue-last [playlist song]
  (let [highest-order (last (sort (map :order (:queue playlist))))]
    (update playlist :queue conj (assoc song :order (inc highest-order)))))

(defn enqueue-next [playlist song]
  (let [[_ current-song] (find-where :currently-playing? (:queue playlist))]
    (update playlist :queue
            (fn [queue]
              (-> (mapv #(if (> (:order %) (:order current-song)) (update % :order inc) %) queue)
                  (conj (assoc song :order (inc (:order current-song)))))))))
