(ns 
  ^{:author "mikera"
    :doc "Functions for managing clisk AST nodes. Should not normally be needed by library users"}  
  clisk.node
  (:require [mikera.vectorz.core :as vec])
  (:import [clisk Util NodeMarker])
  (:import java.awt.image.BufferedImage)
  (:import clisk.IRenderFunction)
  (:use clisk.util))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

;; ==============================
;; Image generation constants

(def ^:const DEFAULT-IMAGE-SIZE 256)

(declare node)
(declare img)
(declare constant-node)
(declare vector-node)
(declare scalar-node?)
(declare vec-node)
(declare code-node)
(declare evaluate)
(declare warp)
(declare ZERO-NODE)
(declare node-info)

;; ===========================
;; Node protocols 

(defprotocol PCodeGen
  (gen-code [node input-syms output-syms inner-code]
            "Returns generated code wrapping inner-code
              
             input-syms are the symbokls provided
             output-syms are the symbols required to be bound for inner-code, as a vector
             argument inner-code is the code that should be inserted as the core of the generated code")
  
  (gen-component [node input-syms index]
            "Generates code to produce one component of the node as specified by index.")
  ) 

(defprotocol PNodeShape
  (node-shape [node]
    "Returns the length of the node output vector, or nil if scalar"))

(defprotocol PValidate
  (validate [node]
    "Returns truthy if node is valid, throws an exception otherwise"))

(defprotocol PNodeComponent
  (component [node i]
    "Gets the i'th component of a node, as a new scalar node"))

;; =======================================
;; Default protocol implementations

(extend-protocol PNodeComponent
  Object 
    (component [this i]
      (let [nd (node this)]
        ;; (when (identical? nd this)) (error "Get-component problem with: " this)
        (component nd i)))
    
  clojure.lang.IPersistentVector
    (component [this i]
      (node (nth this i 0.0))))

;; =======================================
;; Code generation utility functions

(defn map-symbols
  "Returns a sequence of bindings maping old symbols to new symbols"
  ([new-syms old-syms]
    (let [pairs (map vector new-syms old-syms)]
      (mapcat (fn [[a b :as pair]] (if (= a b) nil pair)) pairs))))

(defn get-code 
  "Gets the generated code for a scalar node, assuming [x y z t] as input symbols"
  ([node]
    (when-not (scalar-node? node) (error "get-code requires a scalar node"))
    (gen-code node '[x y z t] '[x] 'x))) 

(defn get-codes 
  "Gets the generated codes for a vector node, assuming [x y z t] as input symbols"
  ([node]
    (when (scalar-node? node) (error "get-codes requires a vector node"))
    (let [dims (node-shape node)]
      (vec (for [i (range dims)]
            (gen-component node '[x y z t] i)))))) 

(defn gen-let-bindings 
  "Generates a set of let bindings around the given code, if necessary."
  ([bindings code]
    (let [pairs (partition 2 bindings)
          bindings (apply concat (filter (fn [[a b]] (not= a b)) pairs))]
      (cond 
        (empty? bindings) 
          code
        (and (== 2 (count bindings)) (= code (first bindings)))
          (second bindings)
        :else
          `(let [~@bindings] ~code))))) 

;; =======================================
;; Node record type implementing pure code
;;
;; Code should use the symbols '[x y z t], which will be bound for the execution of the code
;;
;; A scalar node is a node that returns a scalar value. all other nodes return a vector.

(defrecord CodeNode []
  clojure.lang.IFn
    (invoke [this]
      this) 
    (invoke [this x]
      (warp x this))
    (applyTo [this args]
      (if-let [ss (seq args)]
        (warp (first args) (.applyTo this (next ss)))
        this))
  
  clisk.NodeMarker
  
  PNodeShape
    (node-shape [node]
      (if (= :scalar (:type node))
        nil
        (count (:codes node))))
    
  PValidate
    (validate  [nd]
      (let [nd (node nd)]
        (cond
          (not (xor (:code nd) (:codes nd))) 
            (error "AST code node must have :code or :codes")
          (and (scalar-node? nd)
               (not (:primitive? (node-info nd))))
            (error "AST scalar code node must be of primitive type: " (:code nd) " was: [" (:type (node-info nd)) "]")
          :else 
	          nd)))
    
  PNodeComponent
    (component [node i]
      (if (scalar-node? node)
        node
        (let [my-code (nth (:codes node) i 0.0) ]
          (when-not my-code (error "No code for: " (pr-str node)))
          (code-node my-code :objects (:objects node)))))
  
  PCodeGen
    ;; note that code is assumed to use '[x y z t]
    (gen-code [node input-syms output-syms inner-code]
      (let [scalarnode? (scalar-node? node)]
        (if scalarnode?
          (let [tsym (first output-syms)
                input-bindings (map-symbols '[x y z t] input-syms)
                output-bindings (map-symbols (next output-syms) (repeat tsym)) ;; all inner symbols are set to scalar value
                bindings (concat input-bindings [tsym (:code node)] output-bindings)
                ] 
            (gen-let-bindings bindings inner-code))
          (let [codes (:codes node)
                input-bindings (map-symbols '[x y z t] input-syms) ;; bind inputs
                gsyms (mapv gensym output-syms)     ;; generate temp syms for outputs
                gcode (mapcat vector gsyms (concat codes (repeat 0.0)))   ;; map result of code to each temp symbol
                output-bindings (map-symbols output-syms gsyms)
                bindings (concat input-bindings gcode output-bindings)]
            (gen-let-bindings bindings inner-code)))))
    
    (gen-component [node input-syms index]
      (let [scalarnode? (scalar-node? node)
            code (if scalarnode? (:code node) (nth (:codes node) index 0.0))
            input-bindings (map-symbols '[x y z t] input-syms)]
        (gen-let-bindings input-bindings code)))
    )

;; =======================================
;; Node implementing a warp : g(f(x))

(defrecord WarpNode [f g]
  clojure.lang.IFn
    (invoke [this]
      this) 
    (invoke [this x]
      (warp x this))
    (applyTo [this args]
      (if-let [ss (seq args)]
        (warp (first args) (.applyTo this (next ss)))
        this))
  
  clisk.NodeMarker
  
  PNodeShape
    (node-shape [node]
      (node-shape g))
    
  PValidate
    (validate [nd]
      (validate f)
      (validate g)
      nd)
    
  PNodeComponent
    (component [node i]
      (warp f (component g i)))
  
  PCodeGen
    (gen-code [node input-syms output-syms inner-code]
      (gen-code f input-syms '[x y z t]
                (gen-code g '[x y z t] output-syms inner-code)))
    
    (gen-component [node input-syms index]
      (gen-code f input-syms '[x y z t]
                (gen-component g '[x y z t] index))))

;; ==============================
;; Node predicates

(defn node? [x] 
  (instance? clisk.NodeMarker x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn vector-node? [x] 
  (boolean (node-shape x)))

(defn scalar-node? [x] 
  (nil? (node-shape x)))

(defn is-constant [value]
  (fn [n]
    (let [n (node n)]
      (and (constant-node? n)
           (== (double value) (double (eval (get-code n)))))))) 

(defn constant-form? 
  "Returns true if a form is constant, i.e. contains none of the symbols x, y, z or t"
  ([form] 
    (cond 
      (symbol? form) false
      (vector? form) (every? constant-form? form)
      (sequential? form) (every? constant-form? (next form)) ;; ignore initial operator
      (number? form) true
      :else (error "Unexpected element of form: " (pr-str form)))))

;; standard position vector
(def position-symbol-vector ['x 'y 'z 't])

;; =====================================
;; basic Node functions
;; these are private but aliased in clisk.functions

(defn dimensions 
  "Returns the number of dimensions in a vector node, or 1 if scalar"
  (^long [a]
	  (let [a (node a)]
	    (or (node-shape a) 1))))

(defn components 
    "Gets the components of a node, as a sequence of scalar nodes"
    ([a]
      (let [a (node a)]
        (mapv 
          (fn [i]
            (component a i))
          (range (dimensions a))))))

(defn select-components 
    "Selects a subset of components from a node, returning a new vector node"
    ([a index-vector]
      (let [a (node a)]
        (vec-node 
          (mapv 
            (fn [i]
              (component a i))
            index-vector)))))

(defn ^:private  take-components [n a]
  "Take the first n components from a vector function"
  (let [a (node a)]
    (vec-node
      (for [i (range n)]
        (component a i)))))


;; ========================================
;; Node constructors

(defn value-node 
  "Create a node that represents a constant value"
  ([v]
    (if 
      (sequential? v)
      (CodeNode. nil
           {:type :vector 
            :codes (mapv double v)
            :constant true})
	    (CodeNode. nil
	           {:type :scalar 
	            :code (double v)
	            :constant true}))))


(defn new-node 
  "Create a new AST node with the given properties"
  ([props]
	  (let [n (CodeNode. nil props)]
		  (if (and (:constant props) (not (number? (:code props))))
        (value-node (evaluate n))
		    n))))

(defn object-node 
  "Creates a node with an embedded Java object"
  ([v]
	  (let [sym (gensym "obj")]
	    (CodeNode. nil
	          {:type :scalar
	           :code sym
	           :objects {sym v}
	           :constant true}))))

(defn vec-node 
  "Creates a node from a sequence of scalars. The new node returns each scalar value as a separate component."
  ([xs]
	  (let [nodes (map node xs)]
      (when-not (every? scalar-node? nodes)
        (error "vec-node requires scalar values as input"))
      (new-node 
	      {:type :vector
	       :codes (vec (map get-code nodes))
	       :objects (apply merge (map :objects nodes))
	       :constant (every? constant-node? nodes)}))))

(defn vector-node 
  "Creates a vector node from the given scalars"
  ([& xs] 
    (vec-node xs)))

(defn generate-scalar-code 
  "Creates code that generates a (fn [objects]) which returns a scalar clisk.IFunction"
  ([n]
    (when-not (scalar-node? n) (error "Trying to compile non-scalar node"))
    (let [n (node n)
          obj-map (:objects n)
          obj-syms (keys obj-map)
          code (gen-code n '[x y z t] ['x] 'x)]
      `(fn [~@obj-syms]
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
			             (.calc ~'this 0.0))))))))

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
        (mapv
          #(.calc (compile-scalar-node %) (double x) (double y) (double z) (double t))
          (components n))))))



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
		          (apply transform-node f (map #(component % i) nodes)))))
	      (apply transform-node f nodes)))))

(defn function-node
  "Creates a node which is a scalar function of scalar nodes. Function should be provided as a symbol."
  ([f & scalars]
    (let [scalars (map node scalars)]
      (if-let [nd (first (filter (complement scalar-node?) scalars))] (error "Input nodes to function-node must be scalar, got: " (pr-str nd)))
      (if-not (symbol? f) (error "Function in function-node must be a symbol, got: " f))
      (apply 
        transform-node
        (fn [& xs] `(~f ~@(map get-code xs)))
        scalars))))

(defn code-node 
    "Creates a node from a given code form (may be a vector). Does not preserve objects - must be copied over manually."
   [form
    & {:keys [objects] 
       :or {objects nil}}]
  (if (vector? form)
    (vec-node (map #(code-node % :objects objects) form))
	  (if (constant-form? form)
      (new-node {:type :scalar 
	               :code (eval form) 
	               :constant true
                 :objects objects
                })
      (new-node {:type :scalar 
	               :code form
	               :constant false
                 :objects objects
                }))))

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
        (mapv
          (fn [fsym]
            (assoc 
              (code-node
                `(let [image# ^java.awt.image.BufferedImage ~tsym
                       tx# (int (texture-bound ~'x ~x ~w ~mw))
                       ty# (int (texture-bound ~'y ~y ~h ~mh))]
                   (~fsym (.getRGB ^BufferedImage image# tx# ty#)) ) )
              :objects (:objects texture)) )
          [`red-from-argb `green-from-argb `blue-from-argb `alpha-from-argb])))))

(defn vector-function-node 
  "Creates a vector node from a VectorFunction"
  [^mikera.transformz.ATransform vf]
  (let [input-dims (.inputDimensions vf)
        output-dims (.outputDimensions vf)]
    (error "Not yet implemented")))

(defn node [a]
  "Creates a node from arbitrary input. Idempotent, can be used to force conversion to node."
  (cond 
    (node? a) a
    (number? a) (constant-node a)
    (vector? a) (vec-node a)
    (vec/vec? a) (vec-node (seq a))
    (fn? a) (node (a position-symbol-vector))
    (symbol? a) (code-node a)
    (keyword? a) (error "Can't convert keyword to node: " a)
    (sequential? a) (code-node a)
    (instance? java.awt.image.BufferedImage a) (texture-map a)
    (instance? mikera.transformz.ATransform a) (vector-function-node a)
    :object (object-node a)
    :else (error "Unable to build an AST node from: " a)))

(defn ^:private vectorize 
	"Converts a value into a vector function form. If a is already a vector node, does nothing. If a is a function, apply it to the current position.

   If dims are supplied, vectorizes to the given number of dimensions. This duplicates scalars and zero-extends vectors."
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
    (select-components a (range dims))))


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
          (fn [nd & binds]
            `(let [~@(interleave symbols (map get-code binds))]
               ~(get-code nd)))
          (cons form binding-nodes))
      form))))

(defn ^:private warp 
  "Warps the position vector before calculating a vector function"
  ([new-position f]
	  (let [new-position (node new-position)
         f (node f)]
     (WarpNode. (node new-position) (node f) nil {:objects (merge (:objects new-position) (:objects f))}))))

(def ZERO-NODE (node 0.0))

 
(defn ^clisk.IFunction compile-fn [node]
  "Compiles clisk scalar node into an object that extends clisk.Function and clojure.lang.IFn"
  (clisk.node/compile-scalar-node node))

(defn ^clisk.IRenderFunction compile-render-fn [node]
  "Compiles clisk node into an object that implements clisk.IRenderFunction"
  (let [node (vectorize 4 node) ;; we want 4 channel output
        obj-map (:objects node)
        osyms (keys obj-map)
        code (gen-code node '[x y z t] '[x y z] `(Util/toARGB ~'x ~'y ~'z)) ;; rendering only requires x, y, z
        ]
    (apply (eval
           `(fn [~@osyms]
              (reify clisk.IRenderFunction
                (^int calc [this ^double x ^double y]
                  (let [~'z 0.0 ~'t 0.0] 
                    ~code)))))
           (vals obj-map))))

(defn img
  "Creates a BufferedImage from the given node."
  (^BufferedImage [node]
    (img node DEFAULT-IMAGE-SIZE DEFAULT-IMAGE-SIZE))
  (^BufferedImage [node w h]
    (img node w h 1.0 (/ (double h) (double w))))
  (^BufferedImage [node w h dx dy]
    (let [node (clisk.node/node node)
          image (clisk.Util/newImage (int w) (int h))
          rf (compile-render-fn node)
          w (int w)
          h (int h)
          dx (double dx)
          dy (double dy)
          dw (double w)
          dh (double h)
          gen-row! (fn [rownum] 
                     (let [iy (int rownum)]
                       (dotimes [ix w]
                         (let [iy (int iy)
                               x (/ (* dx (+ 0.5 ix)) dw)
                               y (/ (* dy (+ 0.5 iy)) dh)
                               argb (.calc rf x y)]
                           (.setRGB image ix iy argb)))))]
	    (doall (pmap gen-row! (range h)))
     image)))


(defn node-info [node]
  (expression-info-internal 
     `(fn [~@(keys (:objects node))]
         (let [~'x 1.0 
               ~'y 1.0 
               ~'z 1.0 
               ~'t 1.0 ]
          ~(gen-code node '[x y z t] '[x] 'x)))))
