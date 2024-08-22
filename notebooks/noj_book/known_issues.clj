;; # Known issues ❗

^:kindly/hide-code
(ns noj-book.known-issues
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]))

;; ## scicloj.ml.tribuo

;; Due to a current bug regarding cyclic depdendencies, when using [scicloj.ml.tribuo](https://github.com/scicloj/scicloj.ml.tribuo) for machine learning, it is necessary to include it explicitly in your project dependencies (even though it is a depedency of Noj itself):

(kindly/hide-code
 (kind/code
  "scicloj/scicloj.ml.tribuo {:git/url \"https://github.com/scicloj/scicloj.ml.tribuo\"
                             :git/sha \"f4ebf1e1bb78eb99dd35ca886d75b9f65d800e8d\"}"))
