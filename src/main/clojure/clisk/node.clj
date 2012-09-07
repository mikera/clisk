(ns 
  ^{:author "mikera"
    :doc "Functions for managing clisk AST nodes. Should not normally be needed by library users"}  
  clisk.node
  (:import [clisk Util NodeMarker])
  (:import java.awt.image.BufferedImage)
  (:use clisk.util))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)


(declare node)
(declare img)
(declare constant-node)
(declare vector-node)
(declare vec-node)
(declare evaluate)
(declare warp)
(declare ZERO-NODE)

;; ===========================
;; Node record type

(defrecord Node []
  clojure.lang.IFn
    (invoke [this]
      this) 
    (invoke [this x]
      (warp x this))
    (applyTo [this args]
      (if-let [ss (seq args)]
        (warp (first args) (.applyTo this (next ss)))
        this))
  clisk.NodeMarker)


;; ==============================
;; Image generation constants

(def ^:const DEFAULT-IMAGE-WIDTH 256)
(def ^:const DEFAULT-IMAGE-HEIGHT 256)

;; ==============================
;; Node predicates

(defn node? [x] 
  (instance? clisk.NodeMarker x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn vector-node? [x] 
  (and (node? x) (= :vector (:type x))))

(defn scalar-node? [x] 
  (and (node? x) (= :scalar (:type x))))

(defn is-constant [value]
  (fn [n]
    (let [n (node n)]
      (and (constant-node? n)
           (= value (:code n)))))) 

;; standard position vector
(def position-symbol-vector ['x 'y 'z 't])

;; =====================================
;; basic Node functions
;; these are private but aliased in clisk.functions

(defn ^:private dimensions 
  "Returns the number of dimensions in a vector node, or 1 if scalar"
  ([a]
	  (let [a (node a)]
	    (cond
	      (scalar-node? a)
	        1
	      (vector-node? a)
	        (count (:nodes a))))))


(defn ^:private component [i n]
  "Returns a scalar node that represents the specified component of an input node. Taking any component of a scalr results in the same scalar."
  (let [n (node n)]
	  (if (vector-node? n)
	    (nth (:nodes n) i ZERO-NODE)
	    n))) 

(defn ^:private components [index-vector a]
  "Returns a subset of components from a, according to the provided indices"
  (let [a (node a)]
    (apply vector-node 
         (vec (map 
                (fn [i]
                  (component i a))
                index-vector)))))

(defn ^:private  take-components [n a]
  "Take the first n components from a vector function"
  (let [a (node a)]
    (vec-node
      (for [i (range n)]
        (component i a)))))


;; ========================================
;; Node constructors

(defn value-node [v]
  (if 
    (sequential? v)
    (Node. nil
         {:type :vector 
          :nodes (vec (map node v))
          :codes (vec (map double v))
          :constant true})
	  (Node. nil
	         {:type :scalar 
	          :code (double v)
	          :constant true})))



(defn new-node 
  "Create a new AST node with the given properties"
  ([props]
	  (let [n (Node. nil props)]
		  (if (and (:constant props) (not (number? (:code props))))
        (value-node (evaluate n))
		    n))))


(defn object-node 
  "Creates a node with an embedded Java object"
  ([v]
	  (let [sym (gensym "obj")]
	    (Node. nil
	          {:type :scalar
	           :code sym
	           :objects {sym v}
	           :constant true}))))

(defn generate-scalar-code [n]
  (let [n (node n)
        obj-map (:objects n)
        syms (keys obj-map)
        code (:code n)]
    (if-not (scalar-node? n) (error "Trying to compile non-scalar node"))
    `(fn [~@syms]
	       (let []
		       (reify clisk.IFunction
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
			           (.calc ~'this 0.0)))))))

(defn compile-scalar-node ^clisk.IFunction [n]
  "Compile a scalar node to a clisk.IFunction"
  (let [n (node n)
        obj-map (:objects n)
        objs (vals obj-map)]
    (if-not (scalar-node? n) (error "Trying to compile non-scalar node"))
    (apply
      (eval
		    (generate-scalar-code n))
	    objs)))

(defn ^:private evaluate 
  "Evaluates a node at a given position (defaults to zero). Can return either vector or scalar result."
  ([n] (evaluate n 0.0 0.0 0.0 0.0))
  ([n x] (evaluate n x 0.0 0.0 0.0))
  ([n x y] (evaluate n x y 0.0 0.0))
  ([n x y z] (evaluate n x y z 0.0))
  ([n x y z t]
    (let [n (node n)]
      (if (scalar-node? n)
        (.calc (compile-scalar-node n) (double x) (double y) (double z) (double t))
        (vec
          (map
            #(.calc (compile-scalar-node %) (double x) (double y) (double z) (double t))
            (:nodes n)))))))


(defn vec-node 
  "Creates a node from a sequence of scalar nodes"
  ([xs]
	  (let [nodes (map node xs)]
	    (cond
	      (not (every? #(= :scalar (:type %)) nodes))
	        (error "vec-node requires scalar values as input")
	      :else
	        (new-node 
	          {:type :vector
	           :nodes (vec nodes)
	           :codes (vec (map :code nodes))
	           :objects (apply merge (map :objects nodes))
	           :constant (every? constant-node? nodes)})))))

(defn vector-node [& xs] 
  (vec-node xs))


(defn constant-node 
  "Create a node that returns a constant value, can be either a constant vector or scalar value"
  ([v]
	  (cond
	    (vector? v)
	      (let [node (vec-node v)]
	        (if (not (:constant node)) (error "Not a constant vector!"))
	        node) 
	    :else 
	      (value-node (double v)))))

(defn transform-node
  "Creates a node containing code based on transforming the other nodes into a new form"
  ([f & nodes]
    (let [nodes (map node nodes)
          generated-node (node (apply f nodes))]
      (if (every? constant-node? nodes)
        (constant-node (evaluate generated-node))
        (merge
          generated-node
          {:objects (apply merge (map :objects nodes))})))))

(defn transform-components
  "Calls transform-node separately on each component of a set of nodes. Returns a scalar iff all input nodes are scalar."
  ([f & nodes]
    (let [nodes (map node nodes)]
      (if (some vector-node? nodes)
	      (let [dims (apply max (map dimensions nodes))]
		      (vec-node 
		        (for [i (range dims)]
		          (apply transform-node f (map #(component i %) nodes)))))
	      (apply transform-node f nodes)))))

(defn function-node
  "Creates a node which is a scalar function of scalar nodes. Function should be provided as a symbol."
  ([f & scalars]
    (let [scalars (map node scalars)]
      (if-not (every? scalar-node? scalars) (error "Input nodes to function-node must be scalar"))
      (if-not (symbol? f) (error "Function in function-node must be a symbol, got: " f))
      (apply 
        transform-node
        (fn [& xs] `(~f ~@(map :code xs)))
        scalars))))

