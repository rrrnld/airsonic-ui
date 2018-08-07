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

(defmethod ->playlist :playback-mode/shuffle
  [queue playing-idx playback-mode repeat-mode]
  (let [queue (->> (playlist-queue queue playing-idx)
                   (mapv (fn [order song] (assoc song :order order))
                         (shuffle (range (count queue)))))]
    (->Playlist queue playback-mode repeat-mode)))

(defn- current-idx [playlist]
  (first (keep-indexed (fn [idx item]
                         (when (:currently-playing? item)
                           idx))
                       (:queue playlist))))

(defn set-playback-mode
  "Changes the playback mode of a playlist and re-shuffles it if necessary"
  [playlist playback-mode]
  (->playlist (:queue playlist) (current-idx playlist)
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

(defn- ?assoc
  "Like assoc, but returns coll unchanged if (pref coll) is false"
  [coll pred & assog-args]
  (if (pred coll) (apply assoc coll assog-args) coll))

(defmulti next-song (juxt :playback-mode :repeat-mode))

(defmethod next-song [:playback-mode/linear :repeat-mode/none]
  [playlist]
  (let [current-idx (current-idx playlist)
        next-idx (inc current-idx)]
    (println "next-idx" next-idx "current-idx" current-idx "(< next-idx (count playlist))" (< next-idx (count playlist)))
    (update playlist :queue
            (fn [queue] (-> (update queue current-idx dissoc :currently-playing?)
                            (?assoc #(< next-idx (count playlist))
                                    next-idx (assoc (get queue next-idx)
                                                    :currently-playing? true)))))))

(defmethod next-song [:playback-mode/linear :repeat-mode/single]
  [playlist]
  playlist)

(defmethod next-song [:playback-mode/linear :repeat-mode/all]
  [playlist]
  (let [current-idx (current-idx playlist)
        next-idx (mod (inc current-idx) (count playlist))]
    (update playlist :queue
            (fn [queue] (-> (update queue current-idx dissoc :currently-playing?)
                            (assoc-in [next-idx :currently-playing?] true))))))

(defmethod next-song [:playback-mode/shuffle :repeat-mode/single]
  [playlist]
  playlist)

(defmulti previous-song (juxt :playback-mode :repeat-mode))
