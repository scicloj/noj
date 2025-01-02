;; # Machine learning

;; In this tutorial we will train a simple machine learning model
;; in order to predict the survival of titanic passengers given
;; their data.

;; ## Preface: machine learning models in Noj
;;
;; ML models in Noj are available as different plugins to the 
;; `metamorph.ml` library. 

;; The `metamorph.ml` library itself has no models  (except for a linear regression model),
;; but it contains the various functions to "train" and "predict" based on data.

;; Models are available via Clojure wrappers of existing ML libraries.
;; These are currently part of Noj:

^{:kindly/hide-code true
  :kindly/kind :kind/hiccup}
(->> [[ "Tribuo" "scicloj.ml.tribuo"]
      [ "Xgboost4J" "scicloj.ml.xgboost"]
      [ "scikit-learn" "sklearn-clj"]]
     (map (fn [[library wrapper]]
            [:tr
             [:td library]
             [:td wrapper]
             ]))
     (into [:table [:tr [:th "Library" ] [:th "Clojure Wrapper"]]]))


;; These libraries do not have any functions for the models they contain.
;; `metamorph.ml` has instead of funtcions per model the concept of each model having a 
;; unique `key`, the :model-type , which needs to be given when calling 
;;`metamorph.ml/train`
;;
;; The model libraries register their models under these keys, when their main ns 
;; is `require`d. (and the model keys get printed on screen when getting registered)
;; So we cannot provide cljdoc for the models, as they do no have corresponding functions.
;;
;; Instead we provide in the the last chapters of the Noj book a complete list
;; of all models (and their keys) incl. the parameters they take with a description.
;; For some models this reference documentation contains as well code examples.
;; This can be used to browse or search for models and their parameters.

;; The Tribuo plugin and their models are special in this. 
;; It only contains 2 model types a keys,
;; namely :scicloj.ml.tribuo/classification and :scicloj.ml.tribuo/regression.
;; The model as such is encoded in the same ways as the Triuo Java libraries does this,
;; namely as a map of all Tribuo components in place, of which one is the model, 
;; the so called "Trainer", always needed and having a certin :type, the model class.
;;
;; The reference documentation therefore lists all "Trainer" and their name incl. parameters
;; It lists as well all other "Configurable" which could be refered to in a component map.


;; ## Setup

(ns noj-book.ml-basic
  (:require [tablecloth.api :as tc]
            [scicloj.metamorph.ml.toydata :as data]
            [tech.v3.dataset :as ds]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tech.v3.dataset.categorical :as ds-cat]))



