^:kindly/hide-code
(ns noj-book.xgboost
  (:require
   [scicloj.ml.xgboost]
   [noj-book.utils.render-tools :refer [render-key-info]]))


;; ## Xgboost model reference
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
