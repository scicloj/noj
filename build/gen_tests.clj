(ns gen-tests
  (:require
   [scicloj.clay.v2.api :as clay]))

(defn do-generate-tests []
  (clay/make! {:source-path "notebooks/noj_book/ml_basic.clj" :show false})

  (clay/make! {:source-path "notebooks/noj_book/prepare_for_ml.clj" :show false})
  (println :done :prepare-for-ml)
  (clay/make! {:source-path "notebooks/noj_book/automl.clj" :show false})
  (println :done :automl)
  (shutdown-agents))
