(ns space-invaders.core
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]
            [space-invaders.keys :as keys]))

(def columns 12)
(def rows 12)
(defonce the-stoplight (atom false))

(defn make-fleet []
  {:offsets {:x 0 :y 0}
   :direction :right
   :invaders
   (mapv (fn [row]
           (mapv (constantly true)
                 (range (- columns 4))))
         (range 2))
   :ship {:x 220 :v 0}
   :bullet nil})

(defn for-indexed! [f coll]
  (doall
   (map-indexed f coll)))

(defn map-invaders [function fleet]
  (->> fleet
       (:invaders)
       (for-indexed!
        (fn [row-index row]
          (for-indexed!
           (fn [col-index invader-at]
             (function row-index col-index invader-at))
           row)))))

(defn rown-coln-boommed-invader[fleet x y]
  (let [offsets (:offsets fleet)]
    (->> fleet
         (map-invaders
          (fn [rown coln invader-at]
            (when invader-at
              (and (> x (+ (:x offsets) (* coln 40) 5))
                   (< x (+ (:x offsets) (* coln 40) 30))
                   (> y (+ (:y offsets) (* rown 40) 5))
                   (< y (+ (:y offsets) (* rown 40) 30)))))))))

(defn xtreme-invader [fleet xtreme]
  (->> fleet
       (:invaders)
       (map-indexed
        (fn [row-index row]
          (->> row
               (map-indexed (fn [index exists]
                              (if (= exists false)
                                nil
                                index)))
               (filter some?)
               (reduce xtreme))))
       (reduce xtreme)))

(defn last-invader [fleet] (xtreme-invader fleet max))
(defn first-invader [fleet] (xtreme-invader fleet min))

(defn end-of-screen-? [fleet]
  (->>
   fleet
   (last-invader)
   (inc)
   (* draw/col-width)
   (+ (:x (:offsets fleet)))
   (<= 480)))

(defn begining-of-screen-? [fleet]
  (->>
   fleet
   (first-invader)
   (+ (:x (:offsets fleet)))
   (>= 0)))

(defn draw-fleet!
  [draw fleet]
  (let [{:keys [offsets direction invaders]} fleet]
    (for-indexed! (fn [rown row]
                    (for-indexed! (fn [coln alive?]
                                    (when alive?
                                      (do (draw offsets rown coln))))
                                  row))
                  invaders))
  fleet)

(defn draw-life! [erase? fleet]
  (let [invader (if erase? draw/uninvader draw/invader)]
    (draw-fleet! invader fleet)
    (draw/ship* erase? (:ship fleet))
    (when-let [bullet (:bullet fleet)]
      (draw/ship-bullet erase? bullet))
    (when-let [esposion (:esposion fleet)]
      (draw/boom erase? esposion (:offsets fleet)))
    fleet))

(defn row-end? [row]
  (and (= (count row)
          columns)
       (true? (last row))))

(defn fleet-move-right [row]
  (cons false row))

(defn add-row [invaders]
  (into [[]] invaders))

(defn fleet-move-left [row]
  (if (empty? row)
    row
    (next row)))


(defn row-begin? [row]
  (true? (first row)))


(defn moving-left? [fleet]
  (= :left (:direction fleet)))

(defn move-down [fleet next]
  (if (>= (get-in fleet [:offsets :y]) 20)
    (->
     fleet
     (update :invaders add-row)
     (assoc :direction next)
     (assoc-in [:offsets :y] 0))
    (-> fleet
        (assoc-in [:offsets :y]  20))))

(defn v-move [fleet]
  (let [{{v :v x :x} :ship} fleet

        x (-> (+ x v) (min draw/the-ship-posish) (max 0))

        v (if (or (= x 0) (= x draw/the-ship-posish))
            0
            v)]
    (-> fleet
        (assoc-in [:ship :x] x )
        (assoc-in [:ship :v] v ))))

(defn decn [n]
  (fn [x]
    (- x n)))

(defn bullet-move [fleet]
  (let [bullet (:bullet fleet)]
    (if (not bullet)
      fleet
      (if (<= (:y bullet) 0)
        (assoc-in fleet [:bullet] nil)
        (update-in fleet [:bullet :y] (decn 5))))))

