(ns clisk.node
  (:use clisk.util))

(defrecord Node [])

(declare node)

(defn node? [x] 
  (instance? Node x))

(defn constant? [x] 
  (and (node? x) (:constant x)))

(defn new-node [props]
  (Node. nil props))

(defn constant-node [v]
  (cond
    (vector? v)
      (new-node {:type :vector
             :codes (vec (map double v))
             :constant true})
    :else 
      (new-node {:type :scalar 
             :code (double v)
             :constant true})))

(defn validate [node]
  (cond
    (not (xor (:code node) (:codes node))) 
      (error "AST node must have :code or :codes")
    :else 
      node))

(defn vec-node [[xs]]
  (let [nodes (map node xs)]
    (cond
      (not (every? #(= :scalar (:type %)) nodes))
        (error "vec-node requires scalar values as input")
      :else
        (new-node 
          {:type :vector
           :codes (vec (map :code nodes))
           :constant (every? constant? nodes)}))))

(defn node [x]
  "Creates a node from arbitrary input"
  (cond 
    (node? x) x
    (number? x) (constant-node x)
    (sequential? x) (vec-node x)
    :else (error "Unable to build an AST node from: " x)))
