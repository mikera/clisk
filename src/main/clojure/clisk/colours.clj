(ns 
  ^{:author "mikera"
    :doc "Library of colours, colour gradients and colour-handling functions."}
  clisk.colours
  (:use [clisk node functions patterns])
  (:import java.awt.Color)
  (:import mikera.util.Maths)
  (:import java.lang.Math))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)


(def JAVA-COLOURS
  '(black blue cyan darkGray gray green lightGray magenta 
    orange pink red white yellow))

(doseq [colour JAVA-COLOURS]
  (eval `(def ~colour (rgb (. Color ~colour)))))

(def purple (rgb 0.5 0.0 0.5))
(def brown (rgb 0.6 0.3 0.0))

(def sunset-map 
  (colour-map 
    [[0.0 [0.0 0.0 0.4]]
     [0.3 [0.1 0.0 0.5]]
     [0.5 [0.4 0.1 0.3]]
     [0.7 [0.8 0.3 0.1]]
     [0.9 [1.0 0.6 0.0]]
     [1.0 [1.0 0.9 0.0]]]))

(def colourful-map
  (seamless 0.2 (compose vnoise (v* pos 20))))

(def bright-colourful-map
  (seamless 0.2 (compose vnoise (v* pos 20))))

(def colour-bands
  (grain (vfloor x)))

(def desert-map 
  (colour-map 
    [[0.0 [0.6 1.0 1.0]]
     [0.3 [0.1 0.8 1.0]]
     [0.5 [0.0 0.5 1.0]]
     [0.5 [1.0 1.0 1.0]]
     [0.6 [1.0 0.8 0.6]]
     [0.8 [0.9 0.6 0.1]]
     [1.0 [0.8 0.4 0.0]]]))

(def landscape-map 
  (colour-map 
    [[0.0  [0.0 0.0 0.5]]
     [0.3  [0.0 0.0 0.8]]
     [0.5  [0.0 0.3 1.0]]
     [0.5  [0.8 1.0 0.0]]
     [0.51 [0.8 1.0 0.0]]
     [0.52 [0.3 0.9 0.0]]
     [0.53 [0.2 0.8 0.0]]
     [0.55 [0.1 0.5 0.1]]
     [0.6  [0.1 0.6 0.2]]
     [0.7  [0.5 0.4 0.3]]
     [0.8  [0.6 0.6 0.6]]
     [1.0  [1.0 1.0 1.0]]]))

(defn ^:static hue-function 
  (^double [^double r ^double g ^double b]
	  (let [M (Math/max r (Math/max g b))
	        m (Math/min r (Math/min g b))
	        C (- M m)]
	      (double (/
	        (cond
	          (== C 0.0)
	            0.0
	          (== M r)
	            (Maths/mod (/ (- g b) C) 6.0)
	          (== M g)
	            (+ (/ (- b r) C) 2.0)
	          (== M b)
	            (+ (/ (- r g) C) 4.0)
	        )
	        6.0))))) 

(defn ^:static lightness-function 
  (^double [^double r ^double g ^double b]
    (let [M (Math/max r (Math/max g b))
          m (Math/min r (Math/min g b))]
      (* 0.5 (+ M m))))) 

(defn ^:static saturation-function 
  (^double [^double r ^double g ^double b]
	  (let [M (Math/max r (Math/max g b))
	        m (Math/min r (Math/min g b))
	        C (- M m)
	        L (* 0.5 (+ M m))]
	      (if (== C 0.0)
	        0.0
	        (/ C (- 1.0 (Math/abs (- (* 2.0 L) 1.0)))))))) 

(defn hue-from-rgb [colour-vector]
  (let [r (component 0 colour-vector)
        g (component 1 colour-vector)
        b (component 2 colour-vector)]
    (function-node `hue-function r g b)))

(defn lightness-from-rgb [colour-vector]
  (let [r (component 0 colour-vector)
        g (component 1 colour-vector)
        b (component 2 colour-vector)]
    (function-node `lightness-function r g b)))

(defn saturation-from-rgb [colour-vector]
  (let [r (component 0 colour-vector)
        g (component 1 colour-vector)
        b (component 2 colour-vector)]
    (function-node `saturation-function r g b)))

(defn hsl-from-rgb
  ([rgb]
    (let-vector [rgb rgb] 
      (vec-node 
        [(hue-from-rgb rgb) (saturation-from-rgb rgb) (lightness-from-rgb rgb)]))))

(defn red-from-hsl [colour-vector]
  (let [h (component 0 colour-vector)
        s (component 1 colour-vector)
        l (component 2 colour-vector)]
    (function-node 'clisk.Util/redFromHSL h s l)))

(defn green-from-hsl [colour-vector]
  (let [h (component 0 colour-vector)
        s (component 1 colour-vector)
        l (component 2 colour-vector)]
    (function-node 'clisk.Util/greenFromHSL h s l)))

(defn blue-from-hsl [colour-vector]
  (let [h (component 0 colour-vector)
        s (component 1 colour-vector)
        l (component 2 colour-vector)]
    (function-node 'clisk.Util/blueFromHSL h s l)))

(defn rgb-from-hsl
  ([hsl]
    (let-vector [hsl hsl]
      (vec-node 
        [(red-from-hsl hsl) (green-from-hsl hsl) (blue-from-hsl hsl)]))))

(defn adjust-hue [shift source]
  (rgb-from-hsl 
    (v+ [(component 0 shift) 0 0]
        (hsl-from-rgb source)))) 

(defn adjust-hsl [shift source]
  (rgb-from-hsl 
    (v+ shift
        (hsl-from-rgb source)))) 
