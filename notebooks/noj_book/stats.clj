;; # Statistics (experimental 🛠)

(ns noj-book.stats
  (:require [scicloj.noj.v1.stats :as stats]
            [tablecloth.api :as tc]))

;; ## Example data

(def iris
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
      (tc/dataset {:key-fn keyword})
      (tc/rename-columns {:Sepal.Length :sepal-length
                          :Sepal.Width :sepal-width
                          :Petal.Length :petal-length
                          :Petal.Width :petal-width
                          :Species :species})))

;; ## Correlation matrices

;; The `stats/calc-correlations-matrix` function commputes the correlation
;; matrix of selected columns of a given dataset,
;; organizing the resulting data as a dataset.

(-> iris
    (stats/calc-correlations-matrix
     [:sepal-length :sepal-width :petal-length :petal-width]))

;; ## Multivariate regression

;; The `stats/regression-model` function computes a regressiom model (using `scicloj.ml`)
;; and adds some relevant information such as the `R^2` measure.

(-> iris
    (stats/regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/elastic-net})
    (dissoc :model-data))

(-> iris
    (stats/regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/ordinary-least-square})
    (dissoc :model-data))

;; The `stats/linear-regression-model` convenience function
;; uses specifically the `:smile.regression/ordinary-least-square` model type.

(-> iris
    (stats/linear-regression-model
     :sepal-length
     [:sepal-width :petal-length :petal-width])
    (dissoc :model-data))

;; ## Adding regression predictions to a dataset

;; The `stats/add-predictions` function
;; models a target column using feature columns,
;; adds a new prediction column with the model predictions.

(-> iris
    (stats/add-predictions
     :sepal-length
     [:sepal-width :petal-length :petal-width]
     {:model-type :smile.regression/ordinary-least-square}))

;; It attaches the model's information
;; to the metadata of that new column.

(-> iris
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
