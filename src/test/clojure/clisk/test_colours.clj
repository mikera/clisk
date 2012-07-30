(ns clisk.test-colours
  (:use clojure.test)
  (:use clisk.colours))

(deftest test-basic-colours
  (testing "Java colours"
    (is (= [1.0 1.0 1.0] white))
    (is (= [0.0 0.0 0.0] black))))
