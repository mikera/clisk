(ns clisk.functions
  (:import clisk.Util))

(set! *unchecked-math* true)

(def ^:const TAO (* 2.0 Math/PI))

(def ^:const COMPONENT_TO_DOUBLE (/ 1.0 255.0))

;; standard position vector
(def pos ['x 'y 'z 't])

(defn error
  "Throws a clisk error with the provided message(s)"
  ([& vals]
    (throw (clisk.CliskError. (str (reduce str vals))))))

(defn ensure-scalar [x]
  "Ensure x is a scalar value. If x is a vector, resturns the first component (index 0)."
  (cond 
    (vector? x)
      (ensure-scalar (first x))
    (fn? x)
      (x pos)
    (number? x)
      (double x)
    :else x))

(defn vectorize [x]
  "Converts a value into a vector function form. If x is already a factor, does nothing. If x is a function, apply it to the current position."
  (cond
    (vector? x)
      (vec (map ensure-scalar x))
    (number? x)
      (vec (repeat 4 (double x)))
    (fn? x)
      (vectorize (x pos))
    (nil? x)
      0.0
    :else
      (vec (repeat 4 x))))

(defn component [i a]
  "Gets the scalar component of the vector a at index i. "
  (let [a (vectorize a)]
    (if (< i (count a))
      (let [ret (a i)]
        (ensure-scalar ret))
      0.0)))

(defn components [mask a]
  "Gets a subset of components from a, where the mask vector is > 0. Other components are zeroed"
  (let [a (vectorize a)]
    (vec (map 
           (fn [m v]
             (if (> m 0.0)
               v
               0.0)) 
           mask
           a))))

(defn ^:static red-from-argb 
  (^double [^long argb]
    (* COMPONENT_TO_DOUBLE (bit-and (int 0xFF) (bit-shift-right argb 16)))))

(defn ^:static green-from-argb 
  (^double [^long argb]
    (* COMPONENT_TO_DOUBLE (bit-and (int 0xFF) (bit-shift-right argb 8)))))

(defn ^:static blue-from-argb 
  (^double [^long argb]
    (* COMPONENT_TO_DOUBLE (bit-and (int 0xFF) argb))))

(defn ^:static alpha-from-argb 
  (^double [^long argb]
    (* COMPONENT_TO_DOUBLE (bit-and (int 0xFF) (bit-shift-right argb 24)))))

(defn x [v]
  "Extracts the x component of a vector"
  (component 0 v))

(defn y [v]
  "Extracts the y component of a vector"
  (component 1 v))

(defn z [v]
  "Extracts the z component of a vector"
  (component 2 v))

(defn t [v]
  "Extracts the t component of a vector"
  (component 3 v))

(defn rgb
  "Creates an RGB colour vector"
  ([^java.awt.Color java-colour]
    (rgb (/ (.getRed java-colour) 255.0)
         (/(.getBlue java-colour) 255.0)
         (/ (.getGreen java-colour) 255.0)))
  ([r g b]
    [r g b 1.0])
  ([r g b a]
    [r g b a]))

(defn rgba
  "Creates an RGBA colour vector"
  ([^java.awt.Color java-colour]
    (rgba (/(.getRed java-colour) 255.0)
          (/(.getBlue java-colour) 255.0)
          (/(.getGreen java-colour) 255.0)
          (/(.getAlpha java-colour) 255.0)))
  ([r g b a]
    [r g b a]))


