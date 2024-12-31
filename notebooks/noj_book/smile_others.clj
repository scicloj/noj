;; # Smile other models reference - DRAFT 🛠

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)

^:kindly/hide-code
(ns noj-book.smile-others
  (:require
   [scicloj.ml.smile.manifold]
   [scicloj.ml.smile.clustering]
   [scicloj.ml.smile.projections]
   [noj-book.utils.render-tools :refer [render-key-info]]))

;; ## Smile manifolds

^:kindly/hide-code
(render-key-info :smile.manifold)

;; ## Smile/Fastmath clustering
^:kindly/hide-code
(render-key-info :fastmath.cluster)

;; ## Smile projections
^:kindly/hide-code
(render-key-info :smile.projections)


