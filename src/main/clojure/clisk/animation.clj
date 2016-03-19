(ns clisk.animation
  "Namespace for animation generation utilities"
  (:use [clisk core])
  (:use [clisk util])
  (:import [clisk Util])
  (:require [telegenic.core :as telegenic]))

(def DEFAULT-OPTIONS
  {:filename "out.mp4" 
   :width 854 
   :height 480})

(defmacro render-animation
  "Macro to render a sequence of frames, looping with a symbolic frame counter."
  ([[key frame-count] src]
    `(render-animation [~key ~frame-count] ~src))
  ([[key frame-count] src options]
    (when-not (symbol? key) (error "render-animation needs a symbol binding for the frame number"))
    (when-not (integer? frame-count) (error "render-animation needs a n integer number ofo frames"))
    `(let [opts# (mapcat identity (merge DEFAULT-OPTIONS ~options))]
       (telegenic/encode                       ;; call the telegenic encoder
         (for [~key (range ~frame-count)]      ;; loop over all frames, lazily
           (let [im# (apply image ~src opts#)] ;; create the frame
             ;; (println im#)
             (show im#)                        ;; show the latest frame
             im#))))))                         ;; return the frame for the encoder

