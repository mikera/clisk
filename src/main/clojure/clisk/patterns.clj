(ns clisk.patterns
  (:use clisk.functions)
  (:import java.awt.image.BufferedImage)
  (:import clisk.Perlin))


(def perlin-noise 
  "Standard 4-dimensional scalar perlin noise in range [0..1]"
  '(clisk.Perlin/noise x y z t))

(def noise
  perlin-noise)

(def snoise
  "4-dimensional scalar perlin noise standardised with mean zero, range [-1..1]"
  '(clisk.Perlin/snoise x y z t))

(def plasma 
  "4 dimensional plasma, in range [0..1]"
  (cons 'clojure.core/+
        (map
          (fn [i]
            (let [factor (Math/pow 0.5 (inc i))]
              `(let [~'x (/ ~'x ~factor)
                     ~'y (/ ~'y ~factor)
                     ~'z (/ ~'z ~factor)
                     ~'t (/ ~'t ~factor)]
                 (clojure.core/* ~factor ~noise))))
          (range 6))))

(def hash-cubes 
    "4 dimensional randomly coloured unit hypercubes filling space"
    (vwarp vfloor vhash))

(def vnoise 
  "4 dimensional vector perlin noise in range [0..1]^4"
  (vector-offsets noise))

(def vsnoise 
  "4 dimensional vector standardised perlin noise in range [-1..1]^4"
  (vector-offsets snoise))

(def vplasma 
  "4 dimensional vector plasma in range [0..1]^4"
  (vector-offsets plasma))

(defn turbulence
  "Adds random turbulence to a pattern according to a perlin noise offset"
  ([factor func]
    (voffset (v* factor vsnoise) func)))

(defmethod clojure.core/print-dup java.awt.image.BufferedImage
  [^BufferedImage bi writer]
  (print-dup "[BufferedImage]" writer))

(defn texture-map 
  ([^BufferedImage texture]
    (texture-map texture 0 0 (.getWidth texture) (.getHeight texture)))
  ([^BufferedImage texture x y w h]
    (let [tx (atom texture)]
      (vec (for [i (range 4)]
             `(let [~'tx (long (+ ~x (* ~'x ~w)))
                    ~'ty (long (+ ~y (* ~'y ~h)))
                    ~'argb (.getRGB ^java.awt.image.BufferedImage ~texture ~'tx ~'ty)]
                ~([`(red-from-argb ~'argb)
                   `(green-from-argb ~'argb)
                   `(blue-from-argb ~'argb)
                   `(alpha-from-argb ~'argb)] i)))))))

(defn checker 
  "Checker pattern in (x,y) space, with 2*2 grid in [0..1,0..1] range"
  ([a b]
    (vif '(clojure.core/* 
            (clojure.core/- (clisk.functions/frac x) 0.5)
            (clojure.core/- (clisk.functions/frac y) 0.5))
         a
         b)))

(defn globe
  "Creates a globe, returning the value of the function called 
   on the surface of a unit sphere. 

   (globe) alone produces z values that Can be used as a hight map"
  ([]
    (globe z 0.0))
  ([function]
    (globe function 0.0))
  ([function background]
    (vif 
      (v- 1.0 (length [x y]))
      (vwarp [x y `(Math/sqrt (- 1.0 ~(:code (dot [x y] [x y]))))] function )
      background)))