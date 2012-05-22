(ns clisk.patterns
  (:use clisk.functions)
  (:import clisk.Perlin))


(def perlin-noise 
  '(clisk.Perlin/noise x y z t))

(def noise
  perlin-noise)

(def snoise
  '(clisk.Perlin/snoise x y z t))

(def plasma 
  (cons 'clojure.core/+
        (map
          (fn [i]
            (let [factor (Math/pow 0.5 (inc i))]
              `(let [~'x (/ ~'x ~factor)
                     ~'y (/ ~'y ~factor)
                     ~'z (/ ~'z ~factor)
                     ~'t (/ ~'t ~factor)]
                 (clojure.core/* ~factor ~noise))))
          (range 6))))

(def hash-cubes (vwarp vfloor vhash))

(def vnoise (vector-offsets noise))

(def vsnoise (vector-offsets snoise))

(def vplasma (vector-offsets plasma))

(defn checker 
  ([a b]
    (vif '(clojure.core/* 
            (clojure.core/- (clisk.functions/frac x) 0.5)
            (clojure.core/- (clisk.functions/frac y) 0.5))
         a
         b)))

(defn globe
  "Creates a globe, returning the value of the function called on the surface of a unit sphere"
  ([]
    (globe z 0.0))
  ([function]
    (globe function 0.0))
  ([function background]
    (vif 
      `(- ~(length [x y]) 1.0)
      (vwarp [x y `(Math/sqrt (- 1.0 ~(dot [x y] [x y])))] function )
      background)))