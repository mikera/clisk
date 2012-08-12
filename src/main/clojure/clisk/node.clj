(ns 
  ^{:author "mikera"
    :doc "Functions for managing clisk AST nodes. Should not normally be needed by library users"}  
  clisk.node
  (:import clisk.Util)
  (:use clisk.util))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(defrecord Node [])

(declare node)
(declare constant-node)
(declare evaluate)
(declare ZERO-NODE)

;; ==============================
;; Image generation constants

(def ^:const DEFAULT-IMAGE-WIDTH 256)
(def ^:const DEFAULT-IMAGE-HEIGHT 256)

;; ==============================
;; Node predicates

(defn node? [x] 
  (instance? Node x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn vector-node? [x] 
  (and (node? x) (= :vector (:type x))))

(defn scalar-node? [x] 
  (and (node? x) (= :scalar (:type x))))

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
    :object (object-node a)
    :else (error "Unable to build an AST node from: " a)))

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


(defn validate 
  "Validates the structure and behaviour of any node. Throws an error if any problem is deteted, returns the node otherwise."
  ([node]
	  (cond
	    (not (xor (:code node) (:codes node))) 
	      (error "AST node must have :code or :codes")
	    (and (scalar-node? node) 
	         (not (:primitive? (expression-info-internal 
	                             `(fn [~@(keys (:objects node))]
                                   (let [~'x 1.0 
                                      ~'y 1.0 
                                      ~'z 1.0 
                                      ~'t 1.0 ]
	                                  ~(:code node)))))))
	      (error "AST code must be of primitive type: " (:code node))
	    :else 
	      (if (vector-node? node)
	        (do 
	          (doseq [n (:nodes node)] (validate n))
	          node)
	        node))))
