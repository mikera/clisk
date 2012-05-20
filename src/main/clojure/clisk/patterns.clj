(ns clisk.patterns
  (:use clisk.functions)
  (:import clisk.Perlin))

(defn checker 
  ([a b]
    (vif '(clojure.core/* 
            (clojure.core/- (clisk.functions/frac x) 0.5)
            (clojure.core/- (clisk.functions/frac y) 0.5))
         a
         b)))

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