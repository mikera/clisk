(ns clisk.test-colours
  (:use clojure.test)

  (:use [clisk core functions colours]))

(deftest test-basic-colours
  (testing "Java colours"
    (is (= [1.0 1.0 1.0] white))
    (is (= [0.0 0.0 0.0] black))))

(deftest test-hsl
  (testing "HSL conversions"
    (is (= 1.0 (evaluate (lightness-from-rgb white))))
    (is (= 0.0 (evaluate (lightness-from-rgb black))))
    (is (= 0.5 (evaluate (lightness-from-rgb blue))))
  ))
