(ns clisk.demo
  (:use [clisk core node functions patterns colours util]))

(defn demo []
  
  ;; coloured red/green ramps using frac
  (show (vscale 0.1 vfrac))
  
  ;; Dot products
  (show (vscale 0.25 (dot vfrac vfrac)))
  
  ;; Chess board
  (show (vscale 0.25 (checker 0 1)))
  
  ;; Basic perlin noise
  (show (vscale 0.1 noise))
  
  ;; offset of checkers using perlin noise
  ;; 512*512 output
  (show 
    (vscale 0.2 
            (voffset
              (v* 7 vnoise) 
              (checker [1 0 0] [1 1 0]))) 512 512)
  
  ;; hash cube colours
  (show (vscale 0.1 (vwarp vfloor vhash)))
  
  ;; vnoise warped by hash cubes
  (show (vscale 0.2 (voffset (v* 10 (vwarp vfloor vhash)) vnoise)))
  
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
                  (vgradient (vseamless 1.0 plasma) ))))
  
  ;; aplha blend using vlerp
  (show (vlerp (v- (v* 4 plasma) 1.3) 
               (vscale 0.1 (checker 0 1))
               [1 0 0]))
  
   ;; aplha blend using vlerp
  (show (vlerp (v- (v* 4 plasma) 1.3) 
               (vscale 0.1 (checker 0 1))
               [1 0 0]))
  
   ;; texture mapping example
   (show (texture-map (clisk.util/load-image "Clojure_300x300.png")) 300 300 )

   
   )