(ns space-invaders.core
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]
            [space-invaders.keys :as keys]))

;; Next up
;; 1. detect when the fleet is at the right edge of the screen
;; 2. move left instead of right
;; 3. dad setup keyboard event listener to put events on a channel
;; 4. read the keyboard events to move the ship
;; 5. rename move-invaders! to main-loop! and make it handle the ship too

(def columns 12)
(def rows 12)
(defonce the-stoplight (atom false))

(defn keyboard-input [stage ev]
  )

(defn make-fleet []
  {:offset 0
   :direction :right
   :invaders
   (mapv (fn [row]
           (mapv (constantly true)
                 (range (- columns 4))))
         (range 2))})

(defn for-indexed! [f coll]
  (doall
   (map-indexed f coll)))

(defn draw-fleet! [stage draw fleet]
  (let [{:keys [offset direction invaders]} fleet]
    (for-indexed! (fn [rown row]
                    (for-indexed! (fn [coln alive?]
                                    (if alive?
                                      (do (draw stage offset rown coln))))
                                  row))
                  invaders))
  fleet)

(defn row-end? [row]
  (and (= (count row)
          columns)
       (true? (last row))))

(defn fleet-move-right [row]
  (cons false row))

(defn add-row [fleet]
  (cons [] fleet))


(defn fleet-move-left [row]
  (if (empty? row)
    row
    (next row)))


(defn row-begin? [row]
  (true? (first row)))


(defn moving-left? [fleet]
  false)

(defn move-invaders [fleet]
  (if (moving-left? fleet)
    fleet
    (if true
      (update fleet :offset inc)
      (assoc fleet :direction :left))))

(defn move-invaders!
  [stage]
  (a/go-loop [fleet (make-fleet)]
    (draw-fleet! stage draw/erase fleet)
    (let [fleet (move-invaders fleet)]
      (draw-fleet! stage draw/invader fleet)
      (a/<! (a/timeout 50))
      (when @the-stoplight
        (recur fleet)))))

(defn stop! [] (swap! the-stoplight (constantly false)))
(defn start! [] (swap! the-stoplight (constantly true)))

(comment
  (draw-fleet! draw/the-stage draw/invader (make-fleet))
  (draw-fleet! draw/the-stage draw/erase (make-fleet))

  (keys/handle!)
  (move-invaders! draw/the-stage)
  (stop!)
  (start!)
  )
