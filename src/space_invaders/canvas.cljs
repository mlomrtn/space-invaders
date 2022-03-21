(ns space-invaders.canvas)

(def col-width 40)
(def row-height 40)

(def the-stage
  (-> js/document (.getElementById "space") (.getContext "2d")))

(defn invader [stage row col]
  (let [x (+ 5 (* col col-width))
        y (+ 5 (* row row-height))
        tau (* 2 js/Math.PI)]
    (set! (. stage -fillStyle) "#CCFF33")
    (doto stage
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

(defn rect [stage ox oy x y wd ht]
  (.fillRect stage (+ ox x) (+ oy y) wd ht))

(defn ship [stage row col]
  (let [x (* col col-width)
        y (* row row-height)
        rect (partial rect stage x y)

        wing-col-wd (/ col-width 9)
        wing-row-ht (/ row-height 9)

        left-wing
        (fn []
          (rect (- wing-col-wd) (* 6 wing-row-ht) wing-col-wd wing-row-ht)
          (rect (- (* 2 wing-col-wd)) (* 7 wing-row-ht) (* 2 wing-col-wd) wing-row-ht)
          (rect (- (* 3 wing-col-wd)) (* 8 wing-row-ht) (* 3 wing-col-wd) wing-row-ht))

        right-wing
        (fn []
          (rect col-width (* 6 wing-row-ht) wing-col-wd wing-row-ht)
          (rect col-width (* 7 wing-row-ht) (* 2 wing-col-wd) wing-row-ht)
          (rect col-width (* 8 wing-row-ht) (* 3 wing-col-wd) wing-row-ht))]

    (set! (. stage -fillStyle) "#66ccff")
    (.fillRect stage x (+ y 10) 40 30)
    (.fillRect stage (+ x 15) y 10 10)

    (set! (. stage -fillStyle) "white")
    (left-wing)
    (right-wing)))

(defn erase [stage row col]
  (let [wing-wd (/ col-width 3)
        x (- (* col col-width) wing-wd)
        y (* row row-height)]
    (set! (. stage -fillStyle) "black")
    (.fillRect stage x y (+ col-width (* 2 wing-wd)) row-height)))
