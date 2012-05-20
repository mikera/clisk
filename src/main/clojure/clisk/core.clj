(ns clisk.core
  (:import clisk.Util)
  (:use clisk.functions))

(set! *warn-on-reflection* true)

(def DEFAULT-IMAGE-WIDTH 256)

(def DEFAULT-IMAGE-HEIGHT 256)







  
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
    (let [vector-function (vectorize vector-function)
          image (Util/newImage (int w) (int h))
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
