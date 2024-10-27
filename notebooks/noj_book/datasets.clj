;; # Datasets

;; author: Daniel Slutsky, Ken Huang

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

(def scatter
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/openintro/simulated_scatter.csv"
      (tc/dataset {:key-fn keyword})))

(tc/head scatter)

;; ## Plotly
;; We can also use datasets from [Plotly Sample Datasets](https://plotly.github.io/datasets/)

(-> "https://raw.githubusercontent.com/plotly/datasets/refs/heads/master/1962_2006_walmart_store_openings.csv"
    (tc/dataset {:key-fn keyword
                 :parser-fn {:OPENDATE :string
                             :date_super :string}})
    (tc/head))

;; ## tech.ml.dataset (TMD)
;; [TMD's repo](https://github.com/techascent/tech.ml.dataset/tree/master/test/data)
;; also has some datasets that we can use:

(def stocks
  (tc/dataset
   "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
   {:key-fn keyword}))

stocks
