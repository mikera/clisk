(ns clisk.test-functions
  (:use clojure.test)
  (:use clisk.functions)
  (:import clisk.Util))

(deftest test-vectorize
  (testing "Scalars"
    (is (= [1.0 1.0 1.0 1.0] (vectorize 1)))
    (is (= [1.0 1.0 1.0 1.0] (vectorize [1 1 1 1])))
    (is (= ['x 'x 'x 'x] (vectorize x)))))

(deftest test-cross
  (testing "Scalars"
    (is (= [0.0 0.0 1.0] (map eval (vcross3 [1.0 0.0 0.0] [0.0 1.0 0.0]))))
    (is (= ['x 'x 'x 'x] (vectorize x)))))


(deftest test-plus
  (testing "Colours"
    (is (= [1.0 2.0] (map eval (v+ [1.0 1.0] [0.0 1.0]))))))

(deftest test-mul
  (testing "Colours"
    (is (= [2.0 2.0] (map eval (v* [1.0 1.0] [2.0 2.0]))))))

(deftest test-vlerp
  (testing "Vlerp 3 args"
    (is (= [1.0 1.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 0 ))))
    (is (= [1.0 1.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] -1 ))))
    (is (= [1.5 1.5] (map eval (vlerp [1.0 1.0] [2.0 2.0] 0.5 ))))
    (is (= [2.0 2.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 1 ))))
    (is (= [2.0 2.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 2 )))))
  (testing "Vlerp 2 args"
    (is (= [1.0 1.0] (map eval ((vlerp [1.0 1.0] [2.0 2.0]) 0 ))))))

(deftest test-vlet
  (testing "vlet double"
    (is (== 1.0 (eval (component 0 (vlet ['a 1.0] 'a))))))
   (testing "vlet double"
    (is (== 3.0 (eval (component 0 (vlet ['a 1.0 'b 2.0] `(+ ~'a ~'b)))))))
  (testing "vlet vector"
    (is (== 2 (eval (component 0 (vlet ['a [2 2]] `(count ~'a))))))))

(deftest test-lengths
  (testing "dot"
    (is (== 1.0 (eval (dot [1 0] [1 0])))))
  (testing "length"
    (is (== 1.0 (eval (length [1 0 0]))))))