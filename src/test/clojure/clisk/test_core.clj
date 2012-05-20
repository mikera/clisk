(ns clisk.test-core
  (:use clojure.test)
  (:use clisk.core)
  (:import clisk.Util))

(deftest test-colour
  (testing "Colours"
    (is (= (unchecked-int 0xFF000000) (Util/toARGB 0.0 0.0 0.0)))
    (is (= (unchecked-int 0xFFFF0000) (Util/toARGB 1.0 0.0 0.0)))
    (is (= (unchecked-int 0xFFFFFFFF) (Util/toARGB 1.0 1.0 1.0)))))

(deftest test-compile
  (testing "Colours"
    (is (= 1.0 ((compile-fn 1.0) 0)))))