(ns 
  ^{:author "mikera"
    :doc "Library of clisk patterns generators"}
  clisk.patterns
  
  "Patterns and pattern generators"
  (:use [clisk util node functions])
  (:import java.awt.image.BufferedImage)
  (:import clisk.Perlin))


(def perlin-noise 
  "Standard 4-dimensional scalar perlin noise in range [0..1]"
  '(clisk.Perlin/noise x y z t))

(def clojure
  (texture-map (load-image "Clojure_300x300.png")))

(defn tile
  "Tiles a pattern in the range [0..1,0..1]"
  ([pattern]
    (warp vfrac pattern)))

(def grain
  "Pattern returning a unique vector in [0..1)^4 range value for every point in 4D space"
  vector-hash)

(def noise
  perlin-noise)

(def snoise
  "4-dimensional scalar perlin noise standardised with mean zero, range [-1..1]"
  '(clisk.Perlin/snoise x y z t))

(def plasma 
  "4 dimensional plasma, in range [0..1]"
  (code-node
    (cons 'clojure.core/+
        (map
          (fn [i]
            (let [factor (Math/pow 0.5 (double (inc i)) )]
              `(let [~'x (/ ~'x ~factor)
                     ~'y (/ ~'y ~factor)
                     ~'z (/ ~'z ~factor)
                     ~'t (/ ~'t ~factor)]
                 (clojure.core/* ~factor ~noise))))
          (range 6)))))

(def hash-cubes 
    "4 dimensional randomly coloured unit hypercubes filling space"
    (warp vfloor grain))

(def vnoise 
  "4 dimensional vector perlin noise in range [0..1]^4"
  (vector-offsets noise))

(def vsnoise 
  "4 dimensional vector standardised perlin noise in range [-1..1]^4"
  (vector-offsets snoise))

(def vplasma 
  "4 dimensional vector plasma in range [0..1]^4"
  (vector-offsets plasma))

(defn turbulence
  "Adds random turbulence to a pattern according to a perlin noise offset"
  ([factor func]
    (offset (v* factor vsnoise) func)))

(defmethod clojure.core/print-dup java.awt.image.BufferedImage
  [^BufferedImage bi writer]
  (print-dup "[BufferedImage]" writer))

(defn checker 
  "Checker pattern in (x,y) space, with 2*2 grid in [0..1,0..1] range"
  ([a b]
    (vif '(clojure.core/* 
            (clojure.core/- (clisk.functions/frac x) 0.5)
            (clojure.core/- (clisk.functions/frac y) 0.5))
         a
         b)))

(defn globe
  "Creates a globe, returning the value of the function called 
   on the surface of a unit sphere. 

   (globe) alone produces z values that Can be used as a hight map"
  ([]
    (globe z 0.0))
  ([function]
    (globe function 0.0))
  ([function background]
    (vif 
      (v- 1.0 (length [x y]))
      (warp [x y `(Math/sqrt (- 1.0 ~(:code (dot [x y] [x y]))))] function )
      background)))