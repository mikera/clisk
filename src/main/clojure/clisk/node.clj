(ns clisk.node
  (:use clisk.util))

(defrecord Node [])

(declare node)
(declare constant-node)
(declare evaluate)

;; predicates

(defn node? [x] 
  (instance? Node x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn vector-node? [x] 
  (and (node? x) (= :vector (:type x))))

(defn scalar-node? [x] 
  (and (node? x) (= :scalar (:type x))))

;; standard position vector
(def pos ['x 'y 'z 't])

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
        (if (vector-node? n)
          (value-node (map evaluate (:nodes n)))
          (value-node (evaluate n)))
		    n))))


(defn object-node 
  "Creates a node with an embedded Java object"
  ([v]
	  (let [sym (gensym "obj")]
	    (new-node 
	          {:type :scalar
	           :code sym
	           :objects {sym v}
	           :constant true}))))

(defn compile-scalar-node [n]
  "Compile a scalar node to a clisk.IFunction"
  (let [n (node n)
        obj-map (:objects n)
        syms (keys obj-map)
        objs (vals obj-map)
        code (:code n)]
    (if-not (scalar-node? n) (error "Trying to compile non-scalar node"))
    (apply
      (eval
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
			           (.calc ~'this 0.0))))))
	      objs)))

(defn evaluate 
  "Evaluates a scalar node at a given position (defaults to zero)."
  ([n] (evaluate n 0.0 0.0 0.0 0.0))
  ([n x] (evaluate n x 0.0 0.0 0.0))
  ([n x y] (evaluate n x y 0.0 0.0))
  ([n x y z] (evaluate n x y z 0.0))
  ([n x y z t]
    (let [n (node n)]
      (.calc (compile-scalar-node n) (double x) (double y) (double z) (double t)))))


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


(defn transform-node
  "Creates a node containing code that trasnforms the other scalar vectors into a new code form"
  ([f & scalars]
    (let [scalars (map node scalars)]
      (if-not (every? scalar-node? scalars) (error "Nodes to transform must be scalar"))
	    (new-node
	      {:type :scalar
	       :code (apply f (map :code scalars))
	       :objects (apply merge (map :objects scalars))
	       :constant (every? constant-node? scalars)}))))

(defn function-node
  "Creates a node which is a function of scalar nodes"
  ([f & scalars]
    (apply 
      transform-node
      (fn [& xs] `(~f ~@xs))
      scalars)))

(defn constant-node [v]
  (cond
    (vector? v)
      (let [node (vec-node v)]
        (if (not (:constant node)) (error "Not a constant vector!"))
        node) 
    :else 
      (value-node (double v))))

(defn code-node [form]
  (new-node {:type :scalar 
             :code form
             :objects (apply merge (map :objects (filter node? (flatten form))))
             :constant false}))

(defn node [a]
  "Creates a node from arbitrary input. Idempotent, can be used to force conversion to node."
  (cond 
    (node? a) a
    (number? a) (constant-node a)
    (vector? a) (vec-node a)
    (fn? a) (node (a pos))
    (symbol? a) (code-node a)
    (sequential? a) (code-node a)
    :object (object-node a)
    :else (error "Unable to build an AST node from: " a)))

(def ZERO-NODE (node 0.0))

(defn component [i n]
  "Returns a node that represents the specified component of an input node"
  (let [n (node n)]
	  (if (vector-node? n)
	    (nth (:nodes n) i ZERO-NODE)
	    n))) 

(defn dimensions 
  "Returns the number of dimensions in a vector node, or 1 if scalar"
  ([x]
	  (let [x (node x)]
	    (cond
	      (scalar-node? x)
	        1
	      (vector-node? x)
	        (count (:nodes x))))))

(defn validate [node]
  (cond
    (not (xor (:code node) (:codes node))) 
      (error "AST node must have :code or :codes")
    :else 
      node))
