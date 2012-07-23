(ns clisk.util
  (:import javax.imageio.ImageIO)
  (:import clisk.Util)
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

(defn ^ClassLoader context-class-loader []
  (.getContextClassLoader (Thread/currentThread)))

(defn ^java.awt.image.BufferedImage load-image [resource-name]
  (javax.imageio.ImageIO/read (.getResource (context-class-loader) resource-name)))

(defn ^java.awt.image.BufferedImage new-image [w h]
  (Util/newImage (int w) (int h)))