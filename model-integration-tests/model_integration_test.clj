(ns model-integration-test
  (:require
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [libpython-clj2.python :as py]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.loss :as loss]
   [scicloj.metamorph.ml.toydata :as data]
   [tablecloth.api :as tc]
   [taoensso.nippy :as nippy]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.categorical :as ds-cat]
   [tech.v3.dataset.modelling :as ds-mod]
   [scicloj.ml.tribuo]
   )
  (:import
   [org.tribuo.classification.libsvm SVMClassificationType$SVMMode]
   [org.slf4j.bridge SLF4JBridgeHandler]
   (smile.base.mlp
    ActivationFunction
    Cost
    HiddenLayerBuilder
    OutputFunction
    OutputLayerBuilder)))

(py/initialize!)
(py/run-simple-string "
import warnings
warnings.simplefilter('ignore')")
(SLF4JBridgeHandler/removeHandlersForRootLogger) 
(SLF4JBridgeHandler/install)


(def mlp-hidden-layer-builder
  (HiddenLayerBuilder. 1 (ActivationFunction/linear)))

(def mlp-output-layer-builder
  (OutputLayerBuilder. 3  OutputFunction/LINEAR  Cost/MEAN_SQUARED_ERROR))


(require '[scicloj.metamorph.ml.classification]
         '[scicloj.metamorph.ml.regression]
         '[scicloj.ml.smile.classification]
         '[scicloj.ml.smile.regression]
         
         '[scicloj.ml.tribuo]
         '[scicloj.sklearn-clj.ml]
         '[scicloj.ml.xgboost])

(def not-writable--or-readable-with-nippy
  
  #{:smile.classification/mlp  ;;https://github.com/scicloj/scicloj.ml.smile/issues/18
    :scicloj.ml.tribuo/classification ;;https://github.com/scicloj/scicloj.ml.tribuo/issues/5
    :metamorph.ml/ols
    :fastmath/ols


    })

(def not-working-with-iris-data-or-default-params-or-no-probab
  #{:smile.classification/sparse-svm
    :smile.classification/maxent-binomial
    :smile.classification/maxent-multinomial
    :smile.classification/mlp
    :smile.classification/svm
    :smile.classification/sparse-logistic-regression
    :smile.classification/discrete-naive-bayes
    :smile.classification/knn ;; check

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
    :sklearn.classification/nu-svc})

(def min-accuracies
  {:smile.classification/linear-discriminant-analysis 0.82
   :smile.classification/gradient-tree-boost 0.92

   :sklearn.classification/complement-nb 0.65
   :sklearn.classification/ridge-classifier 0.30
   :sklearn.classification/extra-tree-classifier 0.88
   :sklearn.classification/dummy-classifier 0.2

   :metamorph.ml/dummy-classifier 0.2})

(def smile-model-specs
  (map
   #(vector (get min-accuracies % 0.93)
            {:model-type %})
   (->> (ml/model-definition-names)
        (filter #(str/starts-with? (namespace %) "smile.classification"))
        set
        ((fn [x] (set/difference
                  x
                  not-working-with-iris-data-or-default-params-or-no-probab))))))



(def sklearn-model-specs
  (map
   #(vector (get min-accuracies % 0.90)
            {:model-type %})
   (->> (ml/model-definition-names)
        (filter #(str/starts-with? (namespace %) "sklearn.classification"))
        set
        ((fn [x] (set/difference
                  x
                  not-working-with-iris-data-or-default-params-or-no-probab))))))

