(ns noj-book.smile-regression 
  (:require
   [noj-book.render-tools :refer [render-key-info]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.toydata :as datasets]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.datatype.functional :as dtf]))

(require '[scicloj.ml.smile.regression])

;; ## Smile regression models
^:kindly/hide-code
(render-key-info ":smile.regression/elastic-net")


^:kindly/hide-code
(render-key-info ":smile.regression/gradient-tree-boost")

^:kindly/hide-code
(render-key-info ":smile.regression/lasso")

;; We use the diabetes dataset and will show how Lasso regression
;; regulates the different variables dependent of lambda.

;; First we make a function to create pipelines with different lambdas
(defn make-pipe-fn [lambda]
  (mm/pipeline
   (ds-mm/update-column :disease-progression (fn [col] (map #(double %) col)))
   (mm/lift tc/convert-types :disease-progression :float32)
   (ds-mm/set-inference-target :disease-progression)
   {:metamorph/id :model} (ml/model {:model-type :smile.regression/lasso
                                     :lambda (double lambda)})))

;; No we go over a sequence of lambdas and fit a pipeline for all off them
;; and store the coefficients for each predictor variable:
(def diabetes (datasets/diabetes-ds))
diabetes

(def coefs-vs-lambda
  (flatten
   (map
    (fn [lambda]
      (let [fitted
            (mm/fit-pipe
             diabetes
             (make-pipe-fn lambda))

            model-instance
            (-> fitted
                :model
                (ml/thaw-model))

            predictors
            (map
             #(first (.variables %))
             (seq
              (.. model-instance formula predictors)))]

        (map
         #(hash-map :log-lambda (dtf/log10 lambda)
                    :coefficient %1
                    :predictor %2)
         (-> model-instance .coefficients seq)
         predictors)))
    (range 1 100000 100))))
coefs-vs-lambda

;; Then we plot the coefficients over the log of lambda.

(kind/vega-lite
 {
  :data {:values coefs-vs-lambda}

  :width 500
  :height 500
  :mark {:type "line"}
  :encoding {:x {:field :log-lambda :type "quantitative"}
             :y {:field :coefficient :type "quantitative"}
             :color {:field :predictor}}})

;; This shows that an increasing lambda regulates more and more variables
 ;; to zero. This plot can be used as well to find important variables,
;; namely the ones which stay > 0 even with large lambda.

^:kindly/hide-code
(render-key-info ":smile.regression/ordinary-least-square")

;; In this example we will explore the relationship between the
;; body mass index (bmi) and a diabetes indicator.

;; First we load the data and split into train and test sets.
;;
^{:nextjournal.clerk/viewer :hide-result}
(def diabetes (datasets/diabetes-ds))

^{:nextjournal.clerk/viewer :hide-result}
(def diabetes-train
  (ds/head diabetes 422))

^{:nextjournal.clerk/viewer :hide-result}
(def diabetes-test
  (ds/tail diabetes 20))



;; Next we create the pipeline, converting the target variable to
;; a float value, as needed by the model.

(def ols-pipe-fn
  (mm/pipeline
   (ds-mm/select-columns [:bmi :disease-progression])
   (mm/lift tc/convert-types :disease-progression :float32)
   (ds-mm/set-inference-target :disease-progression)
   {:metamorph/id :model} (ml/model {:model-type :smile.regression/ordinary-least-square})))

;; We can then fit the model, by running the pipeline in mode :fit

(def fitted
  (mm/fit diabetes-train ols-pipe-fn))


;; Next we run the pipe-fn in :transform and extract the prediction
;; for the disease progression:
(def diabetes-test-prediction
  (-> diabetes-test
      (mm/transform-pipe ols-pipe-fn fitted)
      :metamorph/data
      :disease-progression))
diabetes-test-prediction

;; The truth is available in the test dataset.
(def diabetes-test-trueth
  (-> diabetes-test
      :disease-progression))
diabetes-test-trueth




;; The smile Java object of the LinearModel is in the pipeline as well:

(def model-instance
  (-> fitted :model  (ml/thaw-model)))

;; This object contains all information regarding the model fit
;; such as coefficients and formula:
(-> model-instance .coefficients seq)
(-> model-instance .formula str)

;; Smile generates as well a String with the result of the linear
;; regression as part of the toString() method of class LinearModel:

(kind/code
 (str model-instance))



;; This tells us that there is a statistically significant
;; (positive) correlation between the bmi and the diabetes
;; disease progression in this data.


;; At the end we can plot the truth and the prediction on the test data,
;; and observe the linear nature of the model.

(kind/vega-lite
 {:layer [
          {:data {:values (map #(hash-map :disease-progression %1 :bmi %2 :type :truth)
                               diabetes-test-trueth
                               (:bmi  diabetes-test))}

           :width 500
           :height 500
           :mark {:type "circle"}
           :encoding {:x {:field :bmi :type "quantitative"}
                      :y {:field :disease-progression :type "quantitative"}
                      :color {:field :type}}}

          {:data {:values (map #(hash-map :disease-progression %1 :bmi %2 :type :prediction)
                               diabetes-test-prediction
                               (:bmi diabetes-test))}

           :width 500
           :height 500
           :mark {:type "line"}
           :encoding {:x {:field :bmi :type "quantitative"}
                      :y {:field :disease-progression :type "quantitative"}
                      :color {:field :type}}}]})


^:kindly/hide-code
(render-key-info ":smile.regression/random-forest")

^:kindly/hide-code
(render-key-info ":smile.regression/ridge")
