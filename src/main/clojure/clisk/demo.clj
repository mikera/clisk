(ns clisk.demo
  (:use clisk.core)
  (:use clisk.functions)
  (:use clisk.patterns))

(comment
  
  (show (vscale 0.1 (vfrac pos)))
  
  (show (vscale 0.1 (vdot (vfrac pos) (vfrac pos))))
  
  ;; Chess board
  (show (vscale 0.25 (checker 0 1)))
  
  ;; Basic perlin noise
  (show (vscale 0.1 noise))
  
  ;; offset of checkers using perlin noise
  (show 
    (vscale 0.2 
            (voffset
              (v* 7 vnoise) 
              (checker [1 0 0] [1 1 0]))) 512 512)
  
  
  )