^:kindly/hide-code
(require '[same.core :as same]
         '[same.compare :as compare])

^:kindly/hide-code
(defn round
  [n scale rm]
  (.setScale ^java.math.BigDecimal (bigdec n)
             (int scale)
             ^RoundingMode (if (instance? java.math.RoundingMode rm)
                             rm
                             (java.math.RoundingMode/valueOf
                              (str (if (ident? rm) (symbol rm) rm))))))


^:kindly/hide-code
(defn set-sameish-comparator! [scale]
  (same/set-comparator! (fn [a b]
                          (let [a-rounded (round a scale :HALF_UP)
                                b-rounded (round b scale :HALF_UP)]
                            (= a-rounded
                               b-rounded)))))


;; ## Inspect data
;;
;;  The titanic data is part of `metamorph.ml` and in the form of a
;;  train, test split
;;
;;  We use the `:train` part only for this tutorial.
;;
;;
;;
(->
 (data/titanic-ds-split)
 :train)

;; We use `defonce` to avoid reading
;; the files every time we evaluate
;; the namespace.
(defonce titanic-split
  (data/titanic-ds-split))

(def titanic
  (-> titanic-split
      :train
      (tc/map-columns :survived
                      [:survived]
                      (fn [el] (case el
                                 0 "no"
                                 1 "yes")))))


;;  It has various columns
(tc/column-names titanic)

;;  of which we can get some statistics
(ds/descriptive-stats titanic)

;; The data is more or less balanced across the 2 classes:
(-> titanic :survived frequencies)

;;  We will make a very simple model, which will
;;  predict the column `:survived` from columns `:sex` , `:pclass` and `:embarked`.
;;  These represent the "gender", "passenger class" and "port of embarkment".
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

;; (We discuss more about categorical mappings in [another chapter](./noj_book.prepare_for_ml.html).)

(require '[tech.v3.dataset.categorical :as ds-cat]
         '[tech.v3.dataset.modelling :as ds-mod]
         '[tech.v3.dataset.column-filters :as cf])

;; This gives then the selected and numeric columns like this:
(def relevant-titanic-data
  (-> titanic
      (tc/select-columns (conj categorical-feature-columns target-column))
      (tc/drop-missing)
      (ds/categorical->number [:survived] ["no" "yes"] :float64)
      (ds-mod/set-inference-target target-column)))

;; of which we can inspect the lookup-tables
(def cat-maps
  [(ds-cat/fit-categorical-map relevant-titanic-data :sex ["male" "female"] :float64)
   (ds-cat/fit-categorical-map relevant-titanic-data :pclass [0 1 2] :float64)
   (ds-cat/fit-categorical-map relevant-titanic-data :embarked ["S" "Q" "C"] :float64)])


cat-maps

(kindly/check =
              (map  ds-cat/map->CategoricalMap
                    [{:lookup-table {"male" 0, "female" 1}, :src-column :sex, :result-datatype :float64}
                     {:lookup-table {0 0, 1 1, 2 2, 3 3}, :src-column :pclass, :result-datatype :float64}
                     {:lookup-table {"S" 0, "Q" 1, "C" 2}, :src-column :embarked, :result-datatype :float64}]))



;; After the mappings are applied, we have a numeric dataset, as expected
;; by most models.
(def numeric-titanic-data
  (reduce (fn [ds cat-map]
            (ds-cat/transform-categorical-map ds cat-map))
          relevant-titanic-data
          cat-maps))

(tc/head
 numeric-titanic-data)

(ds/rowvecs
 (tc/head
  numeric-titanic-data))

(kindly/check
 =
 [[0.0 3.0 0.0 0.0]
  [1.0 1.0 2.0 1.0]
  [1.0 3.0 0.0 1.0]
  [1.0 1.0 0.0 1.0]
  [0.0 3.0 0.0 0.0]])

;; Split data into train and test set
;;
;; Now we split the data into train and test. By we use
;; a `:holdout` strategy, so will get a single split in training an test data.
;;
(def split
  (first
   (tc/split->seq numeric-titanic-data :holdout {:seed 112723})))

split

;; ## Train a model
;; Now its time to train a model:

(require '[scicloj.metamorph.ml :as ml]
         '[scicloj.metamorph.ml.classification]
         '[scicloj.metamorph.ml.loss :as loss])




;; ### Dummy model
;; We start with a dummy model, which simply predicts the majority class
(def dummy-model (ml/train (:train split)
                           {:model-type :metamorph.ml/dummy-classifier}))


;; TODO: Is the dummy model wrong about the majority?


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
;;  It's performance is poor, even worse than coin flip.

(kindly/check = 0.3973063973063973)

;; ## Logistic regression
;; Next model to use is Logistic Regression
(require '[scicloj.ml.tribuo])



(def lreg-model (ml/train (:train split)
                          {:model-type :scicloj.ml.tribuo/classification
                           :tribuo-components [{:name "logistic"
                                                :type "org.tribuo.classification.sgd.linear.LinearSGDTrainer"}]
                           :tribuo-trainer-name "logistic"}))

(def lreg-prediction
  (ml/predict (:test split) lreg-model))


(loss/classification-accuracy
 (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
 (:survived (ds-cat/reverse-map-categorical-xforms lreg-prediction)))

(kindly/check = 0.7373737373737373)
;; Its performance is  better, 73 %

;; ## Random forest
;; Next is random forest
(def rf-model (ml/train (:train split) {:model-type :scicloj.ml.tribuo/classification
                                        :tribuo-components [{:name "random-forest"
                                                             :type "org.tribuo.classification.dtree.CARTClassificationTrainer"
                                                             :properties {:maxDepth "8"
                                                                          :useRandomSplitPoints "false"
                                                                          :fractionFeaturesInSplit "0.5"}}]
                                        :tribuo-trainer-name "random-forest"}))
(def rf-prediction
  (ml/predict (:test split) rf-model))

^:kindly/hide-code
(kind/hidden
 (set-sameish-comparator! 1))

;; First five prediction including the probability distributions
;; are
(-> rf-prediction
    (tc/head)
    (tc/rows))

(kindly/check same/ish?
              [[0.0 0.64 0.35]
               [0.0 0.57 0.42]
               [0.0 0.85 0.14]
               [0.0 0.88 0.11]
               [0.0 0.88 0.11]])



(loss/classification-accuracy
 (:survived (ds-cat/reverse-map-categorical-xforms (:test split)))
 (:survived (ds-cat/reverse-map-categorical-xforms rf-prediction)))

(kindly/check
 = 0.7878787878787878)

;; best so far, 78 %
;;

;; TODO: Extract feature importance.

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



