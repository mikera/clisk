(ns clisk.test-node
  (:use clojure.test)
  (:use clisk.util)
  (:use clisk.node)
  (:use clisk.functions))


(deftest test-object
  (testing "Object node"
    (let [cn (code-node `(.length ~'a) :objects {'a "foo"})]
      (is (= 3.0 (.calc (compile-fn cn) 0)))))
  (testing "BufferedImage"
    (let [bn (code-node `(.getRGB ~(with-meta 'a {:tag 'java.awt.image.BufferedImage}) 0 0) 
                        :objects {'a (clisk.util/new-image 10 10)})]
      (is (validate bn)))))

(deftest test-node-generation
  (testing "Constant value"
    (is (= 2.0 (:code (constant-node 2.0))))
    (is (= [1.0 2.0] (:codes (constant-node [1 2]))))))

;; testing nodes, should all evaluate to 1.0 when evaluated at [1.0 1.0 1.0 1.0]
(def scalar-node-types
  [(node 1)
   (node 't)
   (node 1N)
   (colour-map [[0 0] [0.25 1] [0.75 1] [1 0]] [0.5])
   (component 1 [0 1 2])
   (v* 1.0 1)
   (dot [1 1 0] [0 1 1])
   (component 0 (offset -2 (v- pos))) 
   (node (v+ 0.5 0.5))
   (node y)
   (vlet [q 1] `(+ 0 ~q))])

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


(deftest test-compile
  (testing "Compiling function"
    (is (= 1.0 (.calc (compile-fn 1.0) 0)))))


