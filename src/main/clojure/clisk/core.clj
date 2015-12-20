(ns 
  ^{:author "mikera"
    :doc "Core clisk image generation functions"}
  clisk.core
  (:import clisk.Util)
  (:import [java.awt.image BufferedImage])
  (:import [mikera.gui Frames])
  (:import [javax.swing JComponent])
  (:use [mikera.cljutils core])
  (:require [clojure test])
  (:require [mikera.image.core :as imagez])
  (:use [clisk node functions util]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(def ^:dynamic *anti-alias* 2)

(defn sample 
  "Samples the value of a node at a given position"
  ([node] (sample node [0.0 0.0]))
  ([node pos]
    (let [pos (vectorize pos)
          node (vectorize node)
          fns (vec (map compile-fn (:nodes node)))
          [x y z t] (map #(evaluate (component % pos)) (range 4))]
      (mapv #(.calc ^clisk.IFunction % (double x) (double y) (double z) (double t))
           fns))))

(defn sampler 
  "Creates a sampler function for a given node, which is a fn from position to sample value"
  ([node] 
    (let [node (vectorize node)
          fns (mapv compile-fn (:nodes node))]
      (fn [[x y z t]]
        (let [x (double (or x 0.0))
              y (double (or y 0.0))
              z (double (or z 0.0))
              t (double (or t 0.0))]
          (mapv #(.calc ^clisk.IFunction % x y z t)
            fns))))))

(defn tst [] (clojure.test/run-all-tests))

(defn scale-image 
  "Scales an image to a given width and height"
  (^BufferedImage [^BufferedImage img w h]
    (imagez/scale-image img w h)))

(defn show-comp 
  "Shows a component in a new frame"
  ([com 
    & {:keys [^String title]
       :as options
       :or {title nil}}]
  (let [^JComponent  com (component com)]
    (Frames/display com title))))

(defn vector-function 
  "Defines a vector function, operating on vectorz vectors"
  (^clisk.VectorFunction [a 
                & {:keys [input-dimensions]}]
    (let [a (vectorize a)
          input-dimensions (int (or input-dimensions 4))
          ^java.util.List funcs (mapv compile-fn (:nodes a))]
      (clisk.VectorFunction/create input-dimensions funcs))))

(defn image
  "Creates a bufferedimage from the given clisk data"
  (^BufferedImage [vector-function
                   & {:keys [width height size anti-alias] 
       :or {size DEFAULT-IMAGE-SIZE}}]
    (let [vector-function (validate (node vector-function))
          scale (double (or anti-alias *anti-alias*))
          w (int (or width size))
          h (int (or height size))
          fw (* w scale)
          fh (* h scale)
          img (img vector-function fw fh)]
      (scale-image img w h))))

(defn show 
  "Creates and shows an image from the given image vector function"
  ([image-or-function
    & {:keys [width height size anti-alias] 
       :or {size DEFAULT-IMAGE-SIZE}
       :as ks}]
    (let [^BufferedImage buf-img (if (instance? BufferedImage image-or-function)
                                   image-or-function
                                   (apply image image-or-function (mapcat identity ks)))]
      (Util/show buf-img))))
