(def var0 nil)


(ns
 noj-book.interactions-ols-generated-test
 (:require
  [fastmath.stats :as fmstats]
  [scicloj.kindly.v4.api :as kindly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.core :as mm]
  [scicloj.metamorph.ml :as ml]
  [scicloj.metamorph.ml.loss :as loss]
  [scicloj.metamorph.ml.regression]
  [tablecloth.api :as tc]
  [tablecloth.column.api :as tcc]
  [tablecloth.pipeline :as tcpipe]
  [tech.v3.dataset.modelling :as modelling]
  [scicloj.ml.tribuo]
  [clojure.test :refer [deftest is]]))


(def var2 (def md (comp kindly/hide-code kind/md)))


(def
 var3
 (md
  "This examples shows how to do interactions in linear regression with `metamorph.ml`."))


(def
 var4
 (md
  "Taking ideas from: [Interaction Effect in Multiple Regression: Essentials](http://www.sthda.com/english/articles/40-regression-analysis/164-interaction-effect-in-multiple-regression-essentials/) by Alboukadel Kassambara"))


(def var5 (md "First we load the data:"))


(def
 var6
 (def
  marketing
  (tc/dataset
   "https://github.com/scicloj/datarium-CSV/raw/main/data/marketing.csv.gz"
   {:key-fn keyword})))


(def var7 (md "and do some preprocessing to set up the regression:"))


(def
 var8
 (def
  preprocessed-data
  (->
   marketing
   (tc/drop-columns [:newspaper])
   (modelling/set-inference-target :sales))))


(def var9 (md "## Additive model"))


(def
 var10
 (md
  "First we build an additive model, which model equation is\n$$sales = b0 + b1 * youtube + b2 * facebook$$"))


(def var11 (def linear-model-config {:model-type :fastmath/ols}))


(def
 var12
 (def
  additive-pipeline
  (mm/pipeline #:metamorph{:id :model} (ml/model linear-model-config))))


(def var13 (md "We evaluate it, "))


(def
 var14
 (def
  evaluations
  (ml/evaluate-pipelines
   [additive-pipeline]
   (tc/split->seq preprocessed-data :holdout {:seed 112723})
   loss/rmse
   :loss
   {:other-metrices
    [{:name :r2, :metric-fn fmstats/r2-determination}]})))


(def
 var15
 (md
  "and print the resulting model:\n(note that the `:sales` term means the intercept `b0`)"))


(def var16 (md "(note that )"))


(def var17 (-> evaluations flatten first :fit-ctx :model ml/tidy))


(def var18 (md "We have the following metrics:"))


(def var19 (md "$RMSE$"))


(def var20 (-> evaluations flatten first :test-transform :metric))


(deftest test21 (is (= var20 0.933077510748531)))


(def var22 (md "$R^2$"))


(def
 var23
 (->
  evaluations
  flatten
  first
  :test-transform
  :other-metrices
  first
  :metric))


(deftest test24 (is (= var23 0.9747551116991899)))


(def var25 (md "## Interaction effects"))


(def
 var26
 (md
  "Now we add interaction effects to it, resulting in this model equation:\n$$sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)$$"))


(def
 var27
 (def
  pipe-interaction
  (mm/pipeline
   (tcpipe/add-column
    :youtube*facebook
    (fn [ds] (tcc/* (ds :youtube) (ds :facebook))))
   #:metamorph{:id :model}
   (ml/model linear-model-config))))


(def var28 (md "Again we evaluate the model,"))


(def
 var29
 (def
  evaluations
  (ml/evaluate-pipelines
   [pipe-interaction]
   (tc/split->seq preprocessed-data :holdout {:seed 112723})
   loss/rmse
   :loss
   {:other-metrices
    [{:name :r2, :metric-fn fmstats/r2-determination}]})))


(def var30 (md "and print it and the performance metrics:"))


(def var31 (-> evaluations flatten first :fit-ctx :model ml/tidy))


(def
 var32
 (md
  "As the multiplcation of `youtube*facebook` is as well statistically relevant, it\nsuggests that there is indeed an interaction between these 2 predictor variables youtube and facebook."))


(def var33 (md "$RMSE$"))


(def var34 (-> evaluations flatten first :test-transform :metric))


(def var35 (md "$R^2$"))


(def
 var36
 (->
  evaluations
  flatten
  first
  :test-transform
  :other-metrices
  first
  :metric))


(def
 var37
 (md
  "$RMSE$ and $R^2$ of the intercation model are sligtly better.\n\nThese results suggest that the model with the interaction term is better than the model that contains only main effects.\nSo, for this specific data, we should go for the model with the interaction model.\n"))
