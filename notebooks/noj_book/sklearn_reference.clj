;; # Sklearn model reference

;; As discussed in the [Machine Learning](../noj_book.ml_basic.html) chapter,
;; this book contains reference chapters for machine learning models
;; that can be registered in [metamorph.ml](https://github.com/scicloj/metamorph.ml).

;; This specific chapter focuses on the models of the
;; [scijit-learn](https://scikit-learn.org/stable/) Python library,
;; which is wrapped by [sklearn-clj](https://github.com/scicloj/sklearn-clj).

(ns noj-book.sklearn-reference
  (:require
   [noj-book.utils.render-tools :refer [render-key-info]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [tech.v3.dataset.tensor :as dst]
   [libpython-clj2.python :refer [py.- ->jvm]]
   [tech.v3.dataset.metamorph :as ds-mm]
   [noj-book.utils.render-tools-sklearn]
   [scicloj.sklearn-clj.ml]))

;; ## Sklearn model reference

;; Below we find all [sklearn](https://scikit-learn.org/stable/) models with their parameters and the original documentation.
;;
;; The parameters are given as Clojure keys in kebab-case.
;; As the document texts are imported from Python, they refer to the
;; Python spelling of the parameter. 
;;
;; But the translation between the two should be obvious.


;; Example: [logistic regression](https://en.wikipedia.org/wiki/Logistic_regression)

(def ds (dst/tensor->dataset [[0 0 0] [1 1 1] [2 2 2]]))

;; Make pipe with sklearn model 'logistic-regression'
(def pipe
  (mm/pipeline
   (ds-mm/set-inference-target 2)
   {:metamorph/id :model}
   (ml/model {:model-type :sklearn.classification/logistic-regression
              :max-iter 100})))


;; Train model:
(def fitted-ctx
  (pipe {:metamorph/data ds
         :metamorph/mode :fit}))

;; Predict on new data:
(->
 (mm/transform-pipe
  (dst/tensor->dataset [[3 4 5]])
  pipe
  fitted-ctx)
 :metamorph/data)

;; Access model details via Python interop
;; (using [libpython-clj](https://github.com/clj-python/libpython-clj)):
(-> fitted-ctx :model :model-data :model
    (py.- coef_)
    (->jvm))


;; All model attributes are also included in the context.

(def model-attributes
  (-> fitted-ctx :model :model-data :attributes))


(kind/hiccup
 [:dl (map
       (fn [[k v]]
         [:span
          (vector :dt k)
          (vector :dd  (clojure.pprint/write v :stream nil))])
       model-attributes)])



;; ## `:sklearn.classification` models
^:kindly/hide-code
(render-key-info ":sklearn.classification" {:level "###"
                                            :remove-s ":sklearn.classification"
                                            :docu-doc-string-fn noj-book.utils.render-tools-sklearn/docu-doc-string})


;; ## `:sklearn.regression` models
^:kindly/hide-code
(render-key-info ":sklearn.regression" {:level "###"
                                        :remove-s ":sklearn.regression"})


