(ns clisk.core
  (:import clisk.Util))

(set! *warn-on-reflection* true)

(def DEFAULT-IMAGE-WIDTH 256)

(def DEFAULT-IMAGE-HEIGHT 256)

(defn error [& vals]
  (throw (Error. (str (reduce str vals)))))

(defn vectorize-item [code]
  (cond 
    (number? code) (double code)
    (symbol? code) code
    (sequential? code) code
    :else (throw (Error. (str "Invalid vector item: " (type code))))))

(defn vectorize* 
  ([dims code]
	  (cond
	    (vector? code) (let [c (count code)]
	                  (if (< c dims)
	                    (vec (concat code (repeat (- dims c) 0.0)))
	                    code))
	    (sequential? code)
	      (let [[op & more] code
              more (map vectorize-item more)
	            vectorized-more (map #(vectorize* dims %) more)]
	        (vec (for [i (range dims)]
	          (cons op (map #(% i) vectorized-more)))))
	    :else 
       (vec (repeat dims (vectorize-item code))))))

(defmacro vectorize [dims code]
  (vectorize* dims code))


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
    (let [v1 v1
         dims (check-dims v1)]
      (vec (for [i (range dims)]
           (list f (v1 i)))))))

(defn vectorize-op2 [f]
  (fn [v1 v2]
    (let [v1 v1
          v2 v2
          dims (check-dims v1 v2)]
      (vec (for [i (range dims)]
           (list f (v1 i) (v2 i)))))))

(def vsin
  (vectorize-op1 'Math/sin))

(def v+ 
  (vectorize-op2 'clojure.core/+))

(def v* 
  (vectorize-op2 'clojure.core/*))

(def v- 
  (vectorize-op2 'clojure.core/-))

(def vdivide 
  (vectorize-op2 'clojure.core//))
  
(defn ^clisk.Function compile-fn [code]
  (eval
    `(proxy [clisk.Function] []
       (calc 
         ([~'x ~'y ~'z ~'t]
           (double ~code))
         ([~'x ~'y ~'z]
           (.calc ~'this ~'x ~'y ~'z 0.0))
         ([~'x ~'y]
           (.calc ~'this ~'x ~'y 0.0))
         ([~'x]
           (.calc ~'this ~'x 0.0))
         ([]
           (.calc ~'this 0.0))))))

(defn img
  ([vector-function]
    (img vector-function DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([vector-function w h]
    (img vector-function w h 1.0 (/ (double h) (double w))))
  ([vector-function w h dx dy]
    (let [image (Util/newImage (int w) (int h))
          fr (compile-fn (vector-function 0))
          fg (compile-fn (vector-function 1))
          fb (compile-fn (vector-function 2))]
	    (dotimes [iy h] 
	      (dotimes [ix w]
	        (let [x (/ (* dx ix) (double w))
                y (/ (* dy iy) (double h))
                r (fr x y)
                g (fg x y)
                b (fb x y)
                argb (Util/toARGB r g b)]
           (.setRGB image ix iy argb))))
     image)))

(defn show 
  ([vector-function]
    (show vector-function DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([vector-function w h]
    (Util/show (img vector-function w h))))
