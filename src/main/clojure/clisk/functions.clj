(ns clisk.functions
  (:import clisk.Util))

(def identity-vector ['x 'y 'z 't])


(defn error [& vals]
  (throw (Error. (str (reduce str vals)))))

(defn vectorize [x]
  (cond
    (vector? x)
      x
    (number? x)
      (vec (repeat 4 (double x)))
    :else
      (vec (repeat 4 x))))


(defn check-dims [& vectors]
  (if (every? vector? vectors)
    (let [[v & vs] vectors
          dims (count v)]
      (if (every? #(= dims (count %)) vs)
        dims
        (error "Unequal vector sizes: " (map count vectors))))
    (error "Not all vectors: " vectors)))

(defn vectorize-op1 [f]
  (fn [v1]
    (let [v1 (vectorize v1)
         dims (check-dims v1)]
      (vec (for [i (range dims)]
           (list f (v1 i)))))))

(defn vectorize-op2 [f]
  (fn [v1 v2]
    (let [v1 (vectorize v1)
          v2 (vectorize v2)
          dims1 (check-dims v1)
          dims2 (check-dims v2)
          dims (max dims1 dims2)]
      (vec 
        (for [i (range dims)]
          (cond 
            (>= i dims1) (list f 0.0 (v2 i))
            (>= i dims2) (list f (v1 i) 0.0)
            :else (list f (v1 i) (v2 i))))))))

(defn ^:static frac ^double [^double x]
  (- x (Math/floor x)))

(def vsin
  (vectorize-op1 'Math/sin))

(def vabs
  (vectorize-op1 'Math/abs))

(def vround
  (vectorize-op1 'Math/round))

(def vfloor
  (vectorize-op1 'Math/floor))

(def v+ 
  (vectorize-op2 'clojure.core/+))

(def v* 
  (vectorize-op2 'clojure.core/*))

(def v- 
  (vectorize-op2 'clojure.core/-))

(def vdivide 
  (vectorize-op2 'clojure.core//))


(defn vwarp 
  [warp f]
  (let [wdims (check-dims warp)
        fdims (check-dims f)
        vars (take wdims ['x 'y 'z 't])
        temps (take wdims ['x-temp 'y-temp 'z-temp 't-temp])
        bindings 
          (concat
            (interleave temps warp)
            (interleave vars temps))]
    (vec
      (for [i (range fdims)]
        `(let [~@bindings] ~(f i))))))

(defn vscale [factor f] 
  (vwarp (v* factor identity-vector) f))

(defn vdistort 
  [warp f]
  (let [warp (vectorize warp)
        f (vectorize f)
        wdims (check-dims warp)]
    (vwarp (v+ 
             (vec (take wdims ['x 'y 'z 't]))
             warp)
           f)))