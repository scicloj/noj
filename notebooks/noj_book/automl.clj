;; # AutoML using metamorph pipelines

;;  In this tutorial we see how to use `metamorph.ml` to perform automatic machine learning.
;;  With AutoML we mean to try lots of different models and hyper parameters and rely on automatic
;;  validation to pick the best performing model automatically.
;;

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)

(ns noj-book.automl
  (:require [noj-book.ml-basic :as ml-basic]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.metamorph.ml :as ml]
            [tablecloth.api :as tc]
            [scicloj.metamorph.ml.loss :as loss]
            [scicloj.metamorph.core :as mm]
            [scicloj.metamorph.ml.gridsearch :as gs]
            [tech.v3.dataset.modelling :as ds-mod]))

;; ## The metamorph pipeline abstraction
;; When using automl, it is very useful to be able to manage all
;; the steps of a machine learning pipeline (including data
;; transformations and modeling) as a unified function that can be
;; freely moved around.
;; This cannot work with a threading macro, as this executes immediate.
;;
;; The Clojure way to do this, is function composing and higher level
;; functions.
;;
;; (The following is a quick explanation of `metamorph`, 
;; see chapter "Machine learning pipelines" for more details.
;;
;; While in the basic tutorial we saw how to use the pair of `train` 
;; and `predict` to
;; perform machine learning, AutoML requires us to use an other
;; abstraction, in order to encapsulate both train and predict in a
;; single function.(or other any operation)
;;
;;  We will use the concept of a "metamorph pipeline", which is a
;;  sequence of specific functions,
;;  and each function can behaves differently, depending on the "mode"
;;  in which the pipelines get run.
;;  It can run either in mode `:fit` or in mode `:transform`, and the
;;  functions of the pipeline can (but don't need to) do
;;  different things depend on the `mode`
;;
;; ### metamorph.ml/model
;; Specifically we have a function called `metamorph.ml/model` which
;; will do `train` in mode
;; `:fit` and `predict` in mode `:transform`
;;
;; The names `:fit` and `:transform` come from the fact that the functions
;; could do other things then  `train` and `predict`, 
;; so `:fit` and `:transform` represent a
;; more general concept then train/predict.

(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.metamorph.core :as mm]
         '[tablecloth.api :as tc])

;;  We will use the ready-for-modeling data from basic-ml tutorial,
;;
(def titanic ml-basic/numeric-titanic-data)

;;  ### Split the data

;;  so lets create splits of the data first:

(def splits (first (tc/split->seq titanic)))
(def train-ds (:train splits))
(def test-ds (:test splits))

;; ### Create pipeline

;; In its foundation a metamorph pipeline is a sequential composition of
;; functions,
;; which **all** take a map as only parameter, the so called context,
;; and they return an other context, changed by the functions.
;; The composed function , hence the pipeline overall, has this same property.
;; Any other function parameters are closed over on function creation.
;; The following creates such a composed function out of other metamorph compliant operations.
;; The overall result of the pipeline function, is the result of the last operation.
;; (in this case we have only '1' operation)
;;
;; In nearly all cases, the last pipeline operation is `ml/model` .
;; But this is not absolutely required.
(def my-pipeline
  (mm/pipeline
   (ml/model {:model-type :metamorph.ml/dummy-classifier})))
;; as we see, this is a function itself
my-pipeline

;; This function is metamorph compliant, so it takes a map
;; (my-pipeline {}) and returns a map.
;;
;; But this map cannot be "arbitrary", it need to adhere to the `metamorph` conventions.
;;
;; ### run pipeline = train model
;;
;; The following `trains` a model, because the `ml/model`
;; function does this when called with `:mode` `:fit`.
;; And it is the only operation in the pipeline, so the pipeline does one
;; thing, it `trains a model`
(def ctx-after-train
  (my-pipeline {:metamorph/data train-ds
                :metamorph/mode :fit}))
ctx-after-train

;; The ctx contains lots of information, so I only show its top level keys
(keys ctx-after-train)
;; This context map has the "data", the "mode" and an UUID for each operation
;; (we had only one in this pipeline)
;;
(vals ctx-after-train)

;; The `model` function has closed over the id, so is knows "its id", so in the
;; `transform`  mode it can get the data created at `:fit`.  So the `model` 
;; function can "send" data to itself from `:fit` to `:transform`, 
;; the `trained model`.
;;
;; So this will do the `predict` on new data:

(def ctx-after-predict
  (my-pipeline (assoc ctx-after-train
                      :metamorph/mode :transform
                      :metamorph/data test-ds)))
(keys ctx-after-predict)
;; For the dummy-model we do not see a `trained-model`,
;; but it "communicates" the majority class from the train data
;; to use it for prediction. So the `dummy-model`
;; has 'learned' the majority class from its training data.
;;
;;  So we can get prediction result out of the ctx:
(-> ctx-after-predict :metamorph/data :survived)



;; This works as long as all operations of the pipeline follow the
;; metamorph convention
;; (we can create such compliant functions, out of
;; normal dataset->dataset functions, as we will see)
;;
;; `my-pipeline` represents therefore a not yet executed model
;; training / prediction flow.
;; It can be freely moved around and applied to  datasets when needed.
;;
;; ## Use metamorph pipelines to do model training with higher level API

;; As user of `metamorph.ml` we do not need to deal with this low-level details
;; of how `metamorph` works, we have convenience functions which hide this.
;;
;; The following code will do the same as `train`, but return a
;; context object, which contains the trained model,
;; so it will execute the pipeline, and not only create it.
;;
;; It uses a convenience function `mm/fit` which generates compliant
;; context maps internally and executes the pipeline as well.
;;
;; The ctx acts a collector of everything "learned" during `:fit`,
;; mainly the trained model, but it could be as well other information
;; learned from the data during `:fit` and to be applied at `:transform` .

(def train-ctx
  (mm/fit titanic
          (ml/model {:model-type :metamorph.ml/dummy-classifier})))

;; (The dummy-classifier model does not have a lot of state,
;; so there is little to see)

(keys train-ctx)


;; To show the power of pipelines, I start with doing the simplest possible pipeline,
;; and expand then on it.

;;  We can already chain train and test with usual functions:
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



;; ## Create metamorph compliant functions
;; As said before, a metamorph pipeline is composed of `metamorph`
;; compliant functions / operations, which take as input and output
;; the ctx.
;; There are three ways to create those.
;;
;; The following three expressions create the same
;; metamorph compliant function
;;
;; 1. implementing a metamorph compliant function directly via anonymous
;; function

(def ops-1
  (fn [ctx]
    (assoc ctx :metamorph/data
           (tc/drop-columns (:metamorph/data ctx) [:embarked]))))

;;  2. using `mm/lift` which does the same as 1.
(def ops-2 (mm/lift tc/drop-columns [:embarked]))

;;  3. using a name-space containing lifted functions
(require '[tablecloth.pipeline])
(def ops-3 (tablecloth.pipeline/drop-columns [:embarked]))

;;  All three create the same pipeline op
;;  and can be used to make a pipeline
(mm/pipeline ops-1)
(mm/pipeline ops-2)
(mm/pipeline ops-3)

;; All three can be called as function taking a dataset wrapped in a ctx map.

;; Pipeline as data is as well supported:
(def op-spec [[ml/model {:model-type :metamorph.ml/dummy-classifier}]])
;;
(mm/->pipeline op-spec)

;; Creating these functions does not yet execute anything, they are
;; functions which can be executed against a context as part of
;; a metamorph pipeline.
;; Executions are triggered like this:

(ops-1 {:metamorph/data titanic})
(ops-2 {:metamorph/data titanic})
(ops-3 {:metamorph/data titanic})


;;
;; The `mm/lift` function transforms any dataset->dataset function
;; into a ctx->ctx function,
;; while using the `metamorph` convention, as required for metamorph
;; pipeline operations
;;
;; For convenience `tablecloth` contains a ns where all `dataset->dataset` functions
;; are lifted into ctx->ctx operations, so can be added to pipelines
;; directly without using `lift`.

;;
;; So a metamorph pipeline can encapsulate arbitrary transformation
;; of a dataset in the 2 modes. They can be "stateless"
;; (only chaining the dataset, such as `drop-columns`) or
;; "state-full", so they store data in the ctx during `:fit` and can use
;; it in `:transform`. In the pipeline above, the trained model is
;; stored in this way.
;;
;;This state is not stored globally, but inside the pipeline
;;so this makes pipeline execution "isolated".
;;
;;


;;  So now we can add more operations to the pipeline,
;;  and nothing else changes, for example drop columns.

;; While most metamorph compliant operations behave the same in  
;; :fit and :transform, there are some which do behave differently.
;; They have a certain notion of "fit" and "transform".
;;
;; They are therefore called "transformer" and are listed in the 
;; "Transformer reference" 
;; at the end of the Noj book.
;;
;; Some transformers exist as well as model and can be used with
;; function `ml/model`


;; ## Automatic ML with `metamorph.ml`
;;
;; The AutoML support in metamorph.ml consists now in the possibility
;; to create an arbitrary number of different pipelines
;; and have them run against arbitrary test/train data splits
;; and it automatically chooses the best model evaluated by a
;; user provided metric function.

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
         '[scicloj.ml.tribuo]
         '[scicloj.ml.xgboost]
         '[scicloj.ml.smile.classification]
         '[scicloj.sklearn-clj.ml])


;; ## Finding the best model automatically

;;  The advantage of the pipelines is even more visible,
;;  if we want to have configurable pipelines,
;;  and do a grid search to find optimal settings.

;;  the following will find the best model across:
;;
;;  * 4 different model classes with different hyper params
;;
;;  * 6 different selections of used features
;;
;;  * k-cross validate this with different test / train splits
;;

(defn make-pipe-fn [model-spec features]
  (mm/pipeline
   ;; store the used features in ctx, so we can retrieve them at the end
   (fn [ctx]
     (assoc ctx :used-features features))
   (mm/lift tc/select-columns (conj features :survived))
   {:metamorph/id :model} (ml/model model-spec)))

;;  Create a 5-K cross validation split of the data:
(def titanic-k-fold (tc/split->seq ml-basic/numeric-titanic-data :kfold {:seed 12345}))

(-> titanic-k-fold count)

;; We add as well 10 hyper-parameter variants for logistic regression
;; obtained via Sobol search over the hyper parameter space of the model.
(def hyper-params 
  (->>
   (ml/hyperparameters :smile.classification/logistic-regression)
   (gs/sobol-gridsearch)
   (take 10)))
hyper-params

(def logistic-regression-specs
  (map
   #(assoc %
           :model-type :smile.classification/logistic-regression)
   hyper-params))
logistic-regression-specs


;; The list of the model types we want to try:
(def models-specs 
  (concat logistic-regression-specs
          [{:model-type :scicloj.ml.tribuo/classification
            :tribuo-components [{:name "cart"
                                 :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                                 :properties {:maxDepth "8"
                                              :useRandomSplitPoints "false"
                                              :fractionFeaturesInSplit "0.5"}}
                                {:name "combiner"
                                 :type "org.tribuo.classification.ensemble.VotingCombiner"}
           
                                {:name "random-forest"
                                 :type "org.tribuo.common.tree.RandomForestTrainer"
                                 :properties {:innerTrainer "cart"
                                              :combiner "combiner"
                                              :seed "1234"
                                              :numMembers "500"}}]
            :tribuo-trainer-name "random-forest"}
           
           {:model-type :xgboost/classification :round 10}
           {:model-type :sklearn.classification/decision-tree-classifier}
           {:model-type :sklearn.classification/logistic-regression}
           {:model-type :sklearn.classification/random-forest-classifier}
           {:model-type :metamorph.ml/dummy-classifier}
           {:model-type :scicloj.ml.tribuo/classification
            :tribuo-components [{:name "logistic"
                                 :type "org.tribuo.classification.sgd.linear.LogisticRegressionTrainer"}]
            :tribuo-trainer-name "logistic"}
           
           ]))


;;  This uses models from Smile, Tribuo and sklearn but could be any
;;  metamorph.ml compliant model 

;;  The list of feature combinations to try for each model:
(def feature-combinations
  [[:sex :pclass :embarked]
   [:sex]
   [:pclass :embarked]
   [:embarked]
   [:sex :embarked]
   [:sex :pclass]])

;; generate 102 pipeline functions:
(def pipe-fns
  (for [model-spec models-specs
        feature-combination feature-combinations]
    (make-pipe-fn model-spec feature-combination)))

(count pipe-fns)
;; Execute all pipelines for all splits in the  cross-validations
;; and return best model by `classification-accuracy`

(add-tap println)
(def evaluation-results
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy))



;; By default it returns the best mode only
(make-results-ds evaluation-results)

;; The key observation is here, that the `metamorph` pipelines
;; allow to not only grid-search over the model hyper-parameters,
;; but as well over arbitrary `pipeline variations`,
;; like which features to include.
;; Both get handled in the same way.


;;  We can get all results as well:
(def evaluation-results-all
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy
   {:map-fn :map
    :return-best-crossvalidation-only false
    :return-best-pipeline-only false}))


