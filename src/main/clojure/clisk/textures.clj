(ns clisk.textures
  (:use [clisk core functions patterns colours util node]))

(def agate
  "A monochrome agate-style rock texture"
  (vscale 0.3 
		(voffset 
		  (v* 4 plasma)
		  (colour-map [[0 0] [0.05 0.5] [0.5 1.0] [0.95 0.5] [1.0 0]] vfrac))))

(def clouds
  "A cloudlike texture"
  (vscale 0.3
     (v- 1 (vpow plasma 3)) ))

(def flecks
  "Stranges wispy flecks"
  (vscale 0.1 (v* 2.0 (apply-to-components min vnoise))))

(def wood
  "Spherical wood-like texture centred at origin"
  (vscale 0.1 (colour-map [[0 0] [1 1]] (vfrac length))))