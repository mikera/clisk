(ns 
  ^{:author "mikera"
    :doc "Demonstration images."}  
  clisk.demo
  (:use [clisk core node functions patterns colours textures util]))

(defn demo []
  
  ;; coloured red/green ramps using frac
  (show (scale 0.1 vfrac))
  
  ;; Dot products
  (show (scale 0.25 (dot vfrac vfrac)))
  
  ;; Chess board
  (show (scale 0.25 (checker 0 1)))
  
  ;; Basic perlin noise
  (show (scale 0.1 noise))
  
  ;; offset of checkers using perlin noise
  ;; 512*512 output
  (show 
    (scale 0.2 
            (offset
              (v* 7 vnoise) 
              (checker [1 0 0] [1 1 0]))) 512 512)
  
  ;; grain-based cube colours
  (show (scale 0.1 (warp vfloor grain)))
  
  ;; vnoise warped by grain-based cubes
  (show (scale 0.2 (offset (v* 10 (warp vfloor grain)) vnoise)))
  
  ;; basic colour map in y-direction
  (show (colour-map [[0   [1 1   1]] 
                     [0.5 [1 0.5 0]] 
                     [1   [0 0   0]]] 
                    'y))
  
    ;; colour map with variable components
  (show (colour-map [[0 [1 1   'z        ]] 
                     [x [1 0.5 0         ]] 
                     [1 [0 0   `(- 1 ~'x)]]] 
                    y))

  ;; tileable rock texture with faked lighting
  (show  (v+ [0.9 0.6 0.3] 
             (dot [0.2 0.2 0] 
                  (gradient (seamless 1.0 plasma) ))))
  
  ;; aplha blend using vlerp
  (show (lerp (v- (v* 4 plasma) 1.3) 
               (scale 0.1 (checker 0 1))
               [1 0 0]))
  
   ;; aplha blend using vlerp
  (show (lerp (v- (v* 4 plasma) 1.3) 
               (scale 0.1 (checker 0 1))
               [1 0 0]))
  
   ;; texture mapping example
   (show (texture-map (clisk.util/load-image "Clojure_300x300.png")) 300 300 )

   ;; tiled clojure pattern
   (show (scale 0.1 (tile clojure)))
   
   ;; interesting noisy wood patterns
   (show (v* [1.0 0.7 0.3] (offset (v* noise plasma 5) wood)))
   
   ;; purple vortex-like texture
   (show (v* [0.9 0.7 1.0] (offset (v* noise flecks 1) agate)))
   
   ;; colour polar co-ordinates
   (show (scale 0.1 [(vfrac radius) (vfrac theta)]))
 )