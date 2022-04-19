(ns space-invaders.canvas)

(def col-width 40)
(def row-height 40)

(def ^:dynamic *stage*
  (-> js/document (.getElementById "space") (.getContext "2d")))

(defn invader*
  [color offset row col]
  (let [x (+ 5 (* col col-width))
        x (+ x offset)
        y (+ 5 (* row row-height))
        tau (* 2 js/Math.PI)]
    (set! (. *stage* -fillStyle) color)
    (doto *stage*
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
      (.fillRect *stage*
                 (+ x (* tx 5) tx)
                 (+ y 20)
                 5
                 10))

    ;; last tentacle is off by one, so manually scootch the gap out
    (.fillRect *stage* (+ x (* 4 5) 5) (+ y 20) 5 10)))

(def invader (partial invader* "#CCFF33"))
(def uninvader (partial invader* "black"))

(defn rect [ox oy x y wd ht]
  (.fillRect *stage* (+ ox x) (+ oy y) wd ht))

(defn square [ox oy size x y color]
  (set! (. *stage* -fillStyle) color)
  (.fillRect *stage* (+ ox x) (+ oy y) size size))

(def the-ship-shape
  (let [b "#2c71d7"
        w "#ffffff"
        r "#c63a20"
        o "#ff6000"
        y "#f8c823"
        t "#10698e"
        k "#000000"
        c "#2291c8"]
    [[0 0 0 0 0 b b 0 0 0 0 0]
     [0 0 0 0 b k t b 0 0 0 0]
     [0 0 0 b b c t b b 0 0 0]
     [0 0 0 b w b b w b 0 0 0]
     [0 0 b r w b b w r b 0 0]
     [0 0 b r w b b w r b 0 0]
     [0 b t r w b b w r t b 0]
     [0 b t r w b b w r t b 0]
     [b b w w w b b w w w b b]
     [b b b b b b b b b b b b]
     [0 0 r o y y y y o r 0 0]
     [0 0 0 r o y y o r 0 0 0]
     [0 0 0 0 r r r r 0 0 0 0]]))

(defn for-indexed! [f coll]
  (doall
   (map-indexed f coll)))


(defn ship*
  [erase-me {x-off :x y-off :y} row col]
  (let [x (+ x-off (* col col-width))
        y (+ y-off (* row row-height))
        square (partial square x y 3)]
    (for-indexed! (fn [rown row]
                    (for-indexed! (fn [coln color]
                                    (square (* coln 3)
                                            (* rown 3)
                                            (or erase-me
                                                (if (= color 0)
                                                  "#000000"
                                                  color))))
                                  row))
                  the-ship-shape)))
                    
(def ship (partial ship* false))
(def unship (partial ship* "#000000"))
