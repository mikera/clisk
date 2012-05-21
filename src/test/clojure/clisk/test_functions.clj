(ns clisk.test-functions
  (:use clojure.test)
  (:use clisk.functions)
  (:import clisk.Util))

(deftest test-plus
  (testing "Colours"
    (is (= [1.0 2.0] (map eval (v+ [1.0 1.0] [0.0 1.0]))))))

(deftest test-vlerp
  (testing "Vlerp 3 args"
    (is (= [1.0 1.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 0 ))))
    (is (= [1.0 1.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] -1 ))))
    (is (= [1.5 1.5] (map eval (vlerp [1.0 1.0] [2.0 2.0] 0.5 ))))
    (is (= [2.0 2.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 1 ))))
    (is (= [2.0 2.0] (map eval (vlerp [1.0 1.0] [2.0 2.0] 2 )))))
  (testing "Vlerp 2 args"
    (is (= [1.0 1.0] (map eval ((vlerp [1.0 1.0] [2.0 2.0]) 0 ))))))
