;; # Ordinary least squares with interactions

;; author: Carsten Behring, Daniel Slutsky

(ns noj-book.interactions-ols
  (:require [fastmath.stats :as fmstats]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.metamorph.core :as mm]
            [scicloj.metamorph.ml :as ml]
            [scicloj.metamorph.ml.loss :as loss]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tablecloth.pipeline :as tcpipe]
            [tech.v3.dataset.modelling :as modelling]
            [scicloj.ml.smile.regression]))

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

(def additive-pipeline
  (mm/pipeline
   {:metamorph/id :model}
   (ml/model {:model-type :smile.regression/ordinary-least-square})))

(md "We evaluate it, ")
(def evaluations
  (ml/evaluate-pipelines
   [additive-pipeline]
   (tc/split->seq preprocessed-data :holdout)
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))

(md "and print the result:")
(-> evaluations flatten first :fit-ctx :model ml/thaw-model)

(md "We have the following metrics:")
(md "$RMSE$")
(-> evaluations flatten first :test-transform :metric)

(md "$R^2$")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "## Interaction effects")
(md "Now we add interaction effects to it, resulting in this model equation:
$$sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)$$")
(def pipe-interaction
  (mm/pipeline
   (tcpipe/add-column :youtube*facebook (fn [ds] (tcc/* (ds :youtube) (ds :facebook))))
   {:metamorph/id :model}(ml/model {:model-type :smile.regression/ordinary-least-square})))

(md "Again we evaluate the model,")
(def evaluations
  (ml/evaluate-pipelines
   [pipe-interaction]
   (tc/split->seq preprocessed-data :holdout)
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print it and the performance metrices:")
(-> evaluations flatten first :fit-ctx :model ml/thaw-model)

(md "As the multiplcation of `youtube*facebook` is as well statistically relevant, it
suggests that there is indeed an interaction between these 2 predictor variables youtube and facebook.")

(md "$RMSE$")
(-> evaluations flatten first :test-transform :metric)

(md "$R^2$")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "$RMSE$ and $R^2$ of the intercation model are sligtly better.

These results suggest that the model with the interaction term is better than the model that contains only main effects.
So, for this specific data, we should go for the model with the interaction model.
")
