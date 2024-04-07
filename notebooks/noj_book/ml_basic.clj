;; # Machine learning

;; author: Carsten Behring

;; In this tutorial we will train a simple machine learning model
;; in order to predict the survival of titanic passengers given
;; their data.
;;

(ns noj-book.ml-basic
  (:require [tablecloth.api :as tc]
            [scicloj.metamorph.ml.toydata :as data]
            [tech.v3.dataset :as ds]
            [camel-snake-kebab.core :as csk]))

;; ## Inspect data
;;
;;  The titanic data is part of `metamorph.ml` and in the form of a
;;  train, test split
;;
;;  We use the :train part only for this tutorial.
;;
;;
;;
(->
 (data/titanic-ds-split)
 :train)


(defonce titanic-split
  (data/titanic-ds-split))

;;  this is the full dataset
(def titanic
  (-> titanic-split
      :train
      (tc/add-column :survived
                         (fn [ds]
                           (map
                            (fn [el] (case el
                                        0 "no"
                                        1 "yes"))
                            (:survived ds))))))




                                      




;;  It has various columns
(tc/column-names titanic)

;;  of which we can get some statistics
(ds/descriptive-stats titanic)

;; The data is more or less balanced across the 2 classes:
(-> titanic :survived frequencies)

;;  We will make a very simple model, which will
;;  predict the column :survived from columns :sex , :pclass and :embark
;;  These represent the "gender", "passenger class" and "port of embarkment"
;;
(def categorical-feature-columns [:sex :pclass :embarked])
(def target-column :survived)

;;## Convert categorical features to numeric
;;
;; As we need to convert the non numerical feature columns to categorical,
;; we will first look at their unique values:
(map
 #(hash-map
   :col-name %
   :values  (distinct (get titanic %)))
 categorical-feature-columns)

;;  This allows us now to set specifically the values in the conversion to numbers.
;; This is a good practice, instead of the relying on the automatic selection of the categorical mapping:

(require
         '[tech.v3.dataset.categorical :as ds-cat]
         '[tech.v3.dataset.modelling :as ds-mod]
         '[tech.v3.dataset.column-filters :as cf])


;; This gives then the selected and numeric columns like this:
(def relevant-titanic-data
  (-> titanic
      (tc/select-columns (conj categorical-feature-columns target-column))
      (ds/drop-missing)
      (ds/categorical->number [:survived] ["no" "yes"] :float64)
      (ds-mod/set-inference-target target-column)))

;;  of which we can inspect the lookup-tables
(def cat-maps
  [(ds-cat/fit-categorical-map relevant-titanic-data :sex ["male" "female"] :float64)
   (ds-cat/fit-categorical-map relevant-titanic-data :pclass [0 1 2] :float64)
   (ds-cat/fit-categorical-map relevant-titanic-data :embarked ["S" "Q" "C"] :float64)])

cat-maps

;; After the mappings are applied, we have a numeric dataset, as expected
;; by most models.
(def numeric-titanic-data
  (reduce (fn [ds cat-map]
            (ds-cat/transform-categorical-map ds cat-map))
          relevant-titanic-data
          cat-maps))
(tc/head
 numeric-titanic-data)

;; Split data into train and test set
;;  Now we split the data into train and test. By we use
;;  a :holdout strategy, so will get a single split in training an test data.
;;
(def split
  (first
   (tc/split->seq numeric-titanic-data :holdout {:seed 112723})))
split

;; ## Train a model
;; Now its time to train a model.

(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.metamorph.ml.classification]
         '[scicloj.metamorph.ml.loss :as loss])

;; ### Dummy model
;; We start with a dummy model, which simply predicts the majority class
(def dummy-model (ml/train (:train split) {:model-type :metamorph.ml/dummy-classifier}))


(def dummy-prediction
  (ml/predict (:test split) dummy-model))
;; It always predicts a single class, as expected:
(-> dummy-prediction :survived frequencies)

;;  we can calculate accuracy by using a metric after having converted
;;  the numerical data back to original (important !)
;;  We should never compare mapped columns directly.
(loss/classification-accuracy
 (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
 (:survived (ds-cat/reverse-map-categorical-xforms dummy-prediction)))
;;  It's performance is poor, even worse the coin flip.


;; ## Logistic regression
;; Next model to use is Logistic Regression
(require '[scicloj.ml.smile.classification])

(def lreg-model (ml/train (:train split) {:model-type :smile.classification/logistic-regression}))
(def lreg-prediction
  (ml/predict (:test split) lreg-model))


(loss/classification-accuracy
 (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
 (:survived (ds-cat/reverse-map-categorical-xforms lreg-prediction)))

;; Its performance is  better, 60 %

;; ## Random forrest
;; Next is random forrest
(def rf-model (ml/train (:train split) {:model-type :smile.classification/random-forest}))
(def rf-prediction
  (ml/predict (:test split) rf-model))

(loss/classification-accuracy
 (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
 (:survived (ds-cat/reverse-map-categorical-xforms rf-prediction)))
;; best so far, 71 %
;;

;;  From the logistic regression model we can get via java Interop
;;  some model explanations, for example the variable importance.

(->>
 (map
  (fn [predictor importance]
    (hash-map :predictor (-> predictor str csk/->kebab-case-keyword)
              :importance importance))

  (-> rf-model ml/thaw-model .formula .predictors)
  (-> rf-model ml/thaw-model .importance))
 (sort-by :importance)
 reverse)

;;  we can see that :sex is more important to predict :survived then
;;  :pclass and :embark

;; # Next steps
;; We could now go further and trying to improve the features / the model type
;; in order to find the best performing model for the data we have.
;; All models types have a range of configurations,
;; so called hyper-parameters. They can have as well influence on the
;; model accuracy.
;;
;; So far we used a single split into 'train' and 'test' data, so we only get
;; a point estimate of the accuracy. This should be made more robust
;; via cross-validations and using different splits of the data.
