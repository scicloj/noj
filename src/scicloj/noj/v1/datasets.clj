(ns scicloj.noj.v1.datasets
  (:require [tablecloth.api :as tc]
            [clojure.java.io :as io]
            [scicloj.tempfiles.api :as tempfiles]))


(defn resource-dataset [resource-path options]
  (tech.v3.dataset/->dataset
   (.getResourceAsStream (.getContextClassLoader (Thread/currentThread))
                         resource-path)
   options))

(def mtcars
  (resource-dataset "data/mtcars.csv"
                    {:file-type :csv
                     :gzipped? false
                     :key-fn keyword}))

(def iris
  (resource-dataset "data/iris.csv"
                    {:file-type :csv
                     :gzipped? false
                     :key-fn keyword}))

(def diamonds
  (resource-dataset "data/diamonds.csv.gz"
                    {:file-type :csv
                     :gzipped? true
                     :key-fn keyword}))
