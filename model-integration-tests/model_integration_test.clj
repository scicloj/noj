(ns model-integration-test
  (:require [scicloj.metamorph.core :as mm]
            [scicloj.metamorph.ml :as ml]
            [scicloj.metamorph.ml.loss :as loss]
            [scicloj.metamorph.ml.toydata :as data]
            [tech.v3.dataset.categorical :as ds-cat]
            [tablecloth.api :as tc]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.test :refer [is deftest]]
            [tech.v3.dataset :as ds]
            [taoensso.nippy :as nippy]
            [libpython-clj2.python :as py])
  (:import
   [java.util.logging Logger]
   [org.slf4j.bridge SLF4JBridgeHandler]
   (smile.base.mlp ActivationFunction Cost HiddenLayerBuilder LayerBuilder OutputFunction OutputLayerBuilder)))

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
         '[scicloj.ml.smile.classification]
         '[scicloj.ml.tribuo]
         '[scicloj.sklearn-clj.ml]
         '[scicloj.ml.xgboost])

(def not-writable--or-readable-with-nippy
  
  #{:smile.classification/mlp  ;;https://github.com/scicloj/scicloj.ml.smile/issues/18
    :scicloj.ml.tribuo/classification ;;https://github.com/scicloj/scicloj.ml.tribuo/issues/5
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
   :sklearn.classification/extra-tree-classifier 0.91
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
    [0.93 {:model-type :scicloj.ml.tribuo/classification
       :tribuo-components [{:name "logistic"
                            :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"
                            :properties {:seed "1234"
                                         :shuffle "false"
                                         :epochs "10"}}]
       :tribuo-trainer-name "logistic"}]
[0.93 {:model-type :scicloj.ml.tribuo/classification
       :tribuo-components [{:name "random-forest"
                            :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                            :properties {:maxDepth "8"
                                         :useRandomSplitPoints "false"
                                         :fractionFeaturesInSplit "0.5"}}]
       :tribuo-trainer-name "random-forest"}]
                   
])
(def xgboost-specs [
                    [0.94 {:model-type :xgboost/classification}]])

(def other-specs [
                  [0.28 {:model-type :smile.classification/mlp
                         :layer-builders [mlp-hidden-layer-builder mlp-output-layer-builder]}]
                  [0.30 {:model-type :metamorph.ml/dummy-classifier}]])

(def model-specs
  (concat
   xgboost-specs
   other-specs
   tribuo-model-specs
   smile-model-specs
   sklearn-model-specs))





(defn my-classification-accuracy [lhs rhs]
  ;(println :lhs (meta lhs))
  ;(println :rhs (meta rhs))

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
         new-trueth)]
    (is (<
         (get min-accuracies (:model-type model-spec) 0.7)
         new-accurcay))))

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
      (is
       (>= acc
           expected-acc)
  
       (format "%s: expect at least: %s, found : %s"
               (:model-type spec)
               expected-acc acc)))
  
    (catch Exception e  (is false e)))
  )




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
     ;; only tribuo can deal with "string" target
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
         ;;https://github.com/scicloj/scicloj.ml.tribuo/issues/6 
         (remove-model-type  :scicloj.ml.tribuo/classification)
         ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
         (remove-model-type  :smile.classification/mlp)
         ;;https://github.com/scicloj/scicloj.ml.xgboost/issues/1
         (remove-model-type  :xgboost/classification)
         ))
     )
    )

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
         ;;https://github.com/scicloj/scicloj.ml.tribuo/issues/6 
         (remove-model-type  :scicloj.ml.tribuo/classification)
         ;;https://github.com/scicloj/scicloj.ml.smile/issues/19
         (remove-model-type  :smile.classification/mlp)
         ;;https://github.com/scicloj/scicloj.ml.xgboost/issues/1
         (remove-model-type  :xgboost/classification)))))

  ;;(classify (-> smile-model-specs first second) iris-3)

