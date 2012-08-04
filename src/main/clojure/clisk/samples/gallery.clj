(ns clisk.samples.gallery
    ^{:author "mikera"
    :doc "Selection of nice sample images."}  
    (:use [clisk core node functions patterns colours textures util]))

;; we define sample gallery as a quoted map
;; so that we can pull out code later
;; TODO: build a simple GUI to show samples with corresponding code

(def samples `{
  :coloured-panels
  ;; vnoise provides a nice colourful plasma texture
  ;; the offset function randomly offsets each 1*1 square
  (show (scale 0.2 
               (offset 
                 (v* 10 (warp vfloor grain)) 
                 vnoise)))
 
  :fractal-landscape
  ;; a factal ladscape
  (show 
	  (scale 0.4 
      (let [z (v+ (v* 2.5 plasma) -0.75)
            colour (landscape-map z)
            height (v* 3.0 (vmax 0.5 z))]
	       (render-lit colour height ))))
    
    })

(def sample-list 
  "List of sample keywords"
  (keys samples))