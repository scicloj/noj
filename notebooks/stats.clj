;; # Statistics

(ns stats
  (:require [scicloj.noj.v1.datasets :as datasets]
            [scicloj.noj.v1.stats :as stats]
            [tablecloth.api :as tc]))

;; ## Correlation matrices

;; The `stats/calc-correlations-matrix` function commputes the correlation
;; matrix of selected columns of a given dataset,
;; organizing the resulting data as a dataset.

(-> datasets/iris
    (stats/calc-correlations-matrix
     [:sepal-length :sepal-width :petal-length :petal-width]))

;; ## Multivariate regression

;; The `stats/regression-model` function computes a regressiom model (using `scicloj.ml`)
;; and adds some relevant information such as the `R^2` measure.

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

;; The `stats/linear-regression-model` convenience function
;; uses specifically the `:smile.regression/ordinary-least-square` model type.

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

;; ## Histograms

;; The `stats/histogram` function computes the necessary data
;; to plot a histogram.

(-> (repeatedly 99 rand)
    (stats/histogram {:bin-count 5}))
