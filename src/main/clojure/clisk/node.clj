(ns clisk.node
  (:use clisk.util))

(defrecord Node [])

(defn node? [x] 
  (instance? Node x))

(defn node [props]
  (Node. nil props))

(defn constant-node [v]
  (cond
    (vector? v)
      (node {:type :vector
             :codes (vec (map double v))
             :constant true})
    :else 
      (node {:type :scalar 
             :code (double v)
             :constant true})))

(defn validate [node]
  (cond
    (not (xor (:code node) (:codes node))) (error "AST node must have :code or :codes")
    :else node))