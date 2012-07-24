(ns clisk.test-functions
  (:use clojure.test)
  (:use clisk.node)
  (:use clisk.functions)
  (:use clisk.core)
  (:use clisk.util)
  (:import clisk.Util)
  (:import java.awt.image.BufferedImage))

(deftest test-values
  (testing "Scalars"
    (is (= 1.0 (evaluate 1)))
    (is (= 1.0 (evaluate 1N)))))


(deftest test-vectorize
  (testing "Vectorising"
    (is (= [1.0 1.0 1.0 1.0] (evaluate [1 1 1 1])))
    (is (= (node ['x]) (vectorize x)))
    (is (= (node [1.0]) (vectorize 1))))
  (testing "Resized vectors"
    (is (= [1.0 2.0] (evaluate (vectorize 2 [1 2 3]))))
    (is (= [1.0 2.0 3.0] (evaluate (vectorize 3 [1 2 3]))))
    (is (= [1.0 2.0 3.0 0.0] (evaluate (vectorize 4 [1 2 3]))))
    (is (= [2.0 2.0] (evaluate (vectorize 2 2))))
    (is (= [] (evaluate (vectorize 0 2))))))

(deftest test-cross
  (testing "Cross product"
    (is (= [0.0 0.0 1.0] (evaluate (vcross3 [1.0 0.0 0.0] [0.0 1.0 0.0]))))))


(deftest test-plus
  (testing "Plus"
    (is (= [1.0 2.0] (evaluate (v+ [1.0 1.0] [0.0 1.0]))))
    (is (= 3.0 (evaluate (v+ 1.0 2.0))))))

(deftest test-mul
  (testing "Multiply"
    (is (= [2.0 6.0] (evaluate (v* [1.0 2.0] [2.0 3.0]))))
    (is (= 10.0 (evaluate (v* 2.0 5.0))))))

(deftest test-height
  (testing "Height comes from z component"
    (is (= 10.0 (evaluate (height [0 5 10 15]))))))

(deftest test-components
  (testing "Components"
    (is (= 10.0 (evaluate (component 2 [0 5 10 15]))))
    (is (= 1.0 (evaluate (component 0 1))))
    (is (= 1.0 (evaluate (component 10 1))))
    (is (= 0.0 (evaluate (component 10 [1]))))
    (is (= [0.0 3.0 4.0] (evaluate (components [0 1 1] [2 3 4 5]))))))


(deftest test-vlet
  (testing "vlet double"
    (is (== 1.0 (evaluate (component 0 (vlet ['a 1.0] 'a))))))
   (testing "vlet double"
    (is (== 3.0 (evaluate (component 0 (vlet ['a 1.0 'b 2.0] `(+ ~'a ~'b))))))))

(deftest test-vif
  (testing "vif scalar conditions"
    (is (= 1.0 (evaluate (vif 0.1 1 2))))
    (is (= 2.0 (evaluate (vif 0.0 1 2))))
    (is (= 2.0 (evaluate (vif -0.1 1 2)))))
  (testing "vif vector conditions"
    (is (= 2.0 (evaluate (vif [-0.1 0.1] 1 2))))
    (is (= 1.0 (evaluate (vif [0.1 -0.1] 1 2)))))
  (testing "vif results vectorised to largest vector"
    (is (= [3.0 3.0] (evaluate (vif 1 3.0 [4.0 5.0]))))
    (is (= [3.0 0.0] (evaluate (vif 1 [3.0] [4.0 5.0]))))))

(deftest test-vlerp
  (testing "Vlerp 3 args"
    (is (= [1.0 1.0] (evaluate (vlerp 0   [1.0 1.0] [2.0 2.0]))))
    (is (= [1.0 1.0] (evaluate (vlerp -1  [1.0 1.0] [2.0 2.0]))))
    (is (= [1.5 1.5] (evaluate (vlerp 0.5 [1.0 1.0] [2.0 2.0]))))
    (is (= [2.0 2.0] (evaluate (vlerp 1   [1.0 1.0] [2.0 2.0]))))
    (is (= [2.0 2.0] (evaluate (vlerp 2   [1.0 1.0] [2.0 2.0])))))
  (testing "vlerp scalars"
    (is (= 2.5 (evaluate (vlerp 0.5 2 3))))))

(deftest test-colour-map
  (testing "Out of range"
    (is (= 2.0 (evaluate ((colour-map [[0 2] [1 3]]) -1))))
    (is (= 3.0 (evaluate ((colour-map [[0 2] [1 3]]) 10)))))
  (testing "Midpoint"
    (is (= 2.5 (evaluate ((colour-map [[0 2] [1 3]]) 0.5))))))

(deftest test-lengths
  (testing "dot"
    (is (== 1.0 (evaluate (dot [1 0] [1 0])))))
  (testing "length"
    (is (== 1.0 (evaluate (length [1 0 0]))))
    (is (== 2.0 (evaluate (length [1 1 1 1]))))
    (is (== 1.0 (evaluate (length 1))))))

(deftest test-colours
  (testing "rgb"
    (is (= [0.3 0.4 0.5 1.0] (rgb 0.3 0.4 0.5)))
    (is (= [0.3 0.4 0.5 0.6] (rgb 0.3 0.4 0.5 0.6)))))

(deftest test-normal
  (testing "Normal of flat surface"
    (is 
      (= (sample (height-normal [x y 1]) [0.0 0.0])
         [0.0 0.0 1.0]))))

(deftest test-texture
  (testing "Basic texture map"
    (let [^BufferedImage im (clisk.util/new-image 10 10)]
      (is (= [0.0 0.0 0.0 0.0] (evaluate (texture-map im) )))
      (.setRGB im 0 0 (unchecked-int 0xFFFFFFFF) )
      (is (= [1.0 1.0 1.0 1.0] (evaluate (texture-map im) ))))))

(deftest test-error
  (testing "Clisk Error"
    (is 
      (= clisk.CliskError
         (try 
           (error "Foobar")
           (catch Throwable t (type t)))))))
