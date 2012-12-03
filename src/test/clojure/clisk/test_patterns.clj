(ns clisk.test-patterns
  (:use clojure.test)
  (:use [clisk node functions patterns textures]))

(deftest test-pattern-validity
  (testing "Java colours"
    (is (validate colour-cubes))
    (is (validate cannon))
    (is (validate velvet))
    (is (validate plasma))
    (is (validate (offset plasma plasma)))
    (is (validate vplasma))))
