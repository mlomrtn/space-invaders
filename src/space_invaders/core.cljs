(ns space-invaders.core
  (:require [core.async :as a]))

(def columns 12)
(def rows 12)
(def col-width 40)
(def row-height 40)

(defn invader [stage row col]
  (let [x (+ (* col col-width) 20)
        y (+ (* row row-height) 20)]
    (doto stage
      (.beginPath)
      (.arc x y 15 0 (* 2 js/Math.PI) false)
      (set! -fillStyle "red")
      (.fill)
      ;; (set! -lineWidth 5)
      ;; (set! -strokeStyle "#030")
      (.stroke))))

(defn draw-ship [stage row col]
  (let [x (* col col-width)
        y (* row row-height)]
    (set! stage -fillStyle "blue")
    (.fillRect stage x (+ y 10) 40 30)
    (.fillRect stage (+ x 15) y 10 10)))

(defn make-fleet []
  (mapv (fn [row]
          (mapv (constantly true)
                (range (- columns 4))))
        (range 2)))

(defn draw-fleet [stage fleet]
  (map-indexed (fn [rown row]
                 (map-indexed (fn [coln alive?]
                                (when alive?
                                  (invader stage rown coln)))))))

(defn row-end? [row]
  (and (= (count row)
          colums)
       (true? (last row))))

(defn row-move [row]
  (cons false row))

(defn move-invaders [stage fleet]
  (let [direction +]
    (a/go-loop []
      (a/<! (a/timeout 500))
      )
    )
  )

(defn keyboard-input [stage ev]
  
  
  )

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
      (invader stage 1 (inc c)))

    (ship stage 12 6)))

(init)
