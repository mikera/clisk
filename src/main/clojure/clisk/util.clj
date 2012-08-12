(ns clisk.util
  (:import javax.imageio.ImageIO)
  (:import clisk.Util)
  (:import [clojure.lang RT Compiler Compiler$C])
  (:import java.awt.image.BufferedImage))

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

(defn expression-info-internal
  [expr]
  (let [fn-ast (Compiler/analyze Compiler$C/EXPRESSION expr)
        expr-ast (.body (first (.methods fn-ast)))]
    (when (.hasJavaClass expr-ast)
      {:class (.getJavaClass expr-ast)
       :primitive? (.isPrimitive (.getJavaClass expr-ast))})))

(defn expression-info
  "Uses the Clojure compiler to analyze the given s-expr.  Returns
  a map with keys :class and :primitive? indicating what the compiler
  concluded about the return value of the expression.  Returns nil if
  no type info can be determined at compile-time.
  
  Example: (expression-info '(+ (int 5) (float 10)))
  Returns: {:class float, :primitive? true}"
  [expr]
  (expression-info-internal `(fn [] ~expr)))

(defmacro typeof 
  ([expression]
    (:class (expression-info expression))))

(defmacro primitive? 
  ([expression]
    (:primitive? (expression-info expression))))

(defn ^ClassLoader context-class-loader []
  (.getContextClassLoader (Thread/currentThread)))

(defn ^java.awt.image.BufferedImage load-image [resource-name]
  (javax.imageio.ImageIO/read (.getResource (context-class-loader) resource-name)))

(defn ^java.awt.image.BufferedImage new-image [w h]
  (Util/newImage (int w) (int h)))