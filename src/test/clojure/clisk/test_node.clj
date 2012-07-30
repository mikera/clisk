(ns clisk.test-node
  (:use clojure.test)
  (:use clisk.node)
  (:use clisk.functions))

(deftest test-node-generation
  (testing "Constant value"
    (is (= 2.0 (:code (constant-node 2.0))))
    (is (= [1.0 2.0] (:codes (constant-node [1 2]))))))

;; testing nodes, should all evaluate to 1.0 when evaluated at [1.0 1.0 1.0 1.0]
(def scalar-node-types
  [(node 1)
   (node 'x)
   (node 't)
   (node 1N)
   (node (v+ 0.5 0.5))
   (node y)])

(def vector-node-types
  [(node [1 'x])
   (node [1/1 y])])


(def all-node-types
  (vec (concat scalar-node-types 
               vector-node-types)))

(deftest test-node-types
  (testing "Validating expressions"
    (is (do 
          (doseq [n all-node-types] (validate n))
          true)))
  (testing "Validating scalars"
    (is (every?
          #(= 1.0 (evaluate % 1.0 1.0 1.0 1.0))
          scalar-node-types)))
   (testing "Validating vectors"
    (is (every?
          #(= [1.0 1.0] (evaluate % 1.0 1.0 1.0 1.0))
          vector-node-types)))
)


