(ns clisk.node)

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