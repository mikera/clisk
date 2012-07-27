(ns clisk.textures
  (:use [clisk core functions patterns util node]))

(def agate
  "A monochrome agate-style rock texture"
  (vscale 0.3 
		(voffset 
		  (v* 4 plasma)
		  (colour-map [[0 0] [0.05 0.5] [0.5 0.9] [0.95 0.5] [1.0 0]] vfrac))))

(def clouds
  "A cloudlike texture"
  (vscale 0.3
     (v- 1 (vpow plasma 3)) ))