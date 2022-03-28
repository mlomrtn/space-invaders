(ns space-invaders.keys
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]))

(defonce the-keys (a/chan))

(def command?
  #{:left
    :right
    :up
    :down
    :shoot})

(defn event-handler
  [ev]
  (when-not (.-defaultPrevented ev)
    (when-let [e (case (.-code ev)
                   ("KeyW" "ArrowUp") :up
                   ("KeyA" "ArrowLeft") :left
                   ("KeyS" "ArrowDown") :down
                   ("KeyD" "ArrowRight") :right
                   ("Space") :shoot
                   nil)]
      (prn 'KEY e)
      (.preventDefault ev)
      (a/put! the-keys e))))

(defn handle!
  []
  (prn 'HANDLER event-handler)
  (-> js/window
      (.addEventListener "keydown" event-handler)))

(defn remove!
  []
  (prn 'HANDLER event-handler)
  (-> js/window
      (.removeEventListener "keydown" event-handler)))
