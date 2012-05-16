(ns clisk.core)

(defn vectorize-item [code]
  (cond 
    (number? code) (double code)
    (symbol? code) code
    (sequential? code) code
    :else (throw (Error. (str "Invalid vector item: " (type code))))))

(defn vectorize* 
  ([dims code]
	  (cond
	    (vector? code) (let [c (count code)]
	                  (if (< c dims)
	                    (vec (concat code (repeat (- dims c) 0.0)))
	                    code))
	    (sequential? code)
	      (let [[op & more] code
              more (map vectorize-item more)
	            vectorized-more (map #(vectorize* dims %) more)]
	        (vec (for [i (range dims)]
	          (cons op (map #(% i) vectorized-more)))))
	    :else 
       (vec (repeat dims (vectorize-item code))))))

(defmacro vectorize [dims code]
  (vectorize* dims code))