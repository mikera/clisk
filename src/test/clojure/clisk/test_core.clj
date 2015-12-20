(ns clisk.test-core
  (:use clojure.test)
  (:use clisk.core)
  (:use clisk.node)
  (:use clisk.functions)
  (:require [mikera.vectorz.core :as v])
  (:import clisk.Util)
  (:import [mikera.vectorz Vector]))

(deftest test-colour
  (testing "Colours"
    (is (= (unchecked-int 0xFF000000) (Util/toARGB 0.0 0.0 0.0)))
    (is (= (unchecked-int 0xFFFF0000) (Util/toARGB 1.0 0.0 0.0)))
    (is (= (unchecked-int 0xFFFFFFFF) (Util/toARGB 1.0 1.0 1.0)))))

(deftest test-vector-function
  (testing "vector-function"
    (is (= (v/of 1 2) ((vector-function [1 2]) [0 0 0 0])))))

(deftest test-sampler  
  (testing "sampler of [x y z]"
    (let [samp (sampler [x y z])]
      (is (= [1.0 2.0 3.0] (samp [1 2 3])))
      (is (= [1.0 2.0 0.0] (samp [1 2]))))))
