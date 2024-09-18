(def var0 nil)


(ns
 noj-book.ml-basic-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.toydata :as data]
  [tech.v3.dataset :as ds]
  [scicloj.metamorph.ml :as ml]
  [scicloj.kindly.v4.api :as kindly]
  [clojure.test :refer [deftest is]]))


(def var2 nil)


(def var3 (-> (data/titanic-ds-split) :train))


(def var4 nil)


(def var5 (defonce titanic-split (data/titanic-ds-split)))


(def
 var6
 (def
  titanic
  (->
   titanic-split
   :train
   (tc/map-columns
    :survived
    [:survived]
    (fn [el] (case el 0 "no" 1 "yes"))))))


(def var7 nil)


(def var8 (tc/column-names titanic))


(def var9 nil)


(def var10 (ds/descriptive-stats titanic))


(def var11 nil)


(def var12 (-> titanic :survived frequencies))


(def var13 nil)


(def var14 (def categorical-feature-columns [:sex :pclass :embarked]))


(def var15 (def target-column :survived))


(def var16 nil)


(def
 var17
 (map
  (fn*
   [p1__65132#]
   (hash-map
    :col-name
    p1__65132#
    :values
    (distinct (get titanic p1__65132#))))
  categorical-feature-columns))


(def var18 nil)


(def
 var19
 (require
  '[tech.v3.dataset.categorical :as ds-cat]
  '[tech.v3.dataset.modelling :as ds-mod]
  '[tech.v3.dataset.column-filters :as cf]))


(def var20 nil)


(def
 var21
 (def
  relevant-titanic-data
  (->
   titanic
   (tc/select-columns (conj categorical-feature-columns target-column))
   (tc/drop-missing)
   (ds/categorical->number [:survived] ["no" "yes"] :float64)
   (ds-mod/set-inference-target target-column))))


(def var22 nil)


(def
 var23
 (def
  cat-maps
  [(ds-cat/fit-categorical-map
    relevant-titanic-data
    :sex
    ["male" "female"]
    :float64)
   (ds-cat/fit-categorical-map
    relevant-titanic-data
    :pclass
    [0 1 2]
    :float64)
   (ds-cat/fit-categorical-map
    relevant-titanic-data
    :embarked
    ["S" "Q" "C"]
    :float64)]))


(def var24 cat-maps)


(def var25 nil)


(def
 var26
 (def
  numeric-titanic-data
  (reduce
   (fn [ds cat-map] (ds-cat/transform-categorical-map ds cat-map))
   relevant-titanic-data
   cat-maps)))


(def var27 (tc/head numeric-titanic-data))


(def var28 nil)


(def
 var29
 (def
  split
  (first (tc/split->seq numeric-titanic-data :holdout {:seed 112723}))))


(def var30 split)


(def var31 nil)


(def
 var32
 (require
  '[scicloj.metamorph.ml :as ml]
  '[scicloj.metamorph.ml.classification]
  '[scicloj.metamorph.ml.loss :as loss]))


(def var33 nil)


(def
 var34
 (def
  dummy-model
  (ml/train
   (:train split)
   {:model-type :metamorph.ml/dummy-classifier})))


(def var35 nil)


(def
 var36
 (def dummy-prediction (ml/predict (:test split) dummy-model)))


(def var37 nil)


(def var38 (-> dummy-prediction :survived frequencies))


(def var39 nil)


(def
 var40
 (loss/classification-accuracy
  (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
  (:survived (ds-cat/reverse-map-categorical-xforms dummy-prediction))))


(def var41 nil)


(def var42 (require '[scicloj.ml.tribuo]))


(def
 var43
 (def
  lreg-model
  (ml/train
   (:train split)
   {:model-type :scicloj.ml.tribuo/classification,
    :tribuo-components
    [{:name "logistic",
      :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}],
    :tribuo-trainer-name "logistic"})))


(def var44 (def lreg-prediction (ml/predict (:test split) lreg-model)))


(def
 var45
 (loss/classification-accuracy
  (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
  (:survived (ds-cat/reverse-map-categorical-xforms lreg-prediction))))


(def var46 nil)


(def
 var47
 (def
  rf-model
  (ml/train
   (:train split)
   {:model-type :scicloj.ml.tribuo/classification,
    :tribuo-components
    [{:name "random-forest",
      :type
      "org.tribuo.classification.dtree.CARTClassificationTrainer",
      :properties
      {:maxDepth "8",
       :useRandomSplitPoints "false",
       :fractionFeaturesInSplit "0.5"}}],
    :tribuo-trainer-name "random-forest"})))


(def var48 (def rf-prediction (ml/predict (:test split) rf-model)))


(def
 var49
 (loss/classification-accuracy
  (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
  (:survived (ds-cat/reverse-map-categorical-xforms rf-prediction))))


(deftest test50 (is (= var49 0.7878787878787878)))


(def var51 nil)
