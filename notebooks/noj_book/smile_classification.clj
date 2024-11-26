^:kindly/hide-code
(ns noj-book.smile-classification
  (:require
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.toydata :as datasets]
   [tablecloth.api :as tc]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [scicloj.ml.xgboost]
   [noj-book.render-tools :refer [render-key-info kroki surface-plot iris-std]]))
^:kindly/hide-code
(require '[scicloj.ml.smile.classification])


;; ## Smile classification models reference - DRAFT 🛠

^:kindly/hide-code
(render-key-info :smile.classification/ada-boost)



;; In this example we will use the capability of the Ada boost classifier
;; to give us the importance of variables.

;; As data we take here the Wiscon Breast Cancer dataset, which has 30 variables.

(def df
  (->
   (datasets/breast-cancer-ds)))
(tc/column-names df)


;; To get an overview of the dataset, we print its summary:

(-> df tc/info)



;; Then we create a metamorph  pipeline with the ada boost model:

(def ada-pipe-fn
  (mm/pipeline
   (ds-mm/set-inference-target :class)
   (ds-mm/categorical->number [:class])
   (ml/model
    {:model-type :smile.classification/ada-boost})))


;; We run the pipeline in :fit. As we just explore the data,
;; not train.test split is needed.

(def trained-ctx
  (mm/fit-pipe df
               ada-pipe-fn))

;; "Next we take the model out of the pipeline:"
(def model
  (-> trained-ctx vals (nth 2) ml/thaw-model))

;; The variable importance can be obtained from the trained model,
(def var-importances
  (mapv
   #(hash-map :variable %1
              :importance %2)
   (map
    #(first (.variables %))
    (.. model formula predictors))
   (.importance model)))
var-importances


;; and we plot the variables:

(kind/vega-lite
 {:data {:values
         var-importances}
  :width  800
  :height 500
  :mark {:type "bar"}
  :encoding {:x {:field :variable :type "nominal" :sort "-y"}
             :y {:field :importance :type "quantitative"}}})


(render-key-info ":smile.classification/decision-tree")

;; A decision tree learns a set of rules from the data in the form
;; of a tree, which we will plot in this example.
;; We use the iris dataset:


(def iris (datasets/iris-ds))
iris


;; We make a pipe only containing the model, as the dataset is ready to
;; be used by `scicloj.ml`
(def trained-pipe-tree
  (mm/fit-pipe
   iris
   (mm/pipeline
    {:metamorph/id :model}
    (ml/model
     {:model-type :smile.classification/decision-tree}))))

;; We extract the Java object of the trained model.

(def tree-model
  (-> trained-pipe-tree :model ml/thaw-model))
tree-model

;; The model has a .dot function, which returns a GraphViz textual
;; representation of the decision tree, which we render to svg using the
;; [kroki](https://kroki.io/) service.

(kind/html
 (String. (:body (kroki (.dot tree-model) :graphviz :svg)) "UTF-8"))



^:kindly/hide-code
(render-key-info ":smile.classification/discrete-naive-bayes")

^:kindly/hide-code
(render-key-info ":smile.classification/gradient-tree-boost")

^:kindly/hide-code
(render-key-info ":smile.classification/knn")
;; In this example we use a knn model to classify some dummy data.
;; The training data is this:

(def df-knn
  (tc/dataset {:x1 [7 7 3 1]
               :x2 [7 4 4 4]
               :y [:bad :bad :good :good]}))
df-knn


;; Then we construct a pipeline with the knn model,
;; using 3 neighbors for decision.

(def knn-pipe-fn
  (mm/pipeline
   (ds-mm/set-inference-target :y)
   (ds-mm/categorical->number [:y])
   (ml/model
    {:model-type :smile.classification/knn
     :k 3})))

;; We run the pipeline in mode fit:

(def trained-ctx-knn
  (knn-pipe-fn {:metamorph/data df-knn
                :metamorph/mode :fit}))

;; Then we run the pipeline in mode :transform with some test data
;; and take the prediction and convert it from numeric into categorical:

(->
 trained-ctx-knn
 (merge
  {:metamorph/data (tc/dataset
                    {:x1 [3 5]
                     :x2 [7 5]
                     :y [nil nil]})
   :metamorph/mode :transform})
 knn-pipe-fn
 :metamorph/data
 (ds-mod/column-values->categorical :y)
 seq)


^:kindly/hide-code
(render-key-info ":smile.classification/logistic-regression")

^:kindly/hide-code
(render-key-info ":smile.classification/maxent-binomial")

^:kindly/hide-code
(render-key-info ":smile.classification/maxent-multinomial")

^:kindly/hide-code
(render-key-info ":smile.classification/random-forest")
;; The following code plots the decision surfaces of the random forest
 ;; model on pairs of features.

;; We use the Iris dataset for this.


iris-std



;; The next function creates a vega specification for the random forest
;; decision surface for a given pair of column names.

^:kindly/hide-code
(defn make-iris-pipeline [model-options]
  (mm/pipeline
   (ds-mm/set-inference-target :species)
   (ds-mm/categorical->number [:species])
   (ml/model model-options)))



(def rf-pipe
  (make-iris-pipeline
   {:model-type :smile.classification/random-forest}))

^:kindly/hide-code


^:kindly/hide-code


(kind/vega-lite (surface-plot iris [:sepal_length :sepal_width] rf-pipe :smile.classification/random-forest))

(kind/vega-lite
 (surface-plot iris-std [:sepal_length :petal_length] rf-pipe :smile.classification/random-forest))

(kind/vega-lite
 (surface-plot iris-std [:sepal_length :petal_width] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:sepal_width :petal_length] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:sepal_width :petal_width] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:petal_length :petal_width] rf-pipe :smile.classification/random-forest))


^:kindly/hide-code
(render-key-info ":smile.classification/sparse-logistic-regression")

^:kindly/hide-code
(render-key-info ":smile.classification/sparse-svm")

^:kindly/hide-code
(render-key-info ":smile.classification/svm")


;; # Compare decision surfaces of different classification models


;; In the following we see the decision surfaces of some models on the
;; same data from the Iris dataset using 2 columns :sepal_width and sepal_length:
^:kindly/hide-code
(mapv #(kind/vega-lite (surface-plot iris-std [:sepal_length :sepal_width] (make-iris-pipeline %) (:model-type %)))
     [
      {:model-type :smile.classification/ada-boost}
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

