(ns scicloj.noj.v1.datasets
  (:require [tablecloth.api :as tc]))

(def mtcars
  (-> "data/mtcars.csv"
      (tc/dataset {:key-fn keyword})))

(def iris
  (-> "data/iris.csv"
      (tc/dataset {:key-fn keyword})))
