(ns clisk.samples
    ^{:author "mikera"
    :doc "Selection of nice sample images."}  
    (:use [clisk core node functions patterns colours textures util]))

(defn samples []
  ;; COLOURED PANELS
  ;; vnoise provides a nice colourful plasma texture
  ;; the offset function randomly offsets each 1*1 square
  (show (scale 0.2 
               (offset 
                 (v* 10 (warp vfloor grain)) 
                 vnoise)))
    
)

