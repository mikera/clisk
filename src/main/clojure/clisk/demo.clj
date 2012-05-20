(ns clisk.demo
  (:use clisk.core)
  (:use clisk.functions))

(comment
  
  (show (vscale 10 (vfrac ['x 'y 'z 't])))
  
  (show (vscale 10 (vdot (vfrac ['x 'y 'z 't]) (vfrac ['x 'y 'z 't]))))
  )