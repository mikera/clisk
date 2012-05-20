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
          fb (compile-fn (vector-function 2))
          w (int w)
          h (int h)
          dx (double dx)
          dy (double dy)
          dw (double w)
          dh (double h)]
	    (dotimes [iy h] 
	      (dotimes [ix w]
	        (let [x (/ (* dx (+ 0.5 ix)) dw)
                y (/ (* dy (+ 0.5 iy)) dh)
                r (.calc fr x y 0.0 0.0)
                g (.calc fg x y 0.0 0.0)
                b (.calc fb x y 0.0 0.0)
                argb (Util/toARGB r g b)]
           (.setRGB image ix iy argb))))
     image)))

(defn show 
  ([vector-function]
    (show vector-function DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([vector-function w h]
    (Util/show (img vector-function w h))))
