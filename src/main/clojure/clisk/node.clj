(ns clisk.node
  (:use clisk.util))

(defrecord Node [])

(declare node)

(defn new-node [props]
  (Node. nil props))


(defn node? [x] 
  (instance? Node x))

(defn constant-node? [x] 
  (and (node? x) (:constant x)))

(defn constant-node [v]
  (cond
    (vector? v)
      (new-node {:type :vector
             :nodes (vec (map double v))
             :constant true})
    :else 
      (new-node {:type :scalar 
             :code (double v)
             :constant true})))

(defn vec-node [[xs]]
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

(defn node [x]
  "Creates a node from arbitrary input. Idempotent, can be used to force conversion to node."
  (cond 
    (node? x) x
    (number? x) (constant-node x)
    (vector? x) (vec-node x)
    :else (error "Unable to build an AST node from: " x)))

(def ZERO-NODE (node 0.0))

(defn component [i n]
  "Returns a node that represents the specified component of an input node"
  (let [n (node n)]
	  (if (= :vector (:type n))
	    (nth (:nodes node) i ZERO-NODE)
	    node)))

(defn validate [node]
  (cond
    (not (xor (:code node) (:nodes node))) 
      (error "AST node must have :code or :nodes")
    :else 
      node))
