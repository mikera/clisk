(ns clisk.colours
  (:use clisk.functions)
  (:import java.awt.Color))

(def JAVA-COLOURS
  '(black blue cyan darkGray gray green lightGray magenta 
    orange pink red white yellow))

(doseq [colour JAVA-COLOURS]
  (eval `(def ~colour (rgb (. Color ~colour)))))


(def sunset-map 
  (colour-map 
    [[0.0 [0.0 0.0 0.4]]
     [0.3 [0.1 0.0 0.5]]
     [0.5 [0.4 0.1 0.3]]
     [0.7 [0.8 0.3 0.1]]
     [0.9 [1.0 0.6 0.0]]
     [1.0 [1.0 0.9 0.0]]]))

(def landscape-map 
  (colour-map 
    [[0.0 [0.0 0.0 1.0]]
     [0.0 [0.1 0.0 0.5]]
     [0.5 [0.4 0.1 0.3]]
     [0.7 [0.8 0.3 0.1]]
     [0.9 [1.0 0.6 0.0]]
     [1.0 [1.0 0.9 0.0]]]))