(ns clisk.test-patterns
  (:use clojure.test)
  (:use clisk.node)
  (:use clisk.patterns)
  (:use clisk.textures))

(deftest test-pattern-validity
  (testing "Java colours"
    (is (validate colour-cubes))
    (is (validate velvet))
    (is (validate plasma))))
