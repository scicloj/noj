(ns noj-book.xgboost
  (:require [tablecloth.api :as tc]
            [tech.v3.dataset.modelling :as dsmod]
            [scicloj.metamorph.ml :as ml]
            [scicloj.ml.tribuo]
            [tablecloth.column.api :as tcc]
            [scicloj.hanamicloth.v1.plotlycloth :as ploclo]))




(def ds
  (-> {:w (range 99)}
      tc/dataset
      (tc/map-columns :x [:w] (fn [w]
                                (* 0.1 (+ 2
                                          (* 3 w)
                                          (rand)))))
      (tc/map-columns :y [:x] (fn [x]
                                (* 0.1 (+ (rand)
                                          (if (< x 10)
                                            5
                                            9)))))))

(-> ds
    (ploclo/layer-point {:=x :x
                         :=y :y}))



(def train-and-test
  (-> ds
      (dsmod/set-inference-target :y)
      tc/split->seq
      first))

(def trainset (:train train-and-test))
(def testset (:test train-and-test))

(tc/row-count trainset)
(tc/row-count testset)


;; CART -- Classification and Regression Trees


(def tree-regression
  {:model-type :scicloj.ml.tribuo/regression
   :tribuo-components [{:name "rtree"
                        :type "org.tribuo.regression.rtree.CARTRegressionTrainer"
                        :properties {:maxDepth "2"
                                     :fractionFeaturesInSplit "1.0"
                                     :seed "12345"
                                     :impurity "mse"}}
                       {:name "mse"
                        :type "org.tribuo.regression.rtree.impurity.MeanSquaredError"}]
   :tribuo-trainer-name "rtree"})


(def tree-model
  (ml/train trainset tree-regression))

;; metamorph.ml


tree-model


(-> (tcc/- (-> trainset
               (ml/predict tree-model)
               :y)
           (-> trainset
               :y))
    tcc/sq
    tcc/mean)


(-> (tcc/- (-> testset
               (ml/predict tree-model)
               :y)
           (-> testset
               :y))
    tcc/sq
    tcc/mean)

(-> tree-model
    :model-data
    :model
    bean
    :roots
    (get "y")
    println)













(-> tree-model
    :model-data
    :model
    bean
    :roots
    (get "y")
    bean)


(def xgboost-regression
  {:model-type :scicloj.ml.tribuo/regression
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
   :tribuo-trainer-name "xgboost"})

(def model
  (ml/train trainset xgboost-regression))

(-> testset
    (ml/predict model)
    :y
    (tcc/- (:y testset))
    tcc/sq
    tcc/mean)

(-> trainset
    (ml/predict model)
    :y
    (tcc/- (:y trainset))
    tcc/sq
    tcc/mean)

(-> model
    :model-data
    :model)


(-> model
    :model-data
    :model
    bean
    :innerModels)
