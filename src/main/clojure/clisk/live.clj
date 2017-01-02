(ns 
  ^{:author "mikera"
    :doc "Namespace with all includes ready for live coding."}    
  clisk.live
;;  (:refer-clojure :exclude [* + - /])
  (:require [mikera.cljutils.namespace :as n]))

(n/pull-all clisk.core)
(n/pull-all clisk.node)
(n/pull-all clisk.functions)
(n/pull-all clisk.patterns)
(n/pull-all clisk.colours)
(n/pull-all clisk.textures)
(n/pull-all clisk.util)
(n/pull-all clisk.effects)
;; (n/pull-all clisk.animation)

;; do we really want to override these???
;;(def * v*)
;;(def + v+)
;;(def - v-)
;;(def / clisk.functions/vdivide)