;; In total it creates and evaluates
;; 17 models (incl. hyper parameters variations) * 6 feature configurations * 5 CV = 510 models
(->  evaluation-results-all flatten count)

;;  We can find the best as well by hand, it's the first from the list,
;;  when sorted by accuracy.
(-> (make-results-ds evaluation-results-all)
    (tc/unique-by)
    (tc/order-by [:mean-accuracy] :desc)
    (tc/head 20)
    (kind/dataset))


;; ## Best practices for data transformation steps in or outside pipeline
;;
(require '[scicloj.metamorph.ml.toydata :as data]
         '[tech.v3.dataset.modelling :as ds-mod]
         '[tech.v3.dataset.categorical :as ds-cat]
         '[tech.v3.dataset :as ds])
;;
;;  We have seen that we have two ways to transform the input
;;  data, outside the pipeline and inside the pipeline.
;;
;;  These are the total steps from raw data to "into the model"
;;  for the titanic use case.

;;  1. raw data
(def titanic
  (:train
   (data/titanic-ds-split)))

;;  2. first transformation, no metamorph pipeline
(def relevant-titanic-data
  (-> titanic
      (tc/select-columns (conj ml-basic/categorical-feature-columns :survived))
      (tc/drop-missing)
      (ds/categorical->number [:sex :pclass :embarked] [0 1 2 "male" "female" "S" "Q" "C"] :float64)
      (ds/categorical->number [:survived] [0 1] :float64)
      (ds-mod/set-inference-target :survived)))

;; 3. transform via pipelines
(defn make-pipe-fn [model-type features]
  (mm/pipeline
   ;; store the used features in ctx, so we can retrieve them at the end
   (fn [ctx]
     (assoc ctx :used-features features))
   (mm/lift tc/select-columns (conj features :survived))
   {:metamorph/id :model} (ml/model {:model-type model-type})))


;;  While it would be technically possible to move all steps from
;;  the "first transformation"
;;  into the pipeline, by just using the "lifted" form of the transformations,
;;  I would not do so, even though this should give the same result.
;;
;; Often it is better to separate the steps which are "fixed",
;; from the steps which are parameterized, so for which we want to find
;; the best values by "trying out".
;;
;; In my view there are two reasons for this:
;; * Debugging: It is harder to debug a pipeline and see the results
;;   of steps. We have one macro helping in this: `mm/def-ctx`
;; * Performance: The pipeline is executed lots of times, for every split / variant
;;    of the pipeline. It should be faster to do data transformations only once, 
;;    before the metamorph pipeline starts.
;; 
;; Nevertheless in some scenarios it is very useful to create a full 
;; transformation pipeline as a metamorph pipeline. 
;; This would for example allow to perform very different transformation steps per 
;; model and still only have a single seq of pipeline functions to manage,
;; therefore having fully self contained pipelines.
