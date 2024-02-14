(ns automl)
(require 'ml-basic)

;; # AutoML using metamorph pipelines
;;  In this tutorial we see how to use `metamorph.ml` to perform automatic machine learning.
;;  With AutoML we mean to try lots of different models and hyper parameters and rely on automatic
;;  validation to pick the best performing model automatically
;;
;;  we will use the ready-for-modeling data from basic-ml tutorial
(def titanic ml-basic/numeric-titanic-data)

;; ## The metamorph pipeline abstraction

;; While before we saw how to use the pair of `train` and `predict` to
;; perform machine learning, AutoML requires us to use an other
;; abstraction, in order to encapsulate both train and predict in single function.
;; (or other any operation)

;;  We will use the concept of a "metamorph pipeline", which is a
;;  sequence of specific functions,
;;  and each function can behaves differently, depending on the "mode"
;;  in which the pipelines get run.
;;  It can run either in mode :fit or in mode :transform, and the
;;  functions of the pipeline can do
;;  different things depend on the `mode`
;;
;; Specifically we have a function called `metamorph.ml/model` which
;; will do 'train` in model
;; :fit and `predict` in mode :transform
;;
;; The names :fit and :transform come from the fact that functions
;; could do other things then
;; `train` and `predict`.

(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.metamorph.core :as mm])
;;
;;

;; The following code will do the same as `train`, but return a
;; context object, which contains the trained model.
;;
;; The ctx acts a a collector of everything "learned" during :fit,
;; mainly the trained model, but it could be as well other information
;; learned from the data.

(def train-ctx
  (mm/fit titanic
          (ml/model {:model-type :metamorph.ml/dummy-classifier})))

;; (The dummy model does not have state....)

train-ctx
(defn transform-pipe [ctx data pipe-fn]
  (mm/transform-pipe data pipe-fn ctx))

;;  Lets prepare data for some examples.
(require '[tablecloth.api :as tc])

(def splits (first (tc/split->seq titanic)))
(def train-ds (:train splits))
(def test-ds (:test splits))


;; To show the power of pipelines, I start with doing the simplest possible pipeline,
;; and expand then on it.

;;  we can already chain train and test with usual functions:
(->>
 (ml/train train-ds {:model-type :metamorph.ml/dummy-classifier})
 (ml/predict test-ds)
 :survived)

;;  the same with pipelines
(def pipeline
  (mm/pipeline (ml/model {:model-type :metamorph.ml/dummy-classifier})))
(->>
 (mm/fit-pipe train-ds pipeline)
 (mm/transform-pipe test-ds pipeline)
 :metamorph/data :survived)


;;  but know we can add more operations to the pipeline,
;;  and nothing else changes, for example drop columns.
(def pipeline-2
  (mm/pipeline
   (mm/lift tc/drop-columns [:embarked])
   (ml/model {:model-type :metamorph.ml/dummy-classifier})))
(->>
 (mm/fit-pipe train-ds pipeline-2)
 (mm/transform-pipe test-ds pipeline-2)
 :metamorph/data :survived)

;; The 'lift` function transposes a dataset->dataset function into a ctx->ctx function, as required for
;; metamorph pipeline operations
;;
;; So a metamorph pipeline can encapsulate arbitray transformation
;; of a dataset in the 2 modes. They can be "stateless"
;; (only chaining the dataset, such as `drop-columns`) or
;; "state-full", so they store data in the ctx during :fit and can use
;; it in :transform. In the pipeline above, the trained model is
;; stored in this way.
;;
;;This makes pipeline execution "isolated"
;;
;; For convenience `tablecloth` contains a ns where all functions
;; are lifted into ctx->ctx operations, so can be added to pipelines
;; directly.
(require 'tablecloth.pipeline)
(def pipeline-3
  (mm/pipeline
   (tablecloth.pipeline/drop-columns [:embarked])
   (ml/model {:model-type :metamorph.ml/dummy-classifier})))
(->>
 (mm/fit-pipe train-ds pipeline-3)
 (mm/transform-pipe test-ds pipeline-3)
 :metamorph/data :survived)

;;  It supports as well "pipelines as data"
(def pipeline-4
  (mm/->pipeline
   [[tablecloth.pipeline/drop-columns [:embarked]]]
   [[ml/model {:model-type :metamorph.ml/dummy-classifier}]]))
(->>
 (mm/fit-pipe train-ds pipeline-4)
 (mm/transform-pipe test-ds pipeline-4)
 :metamorph/data :survived)

;; The auto ml support in metamorph consists now in the possibility
;; to create an arbitrary number of different pipelines
;; and combine them with arbitray test/train data splits

;;  helper for later
(defn make-results-ds [evaluation-results]
  (->> evaluation-results
       flatten
       (map #(hash-map :options (-> % :test-transform :ctx :model :options)
                       :used-features (-> % :fit-ctx :used-features)
                       :mean-accuracy (-> % :test-transform :mean)))
       tc/dataset))


(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.metamorph.ml.loss :as loss]
         '[scicloj.metamorph.core :as mm]

         '[tablecloth.api :as tc])

;; ## Finding best model automatcally
;;  This advantage of the pipelines is even more visible,
;;  if we want to have configurable pipelines,
;;  and do a grid search to find optimal settings

;;  the following will find teh best model across:
;;  5 different model classes
;;  6 different selections of :used features
;;  k-cross validate this with different test / train splits
(defn make-pipe-fn [model-type features]
  (mm/pipeline
   (fn [ctx]
     (assoc ctx :used-features features))
   (mm/lift tc/select-columns (conj features :survived))
   {:metamorph/id :model} (ml/model {:model-type model-type})))

(def titanic-k-fold (tc/split->seq ml-basic/numeric-titanic-data :kfold {:seed 12345}))
(def model-types [:metamorph.ml/dummy-classifier
                  :smile.classification/random-forest
                  :smile.classification/logistic-regression
                  :smile.classification/decision-tree
                  :smile.classification/ada-boost])

(def feature-combinations
  [[:sex :pclass :embarked]
   [:sex]
   [:pclass :embarked]
   [:embarked]
   [:sex :embarked]
   [:sex :pclass]])

;; generate 30 pipeline functions
(def pipe-fns
  (for [model-type model-types
        feature-combination feature-combinations]
    (make-pipe-fn model-type feature-combination)))

;; Go over all pipefn * cross-validations
;; and return best model

(def evaluation-results
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy))
   


;; by default it returns the best mode only
(make-results-ds evaluation-results)

;;  but we can get all results as well:
(def evaluation-results-all
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy
   {:return-best-crossvalidation-only false
    :return-best-pipeline-only false}))


;; In total it creates and evaluates 5 models * 6 model configurations * 5 CV = 150 models
(->  evaluation-results-all flatten count)

;;  We can find the best as well by hand, its the first from list.
(-> (make-results-ds evaluation-results-all)
    (tc/unique-by)
    (tc/order-by [:mean-accuracy] :desc))
