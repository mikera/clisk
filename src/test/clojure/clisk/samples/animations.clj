(ns clisk.samples.animations
  (:use [clisk live])
  (:import [clisk Util]))

(comment
  ;; we do this to avoid failure in testing if jcodec-javase is not present
  (require '[telegenic.core :as telegenic])
  
  (telegenic/encode
    (map 
      (fn [i]
        (let [im (image (offset [0 0 (* i 0.003)]   
                                (scale 0.5 (clisk.live/vabs (clisk.live/length (clisk.live/vsin (clisk.live/v* (clisk.live/lightness-from-rgb (clisk.live/dot [-0.7247 -0.5677] (clisk.live/v+ [-2.4375 -1.7593 -1.0106] [0.7885 -1.7012 1.7523 0.2914]))) (clisk.live/vdivide (clisk.live/saturation-from-rgb (clisk.live/lerp (clisk.live/adjust-hsl (clisk.live/adjust-hsl clisk.live/vsnoise [0.795 2.244 -0.63]) [-1.9613 -1.3866 -0.0114 1.3613]) (clisk.live/y (clisk.live/v* clisk.live/pos 1.2235)) [-1.4063 3.9377999999999997 -1.4972])) (clisk.live/lightness-from-rgb (clisk.live/y (clisk.live/alpha [-2.4185 -1.3271])))))))))  )
                        :width 854 :height 480)]
          (Util/show im)
          im))
      (range 300))
    {:filename "out.mp4"})
  )