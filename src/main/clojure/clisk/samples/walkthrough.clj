(ns clisk.samples.walkthrough
  "A quick walkthrough of clisk's code functionality"
  (:use [clisk core node functions patterns colours textures util]))

;; =========================================
;; Usage:
;;   Run each of the individual "show" lines in the walkthough

(comment 
  
;; colours in RGB  
(show [1 0 0])


;; scalars get interpreted as greyscale
(show 0.5)
(show [0.5 0.5 0.5])

;; named colours
(show pink)

;; adding in a variable
(show [x 0 0])
(show [0 y 0])

;; addition
(show (v+ 
        [x 0 0] 
        [0 y 0])) 
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

;; variable offset
(show (offset 
        [(v* 0.3 y) 0]
        (checker black (scale 0.25 (checker red yellow)))))

(def sin-wave (v* 0.1 (vsin (v* 15 y))))

(show (offset 
        [sin-wave]
        (checker black (scale 0.25 (checker red yellow)))))

;; HSL
(show (rgb-from-hsl [x y 0.5]))

;; texture map
(show cannon)

;; now to make some noise
(show (scale 0.2 noise))

;; noise with red, green and blue
(show (scale 0.2 vnoise))



)
