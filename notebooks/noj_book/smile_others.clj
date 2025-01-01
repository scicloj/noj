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

;; In the following we have a list of all model keys of Smile model-like
;; algorithms including parameters.
;; They can be used in the same way as other models:
(comment
  (ml/train df
            {:model-type <model-key>
             :param-1 0
             :param-2 1}))

;; Some do not support `ml/predict` and are defined as `unsupervised` learners.
;; Clustering and PCA are in this group.

;; ## Smile manifolds

^:kindly/hide-code
(render-key-info :smile.manifold)

;; ## Smile/Fastmath clustering
^:kindly/hide-code
(render-key-info :fastmath.cluster)

;; ## Smile projections
^:kindly/hide-code
(render-key-info :smile.projections)


