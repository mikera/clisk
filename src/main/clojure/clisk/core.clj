(ns 
  ^{:author "mikera"
    :doc "Core clisk image generation functions"}
  clisk.core
  (:import clisk.Util)
  (:import java.awt.image.BufferedImage)
  (:import [mikera.gui Frames])
  (:require [clojure test])
  (:use [clisk node functions util]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(def ^:dynamic *anti-alias* 2)

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


(defn tst [] (clojure.test/run-all-tests))

(defn scale-image [img w h]
  "Scales an image to a given width and height"
  (Util/scaleImage img (int w) (int h)))

(defn show-comp 
  "Shows a component in a new frame"
  ([com 
    & {:keys [^String title]
       :as options
       :or {title nil}}]
  (let [com (component com)]
    (Frames/display com title))))

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
