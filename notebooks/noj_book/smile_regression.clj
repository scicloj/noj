;; # Smile regression models reference - DRAFT 🛠

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)

^:kindly/hide-code
(require '[scicloj.ml.smile.regression]
         '[scicloj.ml.tribuo])

(ns noj-book.smile-regression 
  (:require
   [noj-book.utils.render-tools :refer [render-key-info]]
   [scicloj.ml.tribuo]))


^:kindly/hide-code
(render-key-info :smile.regression)

