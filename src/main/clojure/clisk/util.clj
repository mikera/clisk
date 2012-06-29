(ns clisk.util)

(defmacro error
  "Throws a clisk error with the provided message(s)"
  ([& vals]
    `(throw (clisk.CliskError. (str ~@vals)))))

(defmacro xor 
  "Returns logical xor of values"
  ([] 
    nil)
  ([a]
    a)
  ([a & more]
    `(let [a# ~a
           b# (xor ~@more)]
       (if a#
         (if b# nil a#)
         b#))))