(ns clisk.test-node
  (:use clojure.test)
  (:use clisk.node))

(deftest test-node-generation
  (testing "Constant value"
    (is (= 2.0 (:code (constant-node 2.0))))
    (is (= [1.0 2.0] (:codes (constant-node [1 2]))))))
