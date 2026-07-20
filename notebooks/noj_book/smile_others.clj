;; # Smile other models reference

;; As discussed in the [Machine Learning](../noj_book.ml_basic.html) chapter,
;; this book contains reference chapters for machine learning models
;; that can be registered in [metamorph.ml](https://github.com/scicloj/metamorph.ml).

;; This specific chapter focuses on model-like algorithms
;; such as clustering and dimension-reduction in
;; [Smile](https://haifengl.github.io/) version 2.6,
;; which are wrapped by
;; [scicloj.ml.smile](https://github.com/scicloj/scicloj.ml.smile).

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)

^:kindly/hide-code
(ns noj-book.smile-others
  (:require
   [scicloj.ml.smile.manifold]
   [scicloj.ml.smile.clustering]
   [scicloj.ml.smile.projections]
   [noj-book.utils.render-tools :refer [render-key-info]]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   [tablecloth.api :as tc]
   [scicloj.tableplot.v1.plotly :as plotly]
   [scicloj.kindly.v4.kind :as kind]))

;; In the following we have a list of all model keys of
;; [Smile](https://haifengl.github.io/) model-like algorithms, including parameters.
;; They can be used in the same way as other models:
(comment
  (require '[scicloj.metamorph.ml :as ml])

  (ml/train dataset
            {:model-type <model-key>
             :param-1 0
             :param-2 1}))

;; Some do not support `ml/predict` and are defined as `unsupervised` learners.
;; Clustering and [PCA](https://en.wikipedia.org/wiki/Principal_component_analysis)
;; are in this group.

;; ## Example: t-SNE

;; Let us use Smile to compute the
;; [t-distributed stochastic neighbor embedding](https://en.wikipedia.org/wiki/T-distributed_stochastic_neighbor_embedding)
;; form of nonlinear dimension reduction.

;; We will apply it to [the Iris dataset](https://vincentarelbundock.github.io/Rdatasets/doc/datasets/iris.html)
;; of the [Rdatasets](https://vincentarelbundock.github.io/Rdatasets/articles/data.html)
;; collection.

(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.ml.smile.manifold]
         '[scicloj.metamorph.ml.rdatasets :as rdatasets]
         '[tablecloth.api :as tc]
         '[scicloj.tableplot.v1.plotly :as plotly])

;; Here is our dataset:
(def iris
  (rdatasets/datasets-iris))

iris

;; Let us compute t-SNE over the relevant numerical columns:
(def iris-t-SNE
  (-> iris
      (tc/select-columns [:sepal-length :sepal-width
                          :petal-length :petal-width])
      (ml/train {:model-type :smile.manifold/tsne})))

(kind/pprint iris-t-SNE)

;; Let us now extract the relevant coordinates, reducing
;; our data dimension to 2:

(def iris-t-SNE-coordinates
  (-> iris-t-SNE
      :model-data
      :coordinates
      (tc/rename-columns [:x :y])))

iris-t-SNE-coordinates

;; We can attach these columns back to our original dataset
;; and plot that, coloring by species.

(-> iris
    (tc/add-columns iris-t-SNE-coordinates)
    (plotly/layer-point {:=color :species}))

;; ## Smile manifolds

^:kindly/hide-code
(render-key-info :smile.manifold)

;; ## Smile/Fastmath clustering
^:kindly/hide-code
(render-key-info :fastmath.cluster)

;; ## Smile projections
^:kindly/hide-code
(render-key-info :smile.projections)


