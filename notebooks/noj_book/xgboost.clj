;; # Xgboost model reference

;; As discussed in the [Machine Learning](../noj_book.ml_basic.html) chapter,
;; this book contains reference chapters for machine learning models
;; that can be registered in [metamorph.ml](https://github.com/scicloj/metamorph.ml).

;; This specific chapter focuses on the [XGBoost](https://en.wikipedia.org/wiki/XGBoost)
;; algorithm provided by [scicloj.ml.xgboost](https://github.com/scicloj/scicloj.ml.xgboost).

^:kindly/hide-code
(ns noj-book.xgboost
  (:require
   [scicloj.ml.xgboost]
   [noj-book.utils.render-tools :refer [render-key-info]]))


;; In the following we have a list of all model keys of Xgboost models
;; including parameters.
;; They can be used like this:
(comment
  (ml/train df
            {:model-type <model-key>
             :param-1 0
             :param-2 1}))

^:kindly/hide-code
(render-key-info :xgboost)
