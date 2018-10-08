(ns airsonic-ui.components.podcast.events)

(defn subscribe-to-channel
  [db [_ channel-url]])

(defn delete-podcast-channel
  [db [_ channel-id]])

(defn download-episode
  [db [_ episode-id]])

(defn delete-episode
  [db [_ episode-id]])
