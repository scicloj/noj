;; # Ordinary least squares with interactions

;; author: Carsten Behring, Daniel Slutsky

(ns noj-book.interactions-ols
  (:require [fastmath.stats :as fmstats]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.metamorph.core :as mm]
            [scicloj.metamorph.ml :as ml]
            [scicloj.metamorph.ml.loss :as loss]
            [scicloj.metamorph.ml.regression]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tablecloth.pipeline :as tcpipe]
            [tech.v3.dataset.modelling :as modelling]
            [scicloj.ml.tribuo]
            [scicloj.metamorph.ml.design-matrix :as dm]))

^:kindly/hide-code
(def md
  (comp kindly/hide-code kind/md))

(md "This examples shows how to do interactions in linear regression with `metamorph.ml`.")

(md "Taking ideas from: [Interaction Effect in Multiple Regression: Essentials](http://www.sthda.com/english/articles/40-regression-analysis/164-interaction-effect-in-multiple-regression-essentials/) by Alboukadel Kassambara")

(md "First we load the data:")
(def marketing
  (tc/dataset "https://github.com/scicloj/datarium-CSV/raw/main/data/marketing.csv.gz"
              {:key-fn keyword}))

(md "and do some preprocessing to set up the regression:")
(def preprocessed-data
  (-> marketing
      (tc/drop-columns [:newspaper])
      (modelling/set-inference-target :sales)))

(md "## Additive model")
(md "First we build an additive model, which model equation is
$$sales = b0 + b1 * youtube + b2 * facebook$$")

(def linear-model-config {:model-type :fastmath/ols})

(def additive-pipeline
  (mm/pipeline
   {:metamorph/id :model}
   (ml/model linear-model-config)))

(md "We evaluate it, ")
(def evaluations
  (ml/evaluate-pipelines
   [additive-pipeline]
   (tc/split->seq preprocessed-data
                  :holdout
                  {:seed 112723})
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))

(md "and print the resulting model:
(note that the `:sales` term means the intercept `b0`)")
(md "(note that )")
(-> evaluations flatten first :fit-ctx :model ml/tidy)

(md "We have the following metrics:")
(md "$RMSE$")
(-> evaluations flatten first :test-transform :metric)
(kindly/check = 1.772159024927988)

(md "$R^2$")
(-> evaluations flatten first :test-transform :other-metrices first :metric)
(kindly/check = 0.9094193687523886)

(md "## Interaction effects")
(md "Now we add interaction effects to it, resulting in this model equation:
$$sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)$$")
(def pipe-interaction
  (mm/pipeline
   (tcpipe/add-column :youtube*facebook (fn [ds] (tcc/* (ds :youtube) (ds :facebook))))
   {:metamorph/id :model} (ml/model linear-model-config)))

(md "Again we evaluate the model,")
(def evaluations
  (ml/evaluate-pipelines
   [pipe-interaction]
   (tc/split->seq preprocessed-data
                  :holdout
                  {:seed 112723})
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print it and the performance metrics:")
(-> evaluations flatten first :fit-ctx :model ml/tidy)

(md "As the multiplcation of `youtube*facebook` is as well statistically relevant, it
suggests that there is indeed an interaction between these 2 predictor variables youtube and facebook.")

(md "$RMSE$")
(-> evaluations flatten first :test-transform :metric)
(kindly/check = 0.933077510748531)

(md "$R^2$")
(-> evaluations flatten first :test-transform :other-metrices first :metric)
(kindly/check = 0.9747551116991899)

(md "$RMSE$ and $R^2$ of the intercation model are sligtly better.

These results suggest that the model with the interaction term is better than the model that contains only main effects.
So, for this specific data, we should go for the model with the interaction model.
")


;; ## use design matric
;; Since `metamorph.ml 0.9.0` we have a simpler way to express the same inteactions as before.
;;
;; We can express the same formula 
;; $$sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)$$
;; by specifying a design matrix.


(require '[scicloj.metamorph.ml.design-matrix :as dm])

(def dm
  (dm/create-design-matrix 
   preprocessed-data
   [:sales]                                         ;; predictor
   [
    [:youtube '(identity youtube)]                  ;; youtube stays as-is
    [:facebook '(identity facebook)]                ;; facebook stays as-is
    [:youtube*facebook '(* youtube facebook)]       ;; new term is created
    
    ]      

   ))


;; The result of the `create-design-matrix` function is directly "ready" to be used
;; without any further preprocessing:
;; - only specified terms are present
;; - all numeric
;; - predictor is "marked" as such 
;; all present terms are added

dm

;; Having such numeric dataset the pipeline is "minimal", only containing the model:

(def pipe-mode-only
  (mm/pipeline
   {:metamorph/id :model} (ml/model linear-model-config)))

(def evaluations-dm
  (ml/evaluate-pipelines
   [pipe-mode-only]
   (tc/split->seq dm
                  :holdout
                  {:seed 112723})
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))

;; we get the same metrices as before, ()as it is the same model specification):

(md "$RMSE$")
(-> evaluations-dm flatten first :test-transform :metric)
(kindly/check = 0.933077510748531)

(md "$R^2$")
(-> evaluations-dm flatten first :test-transform :other-metrices first :metric)
(kindly/check = 0.9747551116991899)