(def tribuo-model-specs [
                         [0.95 {:model-type :scicloj.ml.tribuo/classification
                                :tribuo-components [{:name "logistic"
                                                     :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"
                                                     :properties {:seed "1234"
                                                                  :shuffle "false"
                                                                  :epochs "10"}}
                                                    {:name "ada"
                                                     :type "org.tribuo.classification.ensemble.AdaBoostTrainer"
                                                     :properties {:innerTrainer "logistic"
                                                                  :numMembers "5"
                                                                  :seed "1234"
                                                                  }}]
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
(def xgboost-specs [
                    [0.94 {:model-type :xgboost/classification
                           :num-class 3}]])

(def other-specs [
                  [0.28 {:model-type :smile.classification/mlp
                         :layer-builders [mlp-hidden-layer-builder mlp-output-layer-builder]}]
                  [0.2 {:model-type :metamorph.ml/dummy-classifier}]])

(def model-specs
  (concat
   xgboost-specs
   other-specs
   tribuo-model-specs
   smile-model-specs
   sklearn-model-specs)
   )





(defn my-classification-accuracy [lhs rhs]
  (loss/classification-accuracy lhs rhs))

(defn- validate-nippy-round-trip [model-spec result val-ds]
  (let [tmp-file (java.io.File/createTempFile
                  (format "model-%s-" (:model-type model-spec))
                  ".nippy")

        _ (nippy/freeze-to-file
           tmp-file
           (->> result first first :fit-ctx :model))
        new-model (nippy/thaw-from-file tmp-file)

        new-prediction
        (-> val-ds
         (ml/predict new-model)
         (ds-cat/reverse-map-categorical-xforms)
         :species)
        new-trueth
        (-> val-ds
         ds-cat/reverse-map-categorical-xforms
         :species)

        new-accurcay
        (loss/classification-accuracy
         new-prediction
         new-trueth)
        min-accurcay (get min-accuracies (:model-type model-spec) 0.7)
        ]
    (is (< min-accurcay
         
         new-accurcay)
        (format "min accurcay (%s) validation failed for: %s"
                min-accurcay
                model-spec)
        )))

(defn classify [model-spec ds]
  (println :verify (:model-type model-spec))
  (let [train-test-split
        (tc/split->seq ds :kfold {:seed 1234 :k 10})

        pipe
        (mm/pipeline
         {:metamorph/id :model}
         (ml/model model-spec))

        result
        (ml/evaluate-pipelines
         [pipe]
         train-test-split
         my-classification-accuracy
         :accuracy)
        _
        (when (not (contains? not-writable--or-readable-with-nippy
                              (:model-type model-spec)))
          (validate-nippy-round-trip model-spec result ds))



        accuracy (-> result first first :test-transform :mean)]


    accuracy))

(defn- remove-model-type [model-specs type]
  (remove
   #(= type
       (-> % second :model-type))
   model-specs))

(defn- verify-fn [[expected-acc spec] iris]
  (try
    (let [acc (classify spec iris)]
      (println :acc acc)
      (is
       (>= acc
           expected-acc)

       (format "%s: expect at least: %s, found : %s"
               (:model-type spec)
               expected-acc acc)))

    (catch Exception e  (is false e))))


(deftest verify-classification-iris-int-catmap
  (let [iris  (data/iris-ds)]
    (run!
     #(verify-fn % iris)
     model-specs)))

(deftest verify-classification-iris-float-catmap
  (let [iris
        (->
         (data/iris-ds)
         ds-cat/reverse-map-categorical-xforms
         (ds/categorical->number [:species] {} :float64))]
    (run!
     #(verify-fn % iris)
     model-specs)))

 (deftest verify-classification-iris-no-catmap
  (let [iris
        (->
         (data/iris-ds)
         ds-cat/reverse-map-categorical-xforms 
         )]
    (run!
     #(verify-fn % iris)
     ;; only tribuo can deal with "string" target column
     ;;https://github.com/scicloj/noj/issues/36
     (concat
      ;other-specs
      ;xgboost-specs
      tribuo-model-specs
      ;smile-model-specs
      ;sklearn-model-specs
      ))
    ))

(deftest verify-classification-iris-nil-catmap-int
  (let [iris
        (->
         (data/iris-ds)
         (ds/assoc-metadata [:species] :categorical-map nil))]
    (run!
     #(verify-fn % iris)

     (-> model-specs
         ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
         (remove-model-type  :smile.classification/mlp)
         ))))

(deftest verify-classification-iris-nil-catmap-float
  (let [iris
        (->
         (data/iris-ds)
         (ds-cat/reverse-map-categorical-xforms)
         (ds/categorical->number [:species] {} :float64)
         (ds/assoc-metadata [:species] :categorical-map nil))]
    (run!

     #(verify-fn % iris)
     (-> model-specs
         ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
         (remove-model-type  :smile.classification/mlp)
         ))))

(def iris-ds-regression
  (->
   (data/iris-ds)
   (tc/drop-columns [:species])
   (ds-mod/set-inference-target :sepal-length)))


(def split
  (first
   (tc/split->seq 
    iris-ds-regression
    :holdout
    {:seed 12345}
    )))

(def iris-ds-regression--train
  (:train split))

(def iris-ds-regression--test
  (:test split))


(defn assert-mae [model model-map]
  (let [mae
        (loss/mae
         (-> iris-ds-regression--test :sepal-length)
         (-> (ml/predict iris-ds-regression--test model) :sepal-length))]
    (println :mae mae)

    (is (>
         0.4
         mae) (format "mae validation failed: %s" model-map))))
    



(defn validate-regression [model-map]
  ;(println :model-type (:model-type model-map))
  (let [model
        (ml/train
         iris-ds-regression--train
         model-map)]
    
    (when (not (contains? not-writable--or-readable-with-nippy 
                          (:model-type model-map)))
      (let [frozen (nippy/freeze-to-string model)
            unfrozen-model (nippy/thaw-from-string frozen)]
        (assert-mae unfrozen-model model-map)))

    (assert-mae model model-map)))

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
    :sklearn.regression/random-forest-regressor
    

    ]))

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


