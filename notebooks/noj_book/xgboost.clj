(ns noj-book.xgboost
  (:require [tablecloth.api :as tc]
            [tech.v3.dataset.modelling :as dsmod]
            [scicloj.metamorph.ml :as ml]
            [scicloj.ml.tribuo]))

(def ds
  (-> {:x (range 99)}
      tc/dataset
      (tc/map-columns :y [:x] (fn [x]
                                (+ 2
                                   (* 3 x)
                                   (rand))))
      (tc/map-columns :z [:x :y] (fn [x y]
                                   (+ x
                                      (* x y)
                                      (rand))))))

(def train-and-test
  (-> ds
      (dsmod/set-inference-target :z)
      tc/split->seq
      first))

(-> train-and-test
    :train
    (ml/train {:model-type :scicloj.ml.tribuo/regression
               :tribuo-components [{:name "xgboost"
                                    :type "org.tribuo.regression.xgboost.XGBoostRegressionTrainer"
                                    :properties {:numTrees "20"
                                                 :eta "0.5"
                                                 :gamma "0.1"
                                                 :maxDepth "5"
                                                 :minChildWeight "1.0"
                                                 :subsample "1.0"
                                                 :nThread "6"
                                                 :seed "1"}}]
               :tribuo-trainer-name "xgboost"}))
