(ns clisk.samples.animations
  (:use [clisk live]))

(defn demo []
  ;; we do this to avoid failure in testing if telegenic is not present
  (require '[telegenic.core :as telegenic])
  
  )