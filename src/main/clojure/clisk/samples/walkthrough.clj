(ns clisk.samples.walkthrough
  "A quick walkthrough of clisk's code functionaliyy"
  (:use [clisk core node functions patterns colours textures util]))

;; =========================================
;; Usage:
;;   Run each of the individual "show" lines in the walkthough

(comment 
  
;; colours in RGB  
(show [1 0 0])

;; named colours
(show pink)

;; scalars get interpreted as greyscale
(show 0.5)

;; adding in a variable
(show [x 0 0])
(show [x y 0])

;; multiplication
(show (v* [x y 0] 5))


;; what's under the hood?
(node [x y 0])

(let [a (node [x y 0])]
  (:codes a))

(let [a (node (v* [x y 0] 5))]
  (:codes a ))


;; predefined paterns
(show (checker black white))

;; scale a pattern - make a chessboard!
(show (scale 0.25 (checker white black)))

;; nesting a pattern
(show (checker black
               (scale 0.25 (checker red yellow))))

;; offseting a pattern
(show (offset 
        [0.2 0.2]
        (checker black (scale 0.25 (checker red yellow)))))

(show (offset 
        [(v* 0.3 y) 0]
        (checker black (scale 0.25 (checker red yellow)))))
)

