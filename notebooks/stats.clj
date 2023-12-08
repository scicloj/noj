;; # Statistics

(ns stats
  (:require [scicloj.noj.v1.datasets :as datasets]
            [scicloj.noj.v1.stats :as stats]))

;; ## Correlation matrices

(-> datasets/iris
    (stats/calc-correlations-matrix
     [:sepal-length :sepal-width :petal-length :petal-width]))

;; ## Multivariate regression

(-> datasets/iris
    (stats/regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/elastic-net})
    (dissoc :model-data))

(-> datasets/iris
    (stats/regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/ordinary-least-square})
    (dissoc :model-data))

(-> datasets/iris
    (stats/linear-regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width])
    (dissoc :model-data))

;; ## Adding regression predictions to a dataset

;; The `stats/add-predictions` function
;; models a target column using feature columns,
;; adds a new prediction column with the model predictions.

(-> datasets/iris
    (stats/add-predictions
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/ordinary-least-square}))

;; It attaches the model's information
;; to the metadata of that new column.

(-> datasets/iris
    (stats/add-predictions
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/ordinary-least-square})
    :sepal-length-prediction
    meta
    (update :model
            dissoc :model-data :predict :predictions))
