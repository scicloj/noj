;; # Datasets

;; author: Daniel Slutsky

;; ## Rdatasets
;; For our tutorials here,
;; let us fetch some datasets from [Rdatasets](https://vincentarelbundock.github.io/Rdatasets/):

(ns noj-book.datasets
  (:require [tablecloth.api :as tc]))

(def iris
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
      (tc/dataset {:key-fn keyword})
      (tc/rename-columns {:Sepal.Length :sepal-length
                          :Sepal.Width :sepal-width
                          :Petal.Length :petal-length
                          :Petal.Width :petal-width
                          :Species :species})))

iris

(def mtcars
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/mtcars.csv"
      (tc/dataset {:key-fn keyword})))

mtcars

;; ## Plotly
;; We can also use datasets from [Plotly Sample Datasets](https://plotly.github.io/datasets/)

(-> "https://raw.githubusercontent.com/plotly/datasets/refs/heads/master/1962_2006_walmart_store_openings.csv"
    (tc/dataset {:key-fn keyword})
    (tc/head))
