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
            [tech.v3.dataset :as ds])
  (:import 
   (smile.base.mlp ActivationFunction Cost HiddenLayerBuilder LayerBuilder OutputFunction OutputLayerBuilder))
  )

(def mlp-hidden-layer-builder
  (HiddenLayerBuilder. 1 (ActivationFunction/linear)))

(def mlp-output-layer-builder
  (OutputLayerBuilder. 3  OutputFunction/LINEAR  Cost/MEAN_SQUARED_ERROR))


(require '[scicloj.metamorph.ml.classification]
         '[scicloj.ml.smile.classification]
         '[scicloj.ml.tribuo]
         '[scicloj.sklearn-clj.ml]
         '[scicloj.ml.xgboost]
         )

(def min-accuracies
  {:smile.classification/linear-discriminant-analysis 0.85})

(def smile-model-specs
  (map
   #(vector (get min-accuracies % 0.95)
            {:model-type %})
   (->> (ml/model-definition-names)
        (filter #(str/starts-with? (namespace %) "smile.classification"))
        set
        ((fn [x] (set/difference
                  x
                  #{:smile.classification/sparse-svm
                    :smile.classification/maxent-binomial
                    :smile.classification/maxent-multinomial
                    :smile.classification/mlp
                    :smile.classification/svm
                    :smile.classification/sparse-logistic-regression
                    :smile.classification/discrete-naive-bayes}))))))



(def sklearn-model-specs
  (map 
   #(vector 0.90 
            {:model-type %})
   (->> (ml/model-definition-names)
        (filter #(str/starts-with? (namespace %) "sklearn.classification" ))
         set
         ((fn [x] (set/difference
                   x
                   #{:sklearn.classification/perceptron
                     :sklearn.classification/sgd-classifier
                     :sklearn.classification/svc
                     
                     })))
   )))


(def model-specs 
  (concat
   [
    [0.98 {
          ;;  :validate-parameters 1
          ;;  :round 10
          ;;  :silent 0
          ;;  :verbosity 3
           :model-type :xgboost/classification}]
    [0.30 {:model-type :smile.classification/mlp
           :layer-builders [mlp-hidden-layer-builder mlp-output-layer-builder]}]
    [0.95 {:model-type :sklearn.classification/decision-tree-classifier}]
    [0.95 {:model-type :sklearn.classification/random-forest-classifier}]
    [0.95 {:model-type :sklearn.classification/logistic-regression}]
    [0.93 {:model-type :scicloj.ml.tribuo/classification
           :tribuo-components [{:name "logistic"
                                :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"
                                :properties {:seed "1234"
                                             :shuffle "false" 
                                             :epochs "10"}}]
           :tribuo-trainer-name "logistic"}]
    [0.94 {:model-type :scicloj.ml.tribuo/classification
           :tribuo-components [{:name "random-forest"
                                :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                                :properties {:maxDepth "8"
                                             :useRandomSplitPoints "false"
                                             :fractionFeaturesInSplit "0.5"}}]
           :tribuo-trainer-name "random-forest"}]
    [0.30 {:model-type :metamorph.ml/dummy-classifier}]     
    ]
   smile-model-specs
   ;sklearn-model-specs
   ))





(defn my-classification-accuracy [lhs rhs]
  ;(println :lhs (meta lhs))
  ;(println :rhs (meta rhs))
  
  (loss/classification-accuracy lhs rhs)
  )

(defn verify-classification [model-spec expected-accuracy ds]
  (println :verify (:model-type model-spec))
  (let [ 
        train-test-split
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
        accuracy (-> result first first :train-transform :mean)
        ]

    (is (>= accuracy expected-accuracy)
            (format "%s: expect at least: %s, found : %s" 
                    (:model-type model-spec)
                    expected-accuracy accuracy))))


(deftest verify-classifictions-iris
  (run!
   (fn [[acc spec]] (verify-classification spec acc (data/iris-ds)))
   model-specs))


(def iris-2
  (->
   (data/iris-ds)
   ds-cat/reverse-map-categorical-xforms
   ))

;; (deftest verify-classification-iris-2
;;   (run!
;;    (fn [[acc spec]] (verify-classification spec acc iris-2))
;;    smile-model-specs))


(def iris-3
  (->
   (data/iris-ds)
   (ds/assoc-metadata [:species] :categorical-map nil)
   ))


(deftest verify-classification-iris-3
  (run!
   (fn [[acc spec]] (verify-classification spec acc iris-3))
   smile-model-specs))

