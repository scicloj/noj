(ns lr
  (:require [tablecloth.api :as tc]
            [tech.v3.tensor :as dt]
            [scicloj.metamorph.ml :as ml]
            [scicloj.ml.smile.regression]
            [scicloj.metamorph.ml.loss :as loss]
            [tech.v3.dataset.modelling :as dsmod]
            [tech.v3.dataset.tensor :as dst]
            [tech.v3.dataset :as ds]
            [tech.v3.datatype.functional :as dtf]))

(def x (dt/->tensor [[1 1] [1 2] [2 2] [2 3]]))
(def y
  (dtf/+ 3
   (dt/reduce-axis x (fn [row] (dtf/dot-product row (dt/->tensor [1 2]))))))

(def y-ds
  (->
   (dst/tensor->dataset x)
   (tc/add-column :y y)
   (dsmod/set-inference-target :y)))

(def reg
  (ml/train  y-ds
             {:model-type :smile.regression/ordinary-least-square}))

(- 1 (loss/rmse y (:y (ml/predict y-ds reg))));; => 1.0
(seq (.coefficients (ml/thaw-model reg))) ;; => (2.9999999999999996 0.9999999999999998 2.0000000000000004)
(seq (:y (ml/predict (tc/dataset [[3 5]]) reg)));; => (16.0)


(defn Xy->dataset
  "Combines tensor X and vector y to a dataset
  where y is set to be the inference target
  x need to be a tensor
  y a seq or 1-D tensor
  "
  [x y]

  (->
   (dst/tensor->dataset x)
   (ds/add-column (ds/new-column :y y))
   (dsmod/set-inference-target :y)))

