(def var0 nil)


(ns
 noj-book.automl-generated-test
 (:require
  [noj-book.ml-basic :as ml-basic]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.api :as kindly]
  [clojure.test :refer [deftest is]]))


(def var2 nil)


(def
 var3
 (require
  '[scicloj.metamorph.ml :as ml]
  '[scicloj.metamorph.core :as mm]
  '[tablecloth.api :as tc]))


(def var4 nil)


(def var5 (def titanic ml-basic/numeric-titanic-data))


(def var6 nil)


(def var7 (def splits (first (tc/split->seq titanic))))


(def var8 (def train-ds (:train splits)))


(def var9 (def test-ds (:test splits)))


(def var10 nil)


(def
 var11
 (def
  my-pipeline
  (mm/pipeline
   (ml/model {:model-type :metamorph.ml/dummy-classifier}))))


(def var12 nil)


(def var13 my-pipeline)


(def var14 nil)


(def
 var15
 (def
  ctx-after-train
  (my-pipeline #:metamorph{:data train-ds, :mode :fit})))


(def var16 ctx-after-train)


(def var17 (keys ctx-after-train))


(def var18 nil)


(def var19 (vals ctx-after-train))


(def var20 nil)


(def
 var21
 (def
  ctx-after-predict
  (my-pipeline
   (assoc
    ctx-after-train
    :metamorph/mode
    :transform
    :metamorph/data
    test-ds))))


(def var22 ctx-after-predict)


(def var23 nil)


(def var24 (-> ctx-after-predict :metamorph/data :survived))


(def var25 nil)


(def
 var26
 (def
  train-ctx
  (mm/fit
   titanic
   (ml/model {:model-type :metamorph.ml/dummy-classifier}))))


(def var27 nil)


(def var28 train-ctx)


(def var29 nil)


(def
 var30
 (->>
  (ml/train train-ds {:model-type :metamorph.ml/dummy-classifier})
  (ml/predict test-ds)
  :survived))


(def var31 nil)


(def
 var32
 (def
  pipeline
  (mm/pipeline
   (ml/model {:model-type :metamorph.ml/dummy-classifier}))))


(def
 var33
 (->>
  (mm/fit-pipe train-ds pipeline)
  (mm/transform-pipe test-ds pipeline)
  :metamorph/data
  :survived))


(def var34 nil)


(def
 var35
 (def
  ops
  (fn
   [ctx]
   (assoc
    ctx
    :metamorph/data
    (tc/drop-columns (:metamorph/data ctx) [:embarked])))))


(def var36 nil)


(def var37 (def ops (mm/lift tc/drop-columns [:embarked])))


(def var38 nil)


(def var39 (require '[tablecloth.pipeline]))


(def var40 (def ops (tablecloth.pipeline/drop-columns [:embarked])))


(def var41 nil)


(def var42 (mm/pipeline ops))


(def var43 nil)


(def
 var44
 (def
  op-spec
  [[ml/model {:model-type :metamorph.ml/dummy-classifier}]]))


(def var45 nil)


(def var46 (mm/->pipeline op-spec))


(def var47 nil)


(def
 var48
 (defn
  make-results-ds
  [evaluation-results]
  (->>
   evaluation-results
   flatten
   (map
    (fn*
     [p1__64888#]
     (hash-map
      :options
      (-> p1__64888# :test-transform :ctx :model :options)
      :used-features
      (-> p1__64888# :fit-ctx :used-features)
      :mean-accuracy
      (-> p1__64888# :test-transform :mean))))
   tc/dataset)))


(def
 var49
 (require
  '[scicloj.metamorph.ml :as ml]
  '[scicloj.metamorph.ml.loss :as loss]
  '[scicloj.metamorph.core :as mm]
  '[scicloj.ml.tribuo]))


(def var50 nil)


(def
 var51
 (defn
  make-pipe-fn
  [model-spec features]
  (mm/pipeline
   (fn [ctx] (assoc ctx :used-features features))
   (mm/lift tc/select-columns (conj features :survived))
   #:metamorph{:id :model}
   (ml/model model-spec))))


(def var52 nil)


(def
 var53
 (def
  titanic-k-fold
  (tc/split->seq ml-basic/numeric-titanic-data :kfold {:seed 12345})))


(def var54 nil)


(def
 var55
 (def
  models
  [{:model-type :metamorph.ml/dummy-classifier}
   {:model-type :scicloj.ml.tribuo/classification,
    :tribuo-components
    [{:name "logistic",
      :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}],
    :tribuo-trainer-name "logistic"}
   {:model-type :scicloj.ml.tribuo/classification,
    :tribuo-components
    [{:name "random-forest",
      :type
      "org.tribuo.classification.dtree.CARTClassificationTrainer",
      :properties
      {:maxDepth "8",
       :useRandomSplitPoints "false",
       :fractionFeaturesInSplit "0.5"}}],
    :tribuo-trainer-name "random-forest"}]))


(def var56 nil)


(def
 var57
 (def
  feature-combinations
  [[:sex :pclass :embarked]
   [:sex]
   [:pclass :embarked]
   [:embarked]
   [:sex :embarked]
   [:sex :pclass]]))


(def var58 nil)


(def
 var59
 (def
  pipe-fns
  (for
   [model models feature-combination feature-combinations]
   (make-pipe-fn model feature-combination))))


(def var60 nil)


(def
 var61
 (def
  evaluation-results
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy)))


(def var62 nil)


(def var63 (make-results-ds evaluation-results))


(def var64 nil)


(def
 var65
 (def
  evaluation-results-all
  (ml/evaluate-pipelines
   pipe-fns
   titanic-k-fold
   loss/classification-accuracy
   :accuracy
   {:return-best-crossvalidation-only false,
    :return-best-pipeline-only false})))


(def var66 nil)


(def var67 (-> evaluation-results-all flatten count))


(def var68 nil)


(def
 var69
 (->
  (make-results-ds evaluation-results-all)
  (tc/unique-by)
  (tc/order-by [:mean-accuracy] :desc)
  (tc/head)
  (kind/table)))


(deftest
 test70
 (is
  ((fn*
    [p1__64889#]
    (->
     p1__64889#
     tc/rows
     (=
      [[[:sex :pclass :embarked]
        0.8110772551260077
        {:model-type :scicloj.ml.tribuo/classification,
         :tribuo-components
         [{:name "random-forest",
           :type
           "org.tribuo.classification.dtree.CARTClassificationTrainer",
           :properties
           {:maxDepth "8",
            :useRandomSplitPoints "false",
            :fractionFeaturesInSplit "0.5"}}],
         :tribuo-trainer-name "random-forest"}]
       [[:sex]
        0.7863327620135847
        {:model-type :scicloj.ml.tribuo/classification,
         :tribuo-components
         [{:name "random-forest",
           :type
           "org.tribuo.classification.dtree.CARTClassificationTrainer",
           :properties
           {:maxDepth "8",
            :useRandomSplitPoints "false",
            :fractionFeaturesInSplit "0.5"}}],
         :tribuo-trainer-name "random-forest"}]
       [[:sex :pclass]
        0.7863327620135847
        {:model-type :scicloj.ml.tribuo/classification,
         :tribuo-components
         [{:name "logistic",
           :type
           "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}],
         :tribuo-trainer-name "logistic"}]
       [[:sex :embarked]
        0.7863327620135847
        {:model-type :scicloj.ml.tribuo/classification,
         :tribuo-components
         [{:name "logistic",
           :type
           "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}],
         :tribuo-trainer-name "logistic"}]
       [[:sex]
        0.7863327620135847
        {:model-type :scicloj.ml.tribuo/classification,
         :tribuo-components
         [{:name "logistic",
           :type
           "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}],
         :tribuo-trainer-name "logistic"}]])))
   var69)))


(def var71 nil)


(def
 var72
 (require
  '[scicloj.metamorph.ml.toydata :as data]
  '[tech.v3.dataset.modelling :as ds-mod]
  '[tech.v3.dataset.categorical :as ds-cat]
  '[tech.v3.dataset :as ds]))


(def var73 nil)


(def var74 (def titanic (:train (data/titanic-ds-split))))


(def var75 nil)


(def
 var76
 (def
  relevant-titanic-data
  (->
   titanic
   (tc/select-columns
    (conj ml-basic/categorical-feature-columns :survived))
   (tc/drop-missing)
   (ds/categorical->number
    [:sex :pclass :embarked]
    [0 1 2 "male" "female" "S" "Q" "C"]
    :float64)
   (ds/categorical->number [:survived] [0 1] :float64)
   (ds-mod/set-inference-target :survived))))


(def var77 nil)


(def
 var78
 (defn
  make-pipe-fn
  [model-type features]
  (mm/pipeline
   (fn [ctx] (assoc ctx :used-features features))
   (mm/lift tc/select-columns (conj features :survived))
   #:metamorph{:id :model}
   (ml/model {:model-type model-type}))))


(def var79 nil)
