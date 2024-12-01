(ns noj-book.smile-classification
  (:require
   [noj-book.example-code :refer [iris-std]]
   [noj-book.render-tools :refer [render-key-info]]
   [noj-book.surface-plot :refer [surface-plot]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.ml.smile.classification]
   [scicloj.ml.xgboost]
   [tech.v3.dataset.metamorph :as ds-mm]))





;; ## Smile classification models reference - DRAFT 🛠


(render-key-info :smile.classification)

;; # Compare decision surfaces of different classification models


;; In the following we see the decision surfaces of some models on the
;; same data from the Iris dataset using 2 columns :sepal_width and sepal_length:

^:kindly/hide-code
(defn make-iris-pipeline [model-options]
  (mm/pipeline
   (ds-mm/set-inference-target :species)
   (ds-mm/categorical->number [:species])
   (ml/model model-options)))

^:kindly/hide-code
(mapv #(kind/vega-lite (surface-plot iris-std [:sepal_length :sepal_width] (make-iris-pipeline %) (:model-type %)))
      [{:model-type :smile.classification/ada-boost}
       {:model-type :smile.classification/decision-tree}
       {:model-type :smile.classification/gradient-tree-boost}
       {:model-type :smile.classification/knn}
       {:model-type :smile.classification/logistic-regression}
       {:model-type :smile.classification/random-forest}
       {:model-type :smile.classification/linear-discriminant-analysis}
       {:model-type :smile.classification/regularized-discriminant-analysis}
       {:model-type :smile.classification/quadratic-discriminant-analysis}
       {:model-type :xgboost/classification}])



;; This shows nicely that different model types have different capabilities
;; to seperate and therefore classify data.

