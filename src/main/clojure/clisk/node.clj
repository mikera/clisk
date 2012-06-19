(ns clisk.node)

(defrecord Node [])

(defn node? [x] 
  (instance? Node x))

(defn node [props]
  (Node. nil props))

(defn constant-node [v]
  (cond
    (vector? v)
      (node {:type :scalar
             :codes v})
    :else 
      (node {:type :scalar 
             :code v})))