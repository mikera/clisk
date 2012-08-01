(ns 
  ^{:author "mikera"
    :doc "Core clisk image generation functions"}
  clisk.core
  (:import clisk.Util)
  (:import java.awt.image.BufferedImage)
  (:use [clisk node functions util]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(def DEFAULT-IMAGE-WIDTH 256)

(def DEFAULT-IMAGE-HEIGHT 256)

(def ^:dynamic *anti-alias* 2)
 
(defn ^clisk.IFunction compile-fn [node]
  "Compiles clisk scalar node into an object that extends clisk.Function and clojure.lang.IFn"
  (clisk.node/compile-scalar-node node))

(defn sample 
  "Samples the value of a node at a given position"
  ([node] (sample node [0.0 0.0]))
  ([node pos]
    (let [pos (vectorize pos)
          node (vectorize node)
          fns (vec (map compile-fn (:nodes node)))
          [x y z t] (map #(evaluate (component % pos)) (range 4))]
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
          fr (compile-fn (component 0 node))
          fg (compile-fn (component 1 node))
          fb (compile-fn (component 2 node))
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
  "Scales an image to a given width and height"
  (Util/scaleImage img (int w) (int h)))

(defn show 
  "Creates an shows an image from the given vector function"
  ([vector-function]
    (show vector-function DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([vector-function w h]
    (let [vector-function (validate (node vector-function))
          scale *anti-alias*
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
