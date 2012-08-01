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
   (node 't)
   (node 1N)
   (component 1 [0 1 2])
   (v* 1.0 1)
   (dot [1 1 0] [0 1 1])
   (component 0 (offset -2 (v- pos))) 
   (node (v+ 0.5 0.5))
   (node y)
   (vlet ['q 1] `(+ 0 ~'q))])

(def vector-node-types
  [(node [1 'x])
   (node [1/1 y])
   (warp polar [1 1])])


(def all-node-types
  (vec (concat scalar-node-types 
               vector-node-types)))

(deftest test-node-types
  (testing "Validating expressions"
    (doseq [n all-node-types] 
      (is (validate n))))
  (testing "Validating scalars"
    (doseq [n scalar-node-types] 
      (is (= 1.0 (evaluate n 1.0 1.0 1.0 1.0)))))
  (testing "Validating misc functions"
    (is (validate (height-normal (vsin pos))))
    (is (validate (render-lit 1 (vsin pos))))
    (is (validate (normalize x))))
  (testing "Validating vectors"
    (doseq [n vector-node-types] 
      (is (= [1.0 1.0] (evaluate n 1.0 1.0 1.0 1.0)))))) 


