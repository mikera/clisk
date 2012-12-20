(ns clisk.test-live
  (:use clojure.test)
  (:use [clisk live]))

(deftest test-stuff
  (testing "functions"
    (is (= 1.0 (evaluate (v- 2 1)))))
  (testing "colours"
    (is (validate (node colour-cubes)))))
