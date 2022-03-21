(ns space-invaders.core
  (:require [clojure.core.async :as a]))

(def columns 12)
(def rows 12)
(def col-width 40)
(def row-height 40)

(def the-stage
  (-> js/document (.getElementById "space") (.getContext "2d")))

(defn invader [stage row col]
  (let [x (+ 5 (* col col-width))
        y (+ 5 (* row row-height))
        tau (* 2 js/Math.PI)]
    (doto stage
      (set! -fillStyle "#CCFF33")
      (.fillRect (+ x 5) y 20 5)        ; head
      (.fillRect x (+ y 5) 30 15)       ; body

      ;; left curve
      (.beginPath)
      (.arc (+ x 5) (+ y 5) 5 0 tau false)
      (.fill)

      ;; right curve
      (.beginPath)
      (.arc (+ x 25) (+ y 5) 5 0 tau false)
      (.fill))

    (doseq [tx (range 4)]
      (.fillRect stage
                 (+ x (* tx 5) tx)
                 (+ y 20)
                 5
                 10))

    ;; last tentacle is off by one, so manually scootch the gap out
    (.fillRect stage (+ x (* 4 5) 5) (+ y 20) 5 10)))

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

(comment
  (init)
 )
