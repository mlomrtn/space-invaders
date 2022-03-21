(ns space-invaders.core
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]))

(def columns 12)
(def rows 12)

(defn keyboard-input [stage ev]
  )

(defn make-fleet []
  (mapv (fn [row]
          (mapv (constantly true)
                (range (- columns 4))))
        (range 2)))

(defn draw-fleet [stage fleet]
  (map-indexed (fn [rown row]
                 (map-indexed (fn [coln alive?]
                                (when alive?
                                  (draw/invader stage rown coln)))
                              row))
               fleet))

(defn row-end? [row]
  (and (= (count row)
          columns)
       (true? (last row))))

(defn row-move-right [row]
  (cons false row))

(defn add-row [fleet]
  (cons [] fleet))


(defn row-move-left [row]
  (if (empty? row)
    row
    (next row)))


(defn row-begin? [row]
  (true? (first row)))


(defn moving-left? [fleet]
  (odd? (count (take-while empty? fleet))))

(defn move-invaders [fleet]
  (if (moving-left? fleet)
    (if (some row-begin? fleet)
      (add-row fleet)
      (map row-move-right fleet))
    (if (some row-end? fleet)
      (add-row fleet)
      (map row-move-left fleet))))

(defn move-invaders! [stage]
  (a/go-loop [fleet (make-fleet)]
    (draw-fleet stage fleet)
    (a/<! (a/timeout 500))
    (recur (move-invaders fleet))))

(defn init []
  (let [stage (-> js/document
                  (.getElementById "space")
                  (.getContext "2d"))
        fleet (map (fn [x]
                     [1 (inc x)])
                   (range 11)
                   )

        ]

    (dotimes [c 11]
      (draw/invader stage 1 (inc c)))))

(defn tryit []
  (draw-fleet draw/the-stage (make-fleet))
  )

(comment
  (init)
  (tryit)

  (let [the-stage draw/the-stage]
    (draw/erase the-stage 11 5)
    (draw/ship the-stage 11 5))
  )
