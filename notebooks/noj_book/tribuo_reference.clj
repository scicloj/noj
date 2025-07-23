;; # Tribuo reference

;; As discussed in the [Machine Learning](../noj_book.ml_basic.html) chapter,
;; this book contains reference chapters for machine learning models
;; that can be registered in [metamorph.ml](https://github.com/scicloj/metamorph.ml).

;; This specific chapter focuses on the models of the
;; [Tribuo](https://tribuo.org/) Java library,
;; which is wrapped by [scicloj.ml.tribuo](https://github.com/scicloj/scicloj.ml.tribuo).

^:kindly/hide-code
(ns noj-book.tribuo-reference
  (:require
   [clojure.java.classpath]
   [clojure.reflect]
   [scicloj.ml.tribuo]
   [noj-book.utils.tribuo-render-tools :refer [trainer-infos all-non-trainer render-configurables]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml :as ml]))


;; The following is a reference for all [Tribuo](https://tribuo.org/) trainers.
;; They can be used as the model specification in `ml/train` on the `:type`
;; of the Tribuo trainer.
(comment
  (ml/train
   ds
   {:model-type :scicloj.ml.tribuo/classification
    :tribuo-components [{:name "random-forest"
                         :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                         :properties {:maxDepth "8"
                                      :useRandomSplitPoints "false"
                                      :fractionFeaturesInSplit "0.5"}}]
    :tribuo-trainer-name "random-forest"}))

;; There is also a reference to all non-trainer components of Tribuo.
;; These could also be potentially used in Tribuo model specs.


;; ## Tribuo trainer reference
^:kindly/hide-code
(render-configurables (trainer-infos))

;; ## Tribuo component reference
^:kindly/hide-code
(render-configurables  (all-non-trainer))

     

