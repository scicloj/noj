;; # Smile classification models reference - DRAFT 🛠

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)


(ns noj-book.smile-classification
  (:require
   [noj-book.utils.example-code :refer [iris-std]]
   [noj-book.utils.render-tools :refer [render-key-info]]
   [noj-book.utils.surface-plot :refer [surface-plot]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.ml.smile.classification]
   [scicloj.ml.xgboost]
   [tech.v3.dataset.metamorph :as ds-mm]))



;; ## Smile classification models reference
;; In the following we have a list of all model keys of
;; [Smile](https://haifengl.github.io/) classification models, including parameters.
;; They can be used like this:

(comment
  (ml/train df
            {:model-type <model-key>
             :param-1 0
             :param-2 1}))

^:kindly/hide-code
(render-key-info :smile.classification)

;; # Compare decision surfaces of different classification models


;; In the following we see the decision surfaces of some models on the
;; same data from the Iris dataset using 2 columns `:sepal_width` and `:sepal_length`:

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

