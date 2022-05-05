(ns space-invaders.canvas)

(def col-width 40)
(def row-height 40)
(def tau (* 2 js/Math.PI))

(def ^:dynamic *stage*
  (-> js/document (.getElementById "space") (.getContext "2d")))

(defn rown-coln-x-y [rown coln offsets]
 (let [x (+ 5 (* coln col-width))
        x (+ x (:x offsets))
        y (+ 5 (* rown row-height))
       y (+ y (:y offsets))]
   [x y]))

(defn invader* [color offsets row col]
  (let [[x y] (rown-coln-x-y row col offsets)]
           
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

(def ship-tall (* 13 3))
(def canvas-butt 480)
(def the-ship-posish (- canvas-butt ship-tall))

(defn ship*
  [erase-me {x-off :x}]
  (let [erase-me (if (true? erase-me) "#000000" false)
        x x-off 
        y the-ship-posish
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


(defn Thanos-snap []
      (set! (. *stage* -fillStyle) "#000000")
    (doto *stage*
      (.fillRect 0 0 480 480)))      

(defn ship-bullet
  [erase-me {x :x y :y}]
  (let [color  (if erase-me "#000000" "#ffffff")
        square (partial square x y 3)]
    (prn 'ship x y color)
    (square 0 3 color)
    (square 0 0 color)))

(def boom-color { :orange "#ff6000"
                  :yellow "#f8c823" 
                 :white  "#ffffff"})

(defn boom
  [erase-me {:keys [rown coln] stage-me :boom-level} offsets]
  (let [[x y] (rown-coln-x-y rown coln offsets)
        [x y] [(+ x 20) (+ y 20)]
        color  (if erase-me (constantly "#000000") boom-color)
        square (partial square x y 3)]
    (case stage-me
      1 (do (square 0 0 (color :white)))
      2 (do (square 0 0 (color :yellow))
            (square -3 -3 (color :white))
            (square 3 -3 (color :white))
            (square 3 3 (color :white))
            (square 3 -3 (color :white)))
      3 (do (square 0 0 (color :orange))
            (square -3 -3 (color :yellow))
            (square 3 -3 (color :yellow))
            (square 3 3 (color :yellow))
            (square 3 -3 (color :yellow))
            (square 0 -3 (color :yellow))
            (square 0 3 (color :yellow))
            (square 3 0 (color :yellow))
            (square -3 0 (color :yellow))
            (doseq [[x y] [[9 9]
                           [-9 -9]
                           [9 -9]
                           [-9 9]
                           [6 6]
                           [-6 -6]
                           [6 -6]
                           [-6 6]]]
              (square x y (color :white)))))))


            
    
