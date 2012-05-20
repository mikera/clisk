(ns clisk.test-functions
  (:use clojure.test)
  (:use clisk.functions)
  (:import clisk.Util))

(deftest test-plus
  (testing "Colours"
    (is (= [1.0 2.0] (map eval (v+ [1.0 1.0] [0.0 1.0]))))))
