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
  (fn [queue play-idx playback-mode repeat-mode] playback-mode))

(defn- playlist-queue
  [queue play-idx]
  (concat (take play-idx queue)
          [(assoc (nth queue play-idx) :currently-playing? true)]
          (drop (inc play-idx) queue)))

(defmethod ->playlist
  :playback-mode/linear
  [queue play-idx playback-mode repeat-mode]
  (let [queue (->> (playlist-queue queue play-idx)
                   (map (fn [order song] (assoc song :order order))
                        (range (count queue))))]
    (->Playlist queue playback-mode repeat-mode)))

(defmethod ->playlist
  :playback-mode/shuffle
  [queue play-idx playback-mode repeat-mode]
  (let [queue (->> (playlist-queue queue play-idx)
                   (map (fn [order song] (assoc song :order order))
                        (shuffle (range (count queue)))))]
    (->Playlist queue playback-mode repeat-mode)))

(defn set-playback-mode
  "Changes the playback mode of a playlist and re-suffles it if necessary"
  [playlist playback-mode]
  (let [current-idx (first (keep-indexed (fn [idx item]
                                           (when (:currently-playing? item)
                                             idx))
                                         (:queue playlist)))]
    (->playlist (:queue playlist) current-idx playback-mode (:repeat-mode playlist))))

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

(defmulti next-song (juxt :playback-mode :repeat-mode))
(defmulti previous-song (juxt :playback-mode :repeat-mode))