(defn code-node 
    "Creates a node from a given code form (may be a vector). Does not preserve objects - must be copied over manually."
   [form
    & {:keys [objects] 
       :or {objects nil}}]
  (if (vector? form)
    (vec-node (map #(code-node % :objects objects) form))
	  (new-node {:type :scalar 
	             :code form
	             :constant false
               :objects objects
              })))

(defmacro texture-bound [v offset width max]
  `(let [tv# (double (+ (* (double ~v) ~(double width)) ~(double offset)) ) 
         max# (int (dec ~max)) ]
       (if (>= tv# max#) 
         max# 
         (if (<= tv# 0)
           (int 0)
           (int tv#)))))

(defn ^:private texture-map
  ([image]
    (let [^BufferedImage image (cond
                                 (instance? BufferedImage image) 
                                   image
                                 (string? image)
                                   (clisk.util/load-image image)
                                 :else
                                   (clisk.node/img image))]
      (texture-map image 0 0 (.getWidth image) (.getHeight image))))
  ([^BufferedImage image x y w h]
    (let [texture (object-node image)
          tsym (first (keys (:objects texture)))
          mw (.getWidth image)
          mh (.getHeight image)]
      (vec-node
        (map
          (fn [fsym]
            (assoc 
              (code-node
                `(let [image# ^java.awt.image.BufferedImage ~tsym
                       tx# (int (texture-bound ~'x ~x ~w ~mw))
                       ty# (int (texture-bound ~'y ~y ~h ~mh))]
                   (~fsym (.getRGB ^BufferedImage image# tx# ty#)) ) )
              :objects (:objects texture)) )
          [`red-from-argb `green-from-argb `blue-from-argb `alpha-from-argb])))))

