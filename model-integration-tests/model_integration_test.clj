(ns model-integration-test
  "This ns has integration tests that covers:

  1. Performance assurance by validating models meet minimum accuracy thresholds
  2. Regression validation to ensure preventing models' performance degradation
  "
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [libpython-clj2.python :as py]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.loss :as loss]
   [scicloj.metamorph.ml.rdatasets :as rdata]
   [tablecloth.api :as tc]
   [taoensso.nippy :as nippy]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.categorical :as ds-cat]
   [tech.v3.dataset.modelling :as ds-mod]
   [scicloj.ml.tribuo])
  (:import
   (java.io File)
   [org.tribuo.classification.libsvm SVMClassificationType$SVMMode]
   [org.slf4j.bridge SLF4JBridgeHandler]
   (smile.base.mlp
    ActivationFunction
    Cost
    HiddenLayerBuilder
    OutputFunction
    OutputLayerBuilder)))

;; Initialize Python
(py/initialize!)
(py/run-simple-string "
import warnings
warnings.simplefilter('ignore')")

;; Setup a Logger
(SLF4JBridgeHandler/removeHandlersForRootLogger)
(SLF4JBridgeHandler/install)

(def mlp-hidden-layer-builder
  (HiddenLayerBuilder. 1 (ActivationFunction/linear)))

(def mlp-output-layer-builder
  (OutputLayerBuilder. 3  OutputFunction/LINEAR  Cost/MEAN_SQUARED_ERROR))

;; Register models to test
(require '[scicloj.metamorph.ml.classification]
         '[scicloj.metamorph.ml.regression]

         ;;note: `macosx-arm64` not supported for current smile models
         '[scicloj.ml.smile.classification]
         '[scicloj.ml.smile.regression]

         '[scicloj.ml.tribuo]
         '[scicloj.sklearn-clj.ml]
         '[scicloj.ml.xgboost])

;; Sets of excluding models that are not testable
(def not-writable--or-readable-with-nippy
  #{:smile.classification/mlp  ;;https://github.com/scicloj/scicloj.ml.smile/issues/18
    :metamorph.ml/ols
    :fastmath/ols})

(def not-working-with-iris-data-or-default-params-or-no-probab
  #{:smile.classification/sparse-svm
    :smile.classification/maxent-binomial
    :smile.classification/maxent-multinomial
    :smile.classification/mlp
    :smile.classification/svm
    :smile.classification/sparse-logistic-regression
    :smile.classification/discrete-naive-bayes
    

    ;;https://github.com/scicloj/sklearn-clj/issues/13
    :sklearn.classification/ridge-classifier-cv
    :sklearn.classification/linear-svc
    :sklearn.classification/passive-aggressive-classifier
    :sklearn.classification/nearest-centroid
    :sklearn.classification/ridge-classifier
    :sklearn.classification/bernoulli-nb
    :sklearn.classification/perceptron
    :sklearn.classification/sgd-classifier
    :sklearn.classification/svc
    :sklearn.classification/nu-svc
    :sklearn.classification/self-training-classifier
    :sklearn.classification/logistic-regression-cv
    })



 ;; Minimum accuracies for models to validate in tests
 (def min-accuracies-by-model
   {:smile.classification/linear-discriminant-analysis 0.82
    :smile.classification/gradient-tree-boost 0.92

    :sklearn.classification/complement-nb 0.65
    :sklearn.classification/ridge-classifier 0.30
    :sklearn.classification/extra-tree-classifier 0.88
    :sklearn.classification/dummy-classifier 0.2

    :metamorph.ml/dummy-classifier 0.2})

 ;; Specify model specs
 (def smile-model-specs
   (map
    #(vector (get min-accuracies-by-model % 0.93)
             {:model-type %})
    (->> (ml/model-definition-names)
         (filter #(and (str/starts-with? (namespace %) "smile.classification")
                       (not (not-working-with-iris-data-or-default-params-or-no-probab %))))
         set)))

 (def sklearn-model-specs
   (map
    #(vector (get min-accuracies-by-model % 0.90)
             {:model-type %})
    (->> (ml/model-definition-names)
         (filter #(and (str/starts-with? (namespace %) "sklearn.classification")
                       (not (not-working-with-iris-data-or-default-params-or-no-probab %))))
         set)))

 (def tribuo-model-specs [[0.95 {:model-type :scicloj.ml.tribuo/classification
                                 :tribuo-components [{:name "logistic"
                                                      :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"
                                                      :properties {:seed "1234"
                                                                   :shuffle "false"
                                                                   :epochs "10"}}
                                                     {:name "ada"
                                                      :type "org.tribuo.classification.ensemble.AdaBoostTrainer"
                                                      :properties {:innerTrainer "logistic"
                                                                   :numMembers "5"
                                                                   :seed "1234"}}]

                                 :tribuo-trainer-name "ada"}]

                          [0.93 {:model-type :scicloj.ml.tribuo/classification
                                 :tribuo-components [{:name "logistic"
                                                      :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"
                                                      :properties {:seed "1234"
                                                                   :shuffle "false"
                                                                   :epochs "10"}}]
                                 :tribuo-trainer-name "logistic"}]
                          [0.93 {:model-type :scicloj.ml.tribuo/classification
                                 :tribuo-components [{:name "liblinear"
                                                      :type "org.tribuo.classification.liblinear.LibLinearClassificationTrainer"
                                                      :properties {:seed "1234"}}]
                                 :tribuo-trainer-name "liblinear"}]

                          [0.93 {:model-type :scicloj.ml.tribuo/classification
                                 :tribuo-components [{:name "C_SVC"
                                                      :type "org.tribuo.classification.libsvm.SVMClassificationType"
                                                      :properties {:type "C_SVC"}}

                                                     {:name "libsvm"
                                                      :type "org.tribuo.classification.libsvm.LibSVMClassificationTrainer"
                                                      :properties {:seed "1234"
                                                                   :svmType "C_SVC"}}]
                                 :tribuo-trainer-name "libsvm"}]



                          [0.93 {:model-type :scicloj.ml.tribuo/classification
                                 :tribuo-components [{:name "random-forest"
                                                      :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                                                      :properties {:maxDepth "8"
                                                                   :useRandomSplitPoints "false"
                                                                   :fractionFeaturesInSplit "0.5"}}]
                                 :tribuo-trainer-name "random-forest"}]])
 (def xgboost-specs [[0.94 {:model-type :xgboost/classification
                            :num-class 3}]])

 (def other-specs [[0.28 {:model-type :smile.classification/mlp
                          :layer-builders [mlp-hidden-layer-builder mlp-output-layer-builder]}]
                   [0.2 {:model-type :metamorph.ml/dummy-classifier}]
                   [0.95 {:model-type :sklearn.classification/svc
                          :probability true}]
                   ])

 
 ;; Consolidate all the specs into a collection to be used in tests
 (def model-specs
   (concat
    xgboost-specs
    other-specs
    tribuo-model-specs
    smile-model-specs
    sklearn-model-specs))

 ;; Test utils for accuracy checks
 (defn- get-classification-accuracy
   "Calculate classification accuracy:

  accuracy = correct-prediction-count / total-prediction-count"
   [lhs rhs]
   (loss/classification-accuracy lhs rhs))

 (defn- assert-nippy-round-trip
   "Assert a model's serialization/deserialization preserves prediction accuracy"
   [{:keys [model-type] :as model-spec} result dataset]
   (let [;; create a temp file to put model in
         tmp-file     (File/createTempFile
                       (format "model-%s-" model-type)
                       ".nippy")

         ;; serialize the model file
         _            (nippy/freeze-to-file
                       tmp-file
                       (->> result ffirst :fit-ctx :model))

         ;; deserialize the model file
         new-model    (nippy/thaw-from-file tmp-file)

         new-prediction
         (-> dataset
             (ml/predict new-model)
             (ds-cat/reverse-map-categorical-xforms)
             :species)

         new-truth
         (-> dataset
             ds-cat/reverse-map-categorical-xforms
             :species)

         new-accuracy
         (loss/classification-accuracy
          new-prediction
          new-truth)

         min-accuracy (get min-accuracies-by-model model-type 0.7)]
     (is (< min-accuracy new-accuracy)
         (format "min accuracy (%s) validation failed for: %s"
                 min-accuracy
                 model-spec))))

 (defn- train-and-validate-model
   "Train a model and validate its performance using k-fold cross-validation"
   [{:keys [model-type] :as model-spec} dataset]
   (println :verify-accuracy model-type)
   (let [train-test-split
         (tc/split->seq dataset :kfold {:seed 1234 :k 10})

         pipe
         (mm/pipeline
          {:metamorph/id :model}
          (ml/model model-spec))

         result
         (ml/evaluate-pipelines
          [pipe]
          train-test-split
          get-classification-accuracy
          :accuracy)]

     ;; validate a model's accuracy after serial/deserialization
     (when-not (contains? not-writable--or-readable-with-nippy model-type)
       (assert-nippy-round-trip model-spec result dataset))

     ;; get accuracy of the model
     (-> result ffirst :test-transform :mean)))

 (defn- remove-model-type [model-specs type]
   (remove
    #(= type
        (-> % second :model-type))
    model-specs))

 (defn- assert-accuracy
   "Check whether a model's accuracy surpasses minimum expectations"
   [[expected-accuracy {:keys [model-type] :as model-spec}] dataset]
   (try
     (let [accuracy (train-and-validate-model model-spec dataset)]
       (println :min-accuracy expected-accuracy :accuracy accuracy)
       (is
        (>= accuracy
            expected-accuracy)

        (format "model type %s expects accuracy at least of %s, but found %s"
                model-type
                expected-accuracy accuracy)))

     (catch Exception e
       (is false
           (format "Exception: %s" (.toString e))))))

 ;; Define our test dataset
 (def iris-dataset (-> (rdata/datasets-iris)
                       (tc/drop-columns :rownames)
                       (ds-mod/set-inference-target :species)
                       (ds/categorical->number [:species] {} :int16)))

 ;; ==========================
 ;; TEST1: Accuracy Validation
 ;; ==========================

 (deftest verify-classification-iris-int-catmap
   (run!
    #(assert-accuracy % iris-dataset)
    model-specs))

 (deftest verify-classification-iris-float-catmap
   (let [iris
         (->
          iris-dataset
          ds-cat/reverse-map-categorical-xforms
          (ds/categorical->number [:species] {} :float64))]
     (run!
      #(assert-accuracy % iris)
      model-specs)))

 (deftest verify-classification-iris-no-catmap
   (let [iris
         (ds-cat/reverse-map-categorical-xforms
          iris-dataset)]
     (run!
      #(assert-accuracy % iris)
      ;; only tribuo can deal with "string" target column
      ;;https://github.com/scicloj/noj/issues/36
      (concat
      ;other-specs
      ; smile-model-specs https://github.com/scicloj/scicloj.ml.smile/issues/25
       ;xgboost-specs https://github.com/scicloj/scicloj.ml.xgboost/issues/9
       ;sklearn-model-specs https://github.com/scicloj/sklearn-clj/issues/15
       tribuo-model-specs))))
      
      ;

 (deftest verify-classification-iris-nil-catmap-int
   (let [iris
         (ds/assoc-metadata iris-dataset [:species] :categorical-map nil)]
     (run!
      #(assert-accuracy % iris)

      (-> model-specs
          ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
          (remove-model-type  :smile.classification/mlp)))))

 (deftest verify-classification-iris-nil-catmap-float
   (let [iris
         (->
          iris-dataset
          (ds-cat/reverse-map-categorical-xforms)
          (ds/categorical->number [:species] {} :float64)
          (ds/assoc-metadata [:species] :categorical-map nil))]
     (run!
      #(assert-accuracy % iris)
      (-> model-specs
          ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
          (remove-model-type  :smile.classification/mlp)))))

 (comment
   (verify-classification-iris-int-catmap)
   (verify-classification-iris-float-catmap)
   (verify-classification-iris-no-catmap)
   (verify-classification-iris-nil-catmap-int)
   (verify-classification-iris-nil-catmap-float))

 ;; Test utils for regression checks
 (def iris-dataset-regression
   (->
    iris-dataset
    (tc/drop-columns [:species])
    (ds-mod/set-inference-target :sepal-length)))

 (def first-split-iris-dataset-regression
   (first
    (tc/split->seq
     iris-dataset-regression
     :holdout
     {:seed 12345})))

 (def iris-ds-regression--train
   (:train first-split-iris-dataset-regression))

 (def iris-ds-regression--test
   (:test first-split-iris-dataset-regression))

 (defn- assert-mae
   "Check whether a model's mean absolute error(MAE) falls below maximum expectation"
   [model model-map]
   (let [max-mae 0.4
         mae
         (loss/mae
          (-> iris-ds-regression--test :sepal-length)
          (-> (ml/predict iris-ds-regression--test model) :sepal-length))]
     (println :max-mean-absolute-error max-mae :mean-absolute-error mae)

     (is (> max-mae mae)
         (format "mae validation failed: %s" model-map))))

 (defn- validate-regression
   "Re-train a model and validate its mean absolute error thresholds"
   [{:keys [model-type] :as model-map}]
   (println :validate-regression :model-type model-type)
   (let [model
         (ml/train
          iris-ds-regression--train
          model-map)]

     (when-not (contains? not-writable--or-readable-with-nippy model-type)
       (let [frozen (nippy/freeze-to-string model)
             unfrozen-model (nippy/thaw-from-string frozen)]
         (assert-mae unfrozen-model model-map)))

     (assert-mae model model-map)))

 ;; ============================
 ;; TEST2: Regression Validation
 ;; ============================

 (deftest regression-works
   (run!
    #(validate-regression {:model-type %})
    [:metamorph.ml/ols
     :fastmath/ols
     :smile.regression/ordinary-least-square
     :smile.regression/elastic-net
     :smile.regression/lasso
     :smile.regression/ridge
     :smile.regression/gradient-tree-boost
     :smile.regression/random-forest
     :xgboost/linear-regression
     :xgboost/regression
     :sklearn.regression/linear-regression
     :sklearn.regression/decision-tree-regressor
     :sklearn.regression/random-forest-regressor]))

 (deftest tribuo-regression-works
   (run!
    #(validate-regression
      {:model-type :scicloj.ml.tribuo/regression
       :tribuo-trainer-name "reg"
       :tribuo-components %})
    [[{:name "loss"
       :type "org.tribuo.regression.sgd.objectives.SquaredLoss"}
      {:name "reg"
       :type "org.tribuo.regression.sgd.linear.LinearSGDTrainer"
       :properties {:objective "loss"
                    :epochs "20"}}]

     [{:name "reg"
       :type "org.tribuo.regression.rtree.CARTRegressionTrainer"}]

     [{:name "reg"
       :type "org.tribuo.regression.xgboost.XGBoostRegressionTrainer"
       :properties {:numTrees "10"}}]

     [{:name "nu"
       :type "org.tribuo.regression.libsvm.SVMRegressionType"
       :properties {:type "NU_SVR"}}
      {:name "reg"
       :type "org.tribuo.regression.libsvm.LibSVMRegressionTrainer"
       :properties {:svmType "nu"}}]]))

 (comment
   (regression-works)
   (tribuo-regression-works))
