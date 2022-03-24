(ns space-invaders.core
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]))

(def columns 12)
(def rows 12)

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

(defn draw-fleet! [stage draw fleet]
  (let [{:keys [offset direction invaders]} fleet]
    (map-indexed (fn [rown row]
                 (map-indexed (fn [coln alive?]
                                (when alive?
                                  (draw stage offset rown coln)))
                              row))
                 invaders)))

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
     
     

(defn move-invaders! [stage]
  (a/go-loop [fleet (make-fleet)]
    (draw-fleet! stage draw/erase fleet)
    (let [fleet (move-invaders fleet)]
      (draw-fleet! stage draw/invader fleet)
      (prn (:offset fleet))
      (a/<! (a/timeout 500))
      (recur fleet))))



(comment

  (move-invaders (make-fleet))

  (move-invaders! draw/the-stage)

  (draw-fleet! draw/the-stage draw/invader (make-fleet))
  (draw-fleet! draw/the-stage draw/erase (make-fleet))  

  (let [s draw/the-stage]
    (draw-fleet s (make-fleet))
    (draw/erase s 11 5)
    (draw/ship s 11 5))
  )
