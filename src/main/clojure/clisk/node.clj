(ns clisk.node
  (:use clisk.util))

(defrecord Node [])

(declare node)

;; standard position vector
(def pos ['x 'y 'z 't])

(defn new-node [props]
  (Node. nil props))

(defn node? [x] 
  (instance? Node x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn vector-node? [x] 
  (and (node? x) (= :vector (:type x))))

(defn scalar-node? [x] 
  (and (node? x) (= :scalar (:type x))))

(defn vec-node [xs]
  (let [nodes (map node xs)]
    (cond
      (not (every? #(= :scalar (:type %)) nodes))
        (error "vec-node requires scalar values as input")
      :else
        (new-node 
          {:type :vector
           :nodes (vec nodes)
           :codes (vec (map :code nodes))
           :constant (every? constant-node? nodes)}))))

(defn vector-node [& xs] 
  (vec-node xs))

(defn constant-node [v]
  (cond
    (vector? v)
      (let [node (vec-node v)]
        (if (not (:constant node)) (error "Not a constant vector!"))
        node) 
    :else 
      (new-node {:type :scalar 
             :code (double v)
             :constant true})))

(defn code-node [form]
  (new-node {:type :scalar 
             :code form
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
    :else (error "Unable to build an AST node from: " a)))

(def ZERO-NODE (node 0.0))

(defn component [i n]
  "Returns a node that represents the specified component of an input node"
  (let [n (node n)]
	  (if (vector-node? n)
	    (nth (:nodes n) i ZERO-NODE)
	    n))) 

(defn dimensions [x]
  (let [x (node x)]
    (cond
      (scalar-node? x)
        1
      (vector-node? x)
        (count (:nodes x)))))

(defn validate [node]
  (cond
    (not (xor (:code node) (:codes node))) 
      (error "AST node must have :code or :codes")
    :else 
      node))
