;; # Smile regression models reference

;; As discussed in the [Machine Learning](../noj_book.ml_basic.html) chapter,
;; this book contains reference chapters for machine learning models
;; that can be registered in [metamorph.ml](https://github.com/scicloj/metamorph.ml).

;; This specific chapter focuses on regression models of
;; [Smile](https://haifengl.github.io/) version 2.6,
;; which are wrapped by
;; [scicloj.ml.smile](https://github.com/scicloj/scicloj.ml.smile).

;; Note that this chapter requires `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)

;; In the following we have a list of all model keys of
;; [Smile](https://haifengl.github.io/) regression models, including parameters.
;; They can be used like this:

(comment
  (ml/train df
            {:model-type <model-key>
             :param-1 0
             :param-2 1}))

(require '[scicloj.ml.smile.regression]
         '[scicloj.ml.tribuo])

^:kindly/hide-code
(ns noj-book.smile-regression 
  (:require
   [noj-book.utils.render-tools :refer [render-key-info]]
   [scicloj.ml.tribuo]))

^:kindly/hide-code
(render-key-info :smile.regression)

