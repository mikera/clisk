(ns clisk.core
  (:import clisk.Util)
  (:import java.awt.image.BufferedImage)
  (:use clisk.node)
  (:use clisk.util)
  (:use clisk.functions))

(set! *warn-on-reflection* true)

(set! *unchecked-math* true)

(def DEFAULT-IMAGE-WIDTH 256)

(def DEFAULT-IMAGE-HEIGHT 256)

(def ^:dynamic *anti-alias* 2)
 
(defn ^clisk.IFunction compile-fn [node]
  "Compiles clisk scalar node into an object that extends clisk.Function and clojure.lang.IFn"
  (let [code (:code (clisk.node/node node))]
    (if (nil? code) (error "Nil code in node: " node))
	  (eval
	    `(reify clisk.IFunction
	       (calc 
	         [~'this ~'x ~'y ~'z ~'t]
	           (double ~code))
	       (calc
	         [~'this ~'x ~'y ~'z]
	           (.calc ~'this ~'x ~'y ~'z 0.0))
	       (calc
	         [~'this ~'x ~'y]
	           (.calc ~'this ~'x ~'y 0.0))
	       (calc
	         [~'this ~'x]
	           (.calc ~'this ~'x 0.0))
	       (calc
	         [~'this]
	           (.calc ~'this 0.0))))))

(defn sample 
  ([node pos]
    (let [pos (vectorize pos)
          code (vectorize node)
          fns (vec (map compile-fn code))
          [x y z t] (map #(component % pos) (range 4))]
      (vec 
        (map #(.calc ^clisk.IFunction % (double x) (double y) (double z) (double t))
             fns)))))

(defn img
  "Creates a BufferedImage from the given vector function."
  ([node]
    (img node DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([node w h]
    (img node w h 1.0 (/ (double h) (double w))))
  ([node w h dx dy]
    (let [node (clisk.node/node node)
          image (Util/newImage (int w) (int h))
          fr (compile-fn (:code (component 0 vector-function)))
          fg (compile-fn (:code (component 1 vector-function)))
          fb (compile-fn (:code (component 2 vector-function)))
          w (int w)
          h (int h)
          dx (double dx)
          dy (double dy)
          dw (double w)
          dh (double h)]
	    (doall (pmap 
        #(let [iy (int %)]
		      (dotimes [ix w]
		        (let [iy (int iy)
	                x (/ (* dx (+ 0.5 ix)) dw)
	                y (/ (* dy (+ 0.5 iy)) dh)
	                r (.calc fr x y 0.0 0.0)
	                g (.calc fg x y 0.0 0.0)
	                b (.calc fb x y 0.0 0.0)
	                argb (Util/toARGB r g b)]
	           (.setRGB image ix iy argb))))
        (range h)))
     image)))

(defn scale-image [img w h]
  (Util/scaleImage img (int w) (int h)))

(defn show 
  "Creates an shows an image from the given vector function"
  ([vector-function]
    (show vector-function DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([vector-function w h]
    (let [scale *anti-alias*
          fw (* w scale)
          fh (* h scale)
          img (img vector-function fw fh)
          img (loop [scale scale fw fw fh fh img img]
                (if  (> scale 1)
                  (let [factor (min 2.0 scale)
                        nw (/ fw factor)
                        nh (/ fh factor)]   
	                  (recur
	                    (/ scale factor)
	                    nw
                      nh
	                    (scale-image img nw nh)))
                  img))]

      (Util/show ^BufferedImage img))))
