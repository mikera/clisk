(ns clisk.demo
  (:use clisk.core)
  (:use clisk.functions)
  (:use clisk.patterns))

(comment
  
  ;; coloured red/green ramps using frac
  (show (vscale 0.1 vfrac))
  
  ;; Dot products
  (show (vscale 0.25 (vdot vfrac vfrac)))
  
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
  
  ;; hash cube colours
  (show (vscale 0.1 (vwarp vfloor vhash)))
  
  ;; vnoise warped by hash cubes
  (show (vscale 0.2 (voffset (v* 10 (vwarp vfloor vhash)) vnoise)))
  )