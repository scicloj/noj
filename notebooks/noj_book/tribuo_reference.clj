;; # Tribuo reference - DRAFT 🛠

^:kindly/hide-code
(ns noj-book.tribuo-reference
  (:require
   [clojure.java.classpath]
   [clojure.reflect]
   [scicloj.ml.tribuo]
   [noj-book.utils.tribuo-render-tools :refer [trainer-infos all-non-trainer render-configurables]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml :as ml]))


;;The following is a refeference for all Tribuo trainers.
;; They can be used as the model specification in `ml/train` on the :type
;; of the tribuo trainer
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

;; There is as well a reference on all non-trainer compotents of Tribuo.
;; These could potentialy as well be used in Tribuo model specs.


;; ## Tribuo trainer reference 
^:kindly/hide-code
(render-configurables (trainer-infos))

;; ## Tribuo component reference 
^:kindly/hide-code
(render-configurables  (all-non-trainer))

     

