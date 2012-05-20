(ns clisk.demo
  (:use clisk.core)
  (:use clisk.functions)
  (:use clisk.patterns))

(comment
  
  (show (vscale 10 (vfrac pos)))
  
  (show (vscale 10 (vdot (vfrac pos) (vfrac pos))))
  
  ;; Chess board
  (show (vscale 4 (checker 0 1)))
  
  
  )