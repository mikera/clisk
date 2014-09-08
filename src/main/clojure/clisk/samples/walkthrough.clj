(ns clisk.samples.walkthrough
  "A quick walkthrough of clisk's code functionality"
  (:use [clisk core node functions patterns colours textures util]))

(fn [] 

;; =========================================
;; Usage:
;;   Run each of the individual "show" lines in the walkthough

  
;; colours in RGB  
(show [1 0 1])


;; scalars get interpreted as greyscale
(show 0.2)
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

(def sin-wave (v* 0.1 (vsin (v* 15 x))))

(show (offset 
        [0 sin-wave]
        (checker black (scale 0.25 (checker red yellow)))))

;; HSL
(show (rgb-from-hsl [x y 0.5]))

;; texture map
(show cannon)

;; hue adjustment
(show (v- 1.0 (adjust-hsl [y 0 0] cannon)))

(show (v- 1.0 cannon))

;; now to make some noise
(show (v+
        (v* 0.5 green (scale 0.1 noise))
        (v* 0.9 red (scale 0.1 noise))
        (v* 0.8 orange (scale 0.1 noise))
        (v* 0.7 pink (scale 0.5 noise))
        (v* 0.6 blue (scale 0.2 noise))))

;; noise with red and blue
(show (scale 0.5 vnoise [0 0 1]))

(show (v* [6 15 10] (scale 0.1 vnoise) cannon))

;; plasma - sum of noise at different scales
(show (v* [0 0 5] vplasma cannon))

;; noise as an offset
(show (offset
        (v* 10 plasma)
        (checker blue red)))

;; plasma-based fractal landscape
  (show 
	  (scale 0.3 
      (let [z (v+ (v* 2.5 plasma) -0.75)
            colour (landscape-map z)
            height (v* 3.0 (vmax 0.5 z))]
	       (render-lit colour height ))))

;; plasma based colours
(show (seamless 0.3 (vplasma vsnoise)))

;; mandelbrot set
 (show (viewport [-2 -1.5] [1 1.5]
          (fractal 
            :while (v- 2 (length [x y])) 
            :update (v+ c [(v- (v* x x) (v* y y))  (v* 2 x y)]) 
            :result (vplasma (v* 0.1 'i))
            :bailout-result black
            :max-iterations 1000)))
)

(show phone)