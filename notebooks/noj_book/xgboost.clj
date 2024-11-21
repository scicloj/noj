(ns noj-book.xgboost
  (:require
   [noj-book.render-tools :refer [render-key-info]]))


;; ### Xgboost model
(require '[scicloj.ml.xgboost])
^:kindly/hide-code
(render-key-info ":xgboost")
