(ns scicloj.noj.v1.datasets
  (:require [tablecloth.api :as tc]
            [clojure.java.io :as io]
            [scicloj.tempfiles.api :as tempfiles]))

(defonce mtcars-csv
  (let [{:keys [path]} (tempfiles/tempfile! ".csv")]
    (->> "data/mtcars.csv"
         io/resource
         slurp
         (spit path))
    path))

(defonce iris-csv
  (let [{:keys [path]} (tempfiles/tempfile! ".csv")]
    (->> "data/iris.csv"
         io/resource
         slurp
         (spit path))
    path))

(def mtcars
  (-> mtcars-csv
      (tc/dataset {:key-fn keyword})))

(def iris
  (-> iris-csv
      (tc/dataset {:key-fn keyword})))