(defn move-invaders [fleet]
  (prn 'moving fleet)
  (case (:direction fleet)
    (:left)
    (if (begining-of-screen-? fleet)
      (-> fleet
          (assoc :direction :down-right)
          (assoc-in [:offsets :y] 0))
      (update-in fleet [:offsets :x] dec))

    (:right)
    (if (end-of-screen-? fleet)
      (-> fleet
          (assoc :direction :down-left)
          (assoc-in [:offsets :y] 0))
      (update-in fleet [:offsets :x] inc))

    (:down-right)
    (move-down fleet :right)

    (:down-left)
    (move-down fleet :left)))

(defn col-range [fleet coln]
  [(-> fleet (:offsets) (:x) (+ (* coln draw/col-width)))
   (-> fleet (:offsets) (:x) (+ (* (+ coln 1) draw/col-width)))])

(defn row-range [fleet rown]
  [(-> fleet (:offsets) (:y) (+ (* rown draw/row-height)))
   (-> fleet (:offsets) (:y) (+ (* (+ rown 1) draw/row-height)))])

(defn invader-at [fleet x y]
  (let [{:keys [offsets invaders]} fleet
        {ox :x oy :y} offsets
        coln (-> x (- ox) (quot draw/col-width))
        rown (-> y (- oy) (quot draw/row-height))]
    (and (true? (get-in invaders [rown coln]))
         [rown coln])))

(defn boom-teller-bullet [{:keys [bullet invaders] :as fleet}]
  (let [{:keys [x y]} bullet]
    (or (when-let [[rown coln] (invader-at fleet x y)]
          (-> fleet
              (assoc-in [:esposion] {:rown rown :coln coln :boom-level 1})
              (assoc-in [:invaders rown coln] false)
              (dissoc :bullet)
              ))
        fleet)))

(defn boom-teller-ship [{:keys [ship invaders] :as fleet}]
  (let [{:keys [x]} ship
        y draw/the-ship-posish]
    (or (when-let [[rown coln] (or (invader-at fleet (+ x 18) y)
                                   (invader-at fleet (+ x 9) (+ y 13))
                                   (invader-at fleet (+ x 27) (+ y 13)))]
          (make-fleet))
        fleet)))

(def boom-teller (comp boom-teller-bullet boom-teller-ship))

(defn big-boom [fleet]
  (or (when-let [ex (:esposion fleet)]
        (if (= (:boom-level ex) 3)
          (dissoc fleet :esposion)
          (update-in fleet [:esposion :boom-level] inc)))
      fleet))

(def move-life (comp move-invaders v-move bullet-move big-boom boom-teller))

(defn new-bullet [fleet]
  {:x (get-in fleet [:ship :x])
   :y draw/the-ship-posish})

(defn got-command [fleet event]
  (case event
    (:left)
    (update-in fleet [:ship :v] dec)

    (:right)
    (update-in fleet [:ship :v] inc)

    (:shoot :up)
    (if (not (:bullet fleet))
      (assoc-in fleet [:bullet] (new-bullet fleet))
      fleet)
    fleet))

(defn main-loop!
  []
  (draw/Thanos-snap)
  (a/go-loop [fleet (make-fleet)]

    (let [old-fleet fleet
          fleet
          (let [timeout (a/timeout 50)
                [event ch] (a/alts! [keys/the-keys timeout])]
            (cond (= ch timeout)
                  (move-life fleet)

                  :else
                  (got-command fleet event)))]

      (draw-life! true old-fleet)
      (draw-life! false fleet)
      (when @the-stoplight
        (recur fleet)))))

(defn stop! [] (swap! the-stoplight (constantly false)))
(defn start! [] (swap! the-stoplight (constantly true)))

(defn Main-thing []
  (keys/handle!) (start!) (main-loop!))

(defn Stop-thing []
  (keys/remove!) (stop!))


(Main-thing)


(comment
  ;; 1. C-x C-e the next line, it will execute in emacs lisp and start clojure
  ;; (cider-jack-in-cljs '(:cljs-repl-type browser))
  ;; 2. Wait for the browser window to open then, C-c C-z to show the repl buffer
  ;; 3. C-x o to get back to this buffer
  ;; 4o. C-c C-k to load this file


  ;; Do this once, and only remove/re-add the handler if you change the handler
  (keys/handle!)
  (keys/remove!)

  ;; Do this every time
  (start!)
  (main-loop!)
  (stop!)

  ;; Clear the screen
  (draw/Thanos-snap)

  (invader-at? {:invaders [[true true] [true true]]
                :offsets {:x 1 :y 0}
                :bullet {:x 20 :y 20}})

  (-> {:invaders [[true true] [true true]]
       :offsets {:x 1 :y 0}
       :bullet {:x 20 :y 20}}
      (invader-at 2 2)
      ;(boom-teller-bullet)
      ;; (big-boom)
      ;; (big-boom)
      ;; (big-boom)
      )
  )


