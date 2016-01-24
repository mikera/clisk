(ns clisk.test-node
  (:require [mikera.vectorz.core :as vec])
  (:use clojure.test)
  (:use clisk.util)
  (:use clisk.node)
  (:use clisk.functions)
  (:use clisk.patterns)
  (:use clisk.textures))


(deftest test-object
  (testing "Object node"
    (let [cn (code-node `(.length ~'a) :objects {'a "foo"})]
      (is (= 3.0 (.calc (compile-fn cn) 0)))))
  (testing "BufferedImage"
    (let [bn (code-node `(.getRGB (static-cast java.awt.image.BufferedImage ~'a) 0 0) 
                        :objects {'a (clisk.util/new-image 10 10)})]
      (is (validate bn)))))

(deftest test-map-symbols
  (is (empty? (map-symbols ['x] ['x])))
  (is (= '[x a y b] (map-symbols '[x y] '[a b]))))

(deftest test-code-generation
  (testing "Constant scalar extends to all parameters"
    (is (= 2.0 (eval (gen-code (constant-node 2.0) '[] '[n o p q] 'q)))))
  (testing "Zero values outside vectorrange"
    (is (= 0.0 (eval (gen-code (constant-node [1 2]) '[] '[n o p q] 'q)))))
  (testing "Constant vector parameters"
    (is (= 5.0 (eval (gen-code (constant-node [1 2 3 4]) '[] '[n o p q] '(+ o p)))))))

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
    (is (validate (dot x y)))
    (is (validate (grain x)))
    (is (validate (seamless 0.25 vsnoise)))
    (is (validate (grain (vfloor x))))
    (is (validate (normalize x))))
  (testing "Validating vectors"
    (doseq [n vector-node-types] 
      (is (= [1.0 1.0] (evaluate n 1.0 1.0 1.0 1.0)))))) 

(deftest test-vectorz-to-node
  (testing "vectors as nodes"
    (is (= [0.0 1.0] (evaluate (vec/vec2 [0 1]))))
    (is (= [0.0 1.0 3.0] (evaluate (v+ (vec/vec3 [0 1 2]) [0 0 1]))))))


(deftest test-image-texture
  (testing "Image textire"
    (is (= (evaluate (img clojure) 0.3 0.4 0.5)
           (evaluate clojure 0.3 0.4 0.5)))))

(deftest test-compile
  (testing "Compiling function"
    (is (= 1.0 (.calc (compile-fn 1.0) 0)))))


