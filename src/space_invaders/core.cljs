(ns space-invaders.core
  (:require [clojure.core.async :as a]
            [space-invaders.canvas :as draw]
            [space-invaders.keys :as keys]))

(def columns 12)
(def rows 12)
(defonce the-stoplight (atom false))

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

(defn draw-fleet!
  [draw fleet]
  (let [{:keys [offset direction invaders]} fleet]
    (for-indexed! (fn [rown row]
                    (for-indexed! (fn [coln alive?]
                                    (if alive?
                                      (do (draw offset rown coln))))
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
          (let [timeout (a/timeout 500)
                [event ch] (a/alts! [keys/the-keys timeout])]

            (prn 'EVENT
                 event
                 (= ch timeout)
                 (= ch keys/the-keys)
                 )
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
  ;; 4. C-c C-k to load this file

  (draw-fleet! draw/invader (make-fleet))
  (draw-fleet! draw/uninvader (make-fleet))

  (keys/handle!)
  (start!)
  (main-loop!)

  (keys/remove!)
  (stop!)
  )
