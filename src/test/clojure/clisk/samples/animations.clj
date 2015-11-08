(ns clisk.samples.animations
  (:use [clisk live])
  (:import [clisk Util]))

(comment
  ;; we do this to avoid failure in testing if jcodec-javase is not present
  (require '[telegenic.core :as telegenic])
  
  ;; black and white swirly patterns
  (telegenic/encode
    (map 
      (fn [i]
        (let [im (image (offset [0 0 (* i 0.003)]   
                                (scale 0.5 (clisk.live/vabs (clisk.live/length (clisk.live/vsin (clisk.live/v* (clisk.live/lightness-from-rgb (clisk.live/dot [-0.7247 -0.5677] (clisk.live/v+ [-2.4375 -1.7593 -1.0106] [0.7885 -1.7012 1.7523 0.2914]))) (clisk.live/vdivide (clisk.live/saturation-from-rgb (clisk.live/lerp (clisk.live/adjust-hsl (clisk.live/adjust-hsl clisk.live/vsnoise [0.795 2.244 -0.63]) [-1.9613 -1.3866 -0.0114 1.3613]) (clisk.live/y (clisk.live/v* clisk.live/pos 1.2235)) [-1.4063 3.9377999999999997 -1.4972])) (clisk.live/lightness-from-rgb (clisk.live/y (clisk.live/alpha [-2.4185 -1.3271])))))))))  )
                        :width 854 :height 480)]
          (show im)
          im))
      (range 300))
    {:filename "out.mp4"})
  
  
  ;; colourful swirly patterns
  ;; 151104_023134_N.clj
  (telegenic/encode
    (map 
      (fn [i]
        (let [im (image (offset [0 0 (* i 0.003)]   
                                (clisk.live/vabs (clisk.live/lerp (clisk.live/adjust-hsl (clisk.live/adjust-hsl clisk.live/vsnoise [0.795 2.244 -0.63]) [-1.9613 -1.3866 -0.0114 1.3613]) (clisk.live/y (clisk.live/v* clisk.live/pos 1.2235)) [-1.4063 3.9377999999999997 -1.4972])))
                        :width 1920 :height 1080)]
          (show im)
          im))
      (range 600))
    {:filename "out.mp4"})
   
  ;; cloudy skyscape
  ;; 150829_223119_C.clj
  (telegenic/encode
    (map 
      (fn [i]
        (let [im (image (offset [0 0 (* i 0.003)]   
                                (clisk.live/vpow (clisk.live/length (clisk.live/green-from-hsl (clisk.live/rgb-from-hsl (clisk.live/v- clisk.live/pos (clisk.live/blue-from-hsl (clisk.live/rgb-from-hsl (clisk.live/v- (clisk.live/v+ [0.3541 -1.1358] clisk.live/pos) (clisk.live/vsin (clisk.live/vsin clisk.live/vsnoise))))))))) (clisk.live/vpow (clisk.live/length clisk.live/splasma) (clisk.live/sigmoid (clisk.live/gradient (clisk.live/green-from-hsl (clisk.live/rgb-from-hsl (clisk.live/v- clisk.live/vsnoise (clisk.live/vsin (clisk.live/y clisk.live/vsnoise))))))))))
                        :width 854 :height 480)]
          (show im)
          im))
      (range 400))
    {:filename "out.mp4"})
  )