(defn check-dims [& vectors]
  "Ensure that parameters are equal sized vectors. Returns the size of the vector(s) if successful."
  (if (every? vector? vectors)
    (let [[v & vs] vectors
          dims (count v)]
      (if (every? #(= dims (count %)) vs)
        dims
        (error "Unequal vector sizes: " (map count vectors))))
    (error "Not all vectors: " vectors)))

(defn vectorize-op1 [f]
  (fn [v1]
    (let [v1 (vectorize v1)
         dims (check-dims v1)]
      (vec (for [i (range dims)]
             (list f (v1 i)))))))

(defn vectorize-op2 [f]
  (fn vectorize-op
    ([v1 v2]
	    (let [v1 (vectorize v1)
	          v2 (vectorize v2)
	          dims1 (check-dims v1)
	          dims2 (check-dims v2)
	          dims (max dims1 dims2)]
	      (vec 
	        (for [i (range dims)]
	          (cond 
	            (>= i dims1) (list f 0.0 (v2 i))
	            (>= i dims2) (list f (v1 i) 0.0)
	            :else (list f (v1 i) (v2 i)))))))
    ([v1 v2 & more]
      (if-let [more-more (next more)]
        (vectorize-op (vectorize-op v1 v2) (first more) more-more)
        (vectorize-op (vectorize-op v1 v2) (first more))))))

(defn vlet 
  "let one or more scalar values within a vector function" 
  ([bindings form]
	  (if (seq bindings)
	    (let [transformer 
               (fn [x]
	               `(let [~@bindings] 
	                  ~x))]
	      (if (vector? form)
          (vec (map transformer form))
          (transformer form)))
	    form)))

(defn vif [c a b]
  "Conditional vector function. First scalar argument is used as conditional value, > 0.0  is true."
  (let [a (vectorize a)
        b (vectorize b)
        adims (check-dims a)
        bdims (check-dims b)
        dims (max adims bdims)]
    (vec (for [i (range dims)]
           (let [av (component i a)
                 bv (component i b)
                 cv (component 0 c)]
             `(if (> 0.0 ~cv) ~av ~bv))))))

(defn ^:static frac
  "Retuns the fractional part of a number. Equivalent to Math/floor."
  (^double [^double x]
    (- x (Math/floor x))))

(defn ^:static phash 
  "Returns a hashed double value in the range [0..1)"
  (^double [^double x]
    (Util/dhash x))
  (^double [^double x ^double y]
    (Util/dhash x y))
  (^double [^double x ^double y ^double z]
    (Util/dhash x y z))
  (^double [^double x ^double y ^double z ^double t]
    (Util/dhash x y z t)))

(def vsin
  (vectorize-op1 'Math/sin))

(def vabs
  (vectorize-op1 'Math/abs))

(def vround
  (vectorize-op1 'Math/round))

(def vfloor
  (vectorize-op1 'Math/floor))

(def vfrac
  (vectorize-op1 'clisk.functions/frac))

(def v+ 
  "Adds two or more vectors"
  (vectorize-op2 'clojure.core/+))

(def v* 
  "Multiplies two or more vectors"
  (vectorize-op2 'clojure.core/*))

(def v- 
  "Subtracts two or more vectors"
  (vectorize-op2 'clojure.core/-))

(def vdivide 
  "Divides two or more vectors"
  (vectorize-op2 'clojure.core//))

(defn dot 
	"Returns the dot product of two vectors"
  ([a b]
	  (let [a (vectorize a)
	        b (vectorize b)
	        adims (check-dims a)
	        bdims (check-dims b)
	        dims (min adims bdims)]
	    (cons 'clojure.core/+
	      (for [i (range dims)]
	        `(clojure.core/* ~(component i a) ~(component i b)))))))

(defn vcross3
  "Returns the cross product of 2 3D vectors"
  ([a b]
    (let [[x1 y1 z1] (vectorize a)
          [x2 y2 z2] (vectorize b)]
	    [`(- (* ~y1 ~z2) (* ~z1 ~y2))
	     `(- (* ~z1 ~x2) (* ~x1 ~z1))
	     `(- (* ~x1 ~y2) (* ~y1 ~x1))])))

(defn max-component 
  "Returns the max component of a vector"
  ([v]
    (if (vector? v)
      `(max ~@v)
      v)))

(defn min-component 
  "Returns the min component of a vector"
  ([v]
    (if (vector? v)
      `(min ~@v)
      v)))

(defn length [a]
  (let [a (vectorize a)
        syms (vec (map (fn [_] (gensym "temp")) a))] 
    (vlet (vec (interleave syms a))
       `(Math/sqrt ~(dot syms syms)))))

(defn vnormalize [a]
  (let [a (vectorize a)
        syms (vec (map (fn [_] (gensym "temp")) a))]
    (vlet (vec (interleave syms a))
          (vdivide syms `(Math/sqrt ~(dot syms syms))))))

(defn vwarp 
  [warp f]
  (let [warp (vectorize warp)
        f (vectorize f)
        wdims (check-dims warp)
        fdims (check-dims f)
        vars (take wdims ['x 'y 'z 't])
        temps (take wdims ['x-temp 'y-temp 'z-temp 't-temp])
        bindings 
          (concat
            (interleave temps warp)
            (interleave vars temps))]
    (vec
      (for [i (range fdims)]
        `(let [~@bindings] ~(f i))))))

(defn vscale [factor f] 
  (let [factor (vectorize factor)
        f (vectorize f)]
    (vwarp (vdivide pos factor) f)))

(defn voffset 
  [warp f]
    (vwarp (v+ 
             pos
             warp)
           f))

(def offsets-for-vectors [[-120.34 +340.21 -13.67 +56.78]
                          [+12.301 +70.261 -167.678 +34.568]
                          [+78.676 -178.678 -79.612 -80.111]
                          [-78.678 7.6789 200.567 124.099]])

(defn vector-offsets [func]
  "Creates a vector version of a scalar function, where the components are offset versions of the original scalar function"
  (vec 
    (map
      (fn [offs]
        `(let [~@(interleave pos (map #(do `(clojure.core/+ ~%1 ~%2)) offs pos))] 
           ~func))
      offsets-for-vectors)))

(defn vgradient 
  "Computes the gradient of a scalar function f with respect to [x y z t]"
	([f]
	  (let [epsilon 0.000001
	        f (component 0 f)]
	    (vec 
	      (map 
	        (fn [sym] 
	          `(clojure.core// 
	             (clojure.core/-
	               (let [~sym (clojure.core/+ ~epsilon ~sym)]
	                 ~f)
	               ~f)
	             ~epsilon))
	        pos)))))

(defn lerp 
  "Performs clamped linear interpolation between two values, according to the proportion given in the 3rd parameter."
  ([a b v]
  `(let [a# ~a
         b# ~b
         v# ~v]
     (if (<= v# 0) a#
       (if (>= v# 1) b#
         (+ 
           (* v# b#)
           (* (- 1.0 v#) a#)))))))

(defn vlerp 
  "Performs clamped linear interpolation between two vectors, according to the proportion given in the 3rd parameter."
  ([a b]
    (fn [v] (vlerp a b v)))
  ([a b v]
	  (let [a (vectorize a)
	        b (vectorize b)
	        v (component 0 v)
	        dims (max (count a) (count b))
	        vsym (gensym "val")]
	    (vec (for [i (range dims)]
			  `(let [~vsym ~v]
			     (if (<= ~vsym 0) ~(component i a)
			       (if (>= ~vsym 1) ~(component i b)
			         (+ (* ~vsym ~(component i b)) (* (- 1.0 ~vsym) ~(component i a)))))))))))

(defn colour-map 
  ([mapping v]
    ((colour-map mapping) v))
  ([mapping]
	  (fn [x]
		  (let [vals (vec mapping)
		        v (component 0 x)
		        c (count vals)]
		    (cond 
		      (<= c 0) (error "No colour map available!")
		      (== c 1) (vectorize (second (vals 0)))
		      (== c 2) 
		        (let [lo (first (vals 0))
		              hi (first (vals 1))]
                (if (<= hi lo)
                  (vectorize (second (vals 0)))
			            (vlerp  
			              (vectorize (second (vals 0))) 
			              (vectorize (second (vals 1)))
	                  `(/ (- ~v ~lo) ~(- hi lo)))))
		      :else
		        (let [mid (quot c 2)
		              mv (first (vals mid))
		              vsym (gensym "val")
		              upper (colour-map (subvec vals mid c))
		              lower (colour-map (subvec vals 0 (inc mid)))]
		          (vec (for [i (range 4)]
		          `(let [~vsym ~v] 
		             (if (<= ~vsym ~mv)
		               ~(component i (lower vsym))
		               ~(component i (upper vsym))))))))))))

(def scalar-hash-function
  "Hash function producing a scalar value in the range [0..1) for every 
   unique point in space"
  `(phash ~'x ~'y ~'z ~'t))

(def vhash
  "Hash function producing a vector value 
   in the range [0..1)^4 for every 
   unique point in space"
  (vector-offsets scalar-hash-function))

(def vmin
  "Computes the maximum of two vectors"
  (vectorize-op2 'Math/min))

(def vmax
  "Computes the maximum of two vectors"
  (vectorize-op2 'Math/max))

(defn vclamp [v low high]
  "Clamps a vector between a low and high vector. Typically used to limit 
   a vector to a range e.g. (vclamp something [0 0 0] [1 1 1])."
  (let [v (vectorize v)
        low (vectorize low)
        high (vectorize high)]
    (vmax low (vmin high v))))


(defn viewport 
  "Rescales the texture as if viwed from [ax, ay] to [bx ,by]"
  ([a b function]
    (let [[x1 y1] a
          [x2 y2] b
          w (- x2 x1)
          h (- y2 y1)]
      (vscale [(/ 1.0 w) (/ 1.0 h) 1 1] (voffset [x1 y1] function)))))



(defn vseamless 
  "Creates a seamless 2D tileable version of a 4D texture in the [0 0] to [1 1] region. The scale argument detrmines the amount of the source texture to be used per repeat."
  ([scale v4]
    (let [v4 (vectorize v4)
          scale (/ 1.0 (component 0 scale) TAO)
          dims (check-dims v4)]
      (if (< dims 4) (error "vseamless requires 4D input texture, found " dims))
      (vwarp
        [`(* (Math/cos (* ~'x TAO)) ~scale) 
         `(* (Math/sin (* ~'x TAO)) ~scale) 
         `(* (Math/cos (* ~'y TAO)) ~scale)
         `(* (Math/sin (* ~'y TAO)) ~scale)]
        v4))))

(defn height 
  "Calculates the height value (z) of a source function"
  ([f] 
    (z f)))

(defn height-normal 
  "Calculates a vector normal to the surface defined by the z-value of a source vector or a scalar height value. The result is *not* normalised."
  ([heightmap]
    (v- [0 0 1] (components [1 1 0] (vgradient (z heightmap)))))
  ([scale heightmap]
    (v- [0 0 1] (components [1 1 0] (vgradient `(* ~scale ~(z heightmap)))))))


(defn light-value 
  "Calculates diffuse light intensity given a light direction and a surface normal vector. 
   This function performs its own normalisation, so neither the light vector nor the normal vector need to be normalised."
  ([light-direction normal-direction]
	  `(max 0.0 
	        ~(dot (vnormalize light-direction) (vnormalize normal-direction)))))

(defn diffuse-light 
  "Calculate the diffuse light on a surface normal vector.
   This function performs its own normalisation, so neither the light vector nor the normal vector need to be normalised."
  ([light-colour light-direction normal-direction]
    (v* light-colour (light-value light-direction normal-direction))))
