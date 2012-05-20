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

(def offsets-for-vectors [[-120.34 +340.21 -13.67 +56.78]
                          [+12.301 +70.261 -167.678 +34.568]
                          [+78.676 -178.678 -79.612 -80.111]
                          [-78.678 7.6789 200.567 124.099]])

(defn vector-offsets [func]
  (vec 
    (map
      (fn [offs]
        `(let [~@(interleave pos (map #(do `(clojure.core/+ ~%1 ~%2)) offs pos))] 
           ~func))
      offsets-for-vectors)))

(def vnoise (vector-offsets noise))

(def vsnoise (vector-offsets snoise))