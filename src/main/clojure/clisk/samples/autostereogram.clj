(ns clisk.samples.autostereogram
  "Demo of creating an autostereogram using Clisk.

   To see the 3D effect, you need to cross your eyes while looking at the image at
   a comfortable distance from the screen. If you do it right, a 3D pattern should appear.

   See also: https://en.wikipedia.org/wiki/Autostereogram"
  (:use [clisk live]))

(defn demo []
  
  ;; first we define a pattern to create the autostereogram
  ;;
  ;; We do this by
  ;; 1. creating a seamless repeating pattern pattern 
  ;; 2. scaling it down e.g. with a factor of 0.2 to make it repeat multiple times over the image
  (def pattern (scale 0.2 (seamless (scale 0.1 vnoise))))
  
  ;; Next we create a depth map
  ;; Any pattern can be used for this, but you typically want something that varies smoothly from
  ;; 0 to one over well-defined features
  ;; (voronoi-blocks) is a reasonable choice
  (def height-map (voronoi-blocks))

  ;; Finally we create the image by using the height-map to offset the pattern slightly 
  ;; along the x-axis. A higher offset factor results in "steeper" depth perception
  ;; experiments show that a range from 0.01 to 0.10 can be reasonable choices
  (show (offset (v* [0.03 0.0] height-map) pattern) :size 512)
  )