(defn node [a]
  "Creates a node from arbitrary input. Idempotent, can be used to force conversion to node."
  (cond 
    (node? a) a
    (number? a) (constant-node a)
    (vector? a) (vec-node a)
    (fn? a) (node (a position-symbol-vector))
    (symbol? a) (code-node a)
    (keyword? a) (error "Can't convert keyword to node: " a)
    (sequential? a) (code-node a)
    (instance? java.awt.image.BufferedImage a) (texture-map a)
    :object (object-node a)
    :else (error "Unable to build an AST node from: " a)))

(defn ^:private vectorize 
	"Converts a value into a vector function form. If a is already a vector node, does nothing. If a is a function, apply it to the current position."
  ([a]
	  (let [a (node a)] 
	    (cond
		    (vector-node? a)
		      a
		    (scalar-node? a)
		      (vector-node a)
		    :else
		      (error "Should not be possible!"))))
  ([dims a]
    (let [a (node a)
          va (vectorize a)
          adims (dimensions va)]
      (cond
        (= adims dims) va
        (< dims adims) (node (vec (take dims (:nodes va))))
        :else (node (vec (concat (:nodes va) (repeat (- dims adims) (if (scalar-node? a) a ZERO-NODE)))))))))


(defn ^:private vlet* 
  "let one or more values within a vector function" 
  ([bindings form]
    (let [form (node form)
          binding-pairs (partition 2 bindings)
          symbols (map first binding-pairs)
          binding-nodes (map (comp node second) binding-pairs)]
      ;; (if-not (every? scalar-node? binding-nodes) (error "All binding values must be scalar"))
		  (if (seq bindings)
		    (apply transform-components
          (fn [form & binds]
            `(let [~@(interleave symbols (map :code binds))]
               ~(:code form)))
          (cons form binding-nodes))
      form))))

(defn ^:private warp 
  "Warps the position vector before calculating a vector function"
  ([new-position f]
	  (let [new-position (vectorize new-position)
	        f (node f)
	        wdims (dimensions new-position)
          zdims (- 4 wdims) 
	        fdims (dimensions f)
	        vars (take wdims ['x 'y 'z 't])
	        temps (take wdims ['x-temp 'y-temp 'z-temp 't-temp])
          zero-bindings (interleave (drop wdims ['x 'y 'z 't]) (repeat zdims 0.0)) 
	        bindings 
	          (vec (concat
                  (interleave temps (take wdims (:nodes new-position))) ;; needed so that symbols x,y,z,t aren't overwritten too early
                  (interleave vars temps)
                  zero-bindings))]
     (vlet* bindings f))))

(def ZERO-NODE (node 0.0))

 
(defn ^clisk.IFunction compile-fn [node]
  "Compiles clisk scalar node into an object that extends clisk.Function and clojure.lang.IFn"
  (clisk.node/compile-scalar-node node))

(defn img
  "Creates a BufferedImage from the given vector function."
  ([node]
    (img node DEFAULT-IMAGE-WIDTH DEFAULT-IMAGE-HEIGHT))
  ([node w h]
    (img node w h 1.0 (/ (double h) (double w))))
  ([node w h dx dy]
    (let [node (clisk.node/node node)
          image (clisk.Util/newImage (int w) (int h))
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


(defn node-info [node]
  (expression-info-internal 
     `(fn [~@(keys (:objects node))]
         (let [~'x 1.0 
               ~'y 1.0 
               ~'z 1.0 
               ~'t 1.0 ]
          ~(:code node)))))

(defn validate 
  "Validates the structure and behaviour of any node. Throws an error if any problem is deteted, returns the node otherwise."
  ([nd]
	  (let [nd (node nd)]
     (cond
	    (not (xor (:code nd) (:codes nd))) 
	      (error "AST node must have :code or :codes")
	    (and (scalar-node? nd)
           (not (:primitive? (node-info nd))))
	      (error "AST code must be of primitive type: " (:code nd) " was: [" (:type (node-info nd)) "]")
	    (vector-node? nd)
         (do 
	          (doseq [n (:nodes nd)] (validate n))
	          nd)
      :else 
	      nd))))
