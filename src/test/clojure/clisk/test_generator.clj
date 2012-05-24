(ns clisk.test-generator
  (:use clojure.test)
  (:use clisk.core)
  (:import clisk.Generator)
  (:import java.awt.image.BufferedImage))

(deftest test-gen
  (testing "Basic generator"
    (let [^BufferedImage b (Generator/generate "[1 1 1]" 10 12)]
      (is (== 10 (.getWidth b)))
      (is (== 12 (.getHeight b)))
      (is (== (unchecked-int 0xFFFFFFFF) (.getRGB b 0 0))))))
