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
         (range 2))})

(defn for-indexed! [f coll]
  (doall
   (map-indexed f coll)))

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
                                    (if alive?
                                      (do (draw offsets rown coln))))
                                  row))
                  invaders))
  fleet)

(defn row-end? [row]
  (and (= (count row)
          columns)
       (true? (last row))))

(defn fleet-move-right [row]
  (cons false row))

(defn add-row [invaders]
  (cons [] invaders))


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
  
     
    

    
    

    

(defn advance-fleet
  [fleet]
  (->> fleet
       (draw-fleet! draw/uninvader)
       (move-invaders)
       (draw-fleet! draw/invader)))

(defn got-command
  [fleet event]
  fleet)

(defn main-loop!
  []
  (a/go-loop [fleet (make-fleet)]

    (let [old-fleet fleet
          fleet
          (let [timeout (a/timeout 50)
                [event ch] (a/alts! [keys/the-keys timeout])]
            (cond (= ch timeout)
                  (advance-fleet fleet)

                  :else
                  (got-command fleet event)))]

      (draw-fleet! draw/uninvader old-fleet)
      (draw-fleet! draw/invader fleet)
      (when @the-stoplight
        (recur fleet)))))

(defn stop! [] (swap! the-stoplight (constantly false)))
(defn start! [] (swap! the-stoplight (constantly true)))

(comment
  ;; 1. C-x C-e the next line, it will execute in emacs lisp and start clojure
  ;; (cider-jack-in-cljs '(:cljs-repl-type browser))
  ;; 2. Wait for the browser window to open then, C-c C-z to show the repl buffer
  ;; 3. C-x o to get back to this buffer
  ;; 4o. C-c C-k to load this file

  (draw-fleet! draw/invader (make-fleet))
  (draw-fleet! draw/uninvader (make-fleet))

  ;; (moving-left? (make-fleet))
  ;; (end-of-screen-? (make-fleet))

  (let [f {:offset 198, :direction :right, :invaders [[true true true true true true true true] [true true true true true true true true]]}]

    (->> (* (last-invader f) draw/col-width)
         (+ (:offset f)))
    )

  (draw/ship {:x 0 :y 0} 0 0)
    
    
  (keys/handle!)
  (start!)
  (main-loop!)

  (map inc
       [1 2 3])
  
  ({1 2 2 3 3 4} 2)

  (keys/remove!)
  (stop!)

  (move-down fleet :right)
  
  )
