(ns clisk.colours
  (:use clisk.functions)
  (:import java.awt.Color))

(def JAVA-COLOURS
  '(black blue cyan darkGray gray green lightGray magenta 
    orange pink red white yellow))

(doseq [colour JAVA-COLOURS]
  (eval `(def ~colour (rgb (. Color ~colour)))))