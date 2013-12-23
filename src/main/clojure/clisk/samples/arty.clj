(ns clisk.samples.arty
  (:use [clisk live]))

(defn demo []
  ;; patterned grey noise
  (show (offset (v* 0.1 (scale 0.03 vsnoise)) (rgb-from-hsl (v+ [0 0 0.5] (scale 0.3 snoise)))) :size 512)
    
  )