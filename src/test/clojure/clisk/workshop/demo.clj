(ns clisk.workshop.demo
  (:use [clisk live])
  (:require [telegenic.core :as telegenic]))


;; Image composition example
(comment 
  (show (scale 0.05 (vsin x)))
  
  (show (scale 0.5 (rgb-from-hsl vnoise)))
  
  (show (v* (scale 0.05 (vsin x)) (scale 0.5 (rgb-from-hsl vnoise))))
  )

;; Walkthrough
(comment 
  ;; red along x-axis
  (show [x 0 0])
  
  ;; red along x-asis, green along y-axis
  (show [x y 0])
  
  ;; solid colour
  (show [0.1 0.7 1.0])

  ;; paterns / checker
  (show (checker red pink))
  
  ;; texture maps
  (show clojure)

  ;; scaling
  (show 
    (scale 0.3
      (checker red pink)))
  
  ;; offset
  (show 
    (offset 
      [0.2 0.2]
      (checker red pink)))
  
  ;; warp
  (show 
    (warp
      (scale 0.2 vnoise)
      (checker red pink)))

  ;; monochromatic noise
  (show (scale 0.2 noise))
  
  ;; vector noise
  (show (scale 0.2 vnoise))
  
  ;; plasma
  (show (scale 0.2 plasma))
  )