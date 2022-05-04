(ns quiz.one)

;;; Fix the indentation so that this expression is readable:
(= 94
   (/ (+ (* 2 3)
         (* 3 90)
         (+ 1 2 3))
      3))

;;; Make the map in the let binding that makes the test return true:
(let [one-2]
  (and (= 1 (:a one-2))
       (= 2 (:b one-2))
       (= 3 (:c one-2))))

;;; Is there any difference in the meaning of these two definitions?
(def square-1
  (fn [x]
    (* x x)))

(defn square-2 [x]
  (* x x))

;;; What value does the repl return for this expression?
(fn [x] (+ x 2))

;;; What about this one?
((fn [x] (+ x 2)) 2)

;;; What does this do?
(map square-1 [1 2 3])

;;; What does this do?
(->> [1 2 3]
     (map square-1))

;;; Lookup the documentation for `filter`, `even?`, and `count`, all
;;; built-in functions. How many even squares are there in with square
;;; roots between 1 and 99?
(->> (range 1 99)
     (map square-1)
     )
