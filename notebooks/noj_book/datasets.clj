;; # Example Datasets

;; author: Daniel Slutsky, Ken Huang

;; We may use various sources of datasets for our tutorials here.

(ns noj-book.datasets
  (:require [tablecloth.api :as tc]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## rdatasets
;; One of the main sources is the `rdatasets` namespace of [metamorph.ml](https://github.com/scicloj/metamorph.ml),
;; which can fetch datasets from the [Rdatasets](https://vincentarelbundock.github.io/Rdatasets/) collection.

(rdatasets/datasets-iris)

(rdatasets/ggplot2-mpg)

(rdatasets/openintro-simulated_scatter)

;; ## Plotly
;; We can also use datasets from [Plotly Sample Datasets](https://plotly.github.io/datasets/)

(tc/dataset
 "https://raw.githubusercontent.com/plotly/datasets/refs/heads/master/1962_2006_walmart_store_openings.csv"
 {:key-fn keyword
  :parser-fn {:OPENDATE :string
              :date_super :string}})

;; ## tech.ml.dataset (TMD)
;; [TMD's repo](https://github.com/techascent/tech.ml.dataset/tree/master/test/data)
;; also has some datasets that we can use:

(tc/dataset
 "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
 {:key-fn keyword})

