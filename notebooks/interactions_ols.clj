(ns interactions-ols
  (:require [tablecloth.api :as tc]
            [tablecloth.pipeline :as tcpipe]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [fastmath.stats :as fmstats]
            [tech.v3.dataset.math :as std-math]
            [tech.v3.datatype.functional :as dtf]
            [scicloj.metamorph.core :as mm]
            [scicloj.metamorph.ml :as mm.ml]
            [scicloj.metamorph.ml.loss :as loss]
            [scicloj.ml.smile.regression :as regression]
            [tech.v3.dataset.metamorph :as tmd.mm]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]))

(def md
  (comp kindly/hide-code kind/md))

(md "This examples shows how to do interactions in linear regression with `metamorph.ml`.")

(md "Taking ideas from:

[Interaction Effect in Multiple Regression: Essentials](http://www.sthda.com/english/articles/40-regression-analysis/164-interaction-effect-in-multiple-regression-essentials/#comments-list) by Alboukadel Kassambara")

(defn pp-str [x]
  (with-out-str (clojure.pprint/pprint x)))

(md "First we load the data:")
(def marketing (tc/dataset "https://github.com/scicloj/datarium-CSV/raw/main/data/marketing.csv.gz" {:key-fn keyword}))

(md "## Additive model")

(md "Firts we build an additive model, which model equation is 'sales = b0 + b1 * youtube + b2 * facebook'")

(def additive-pipeline
  (mm/pipeline
   (tmd.mm/set-inference-target :sales)
   (tcpipe/drop-columns [:newspaper])
   {:metamorph/id :model}
   (mm.ml/model {:model-type :smile.regression/ordinary-least-square})))


(md "We evaluate it, ")
(def evaluations
  (mm.ml/evaluate-pipelines
   [additive-pipeline]
   (tc/split->seq marketing :holdout)
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print the result:")
(-> evaluations flatten first :fit-ctx :model mm.ml/thaw-model)

(md "We have the following metrices:")
(md "RMSE")
(-> evaluations flatten first :test-transform :metric)

(md "R2")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "## Interaction effects")
(md "Now we add interaction effects to it, resulting in this model equation: 'sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)'")
(def pipe-interaction
  (mm/pipeline
   (tcpipe/drop-columns [:newspaper])
   (tcpipe/add-column :youtube*facebook (fn [ds] (dtf/* (ds :youtube) (ds :facebook))))
   (tmd.mm/set-inference-target :sales)
   {:metamorph/id :model}(mm.ml/model {:model-type :smile.regression/ordinary-least-square})))

(md "Again we evaluate the model,")
(def evaluations
  (mm.ml/evaluate-pipelines
   [pipe-interaction]
   (tc/split->seq marketing :holdout)
   loss/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print it and the performance metrices:")
(-> evaluations flatten first :fit-ctx :model mm.ml/thaw-model)

(md "As the multiplcation of 'youtube * facebook' is as well statistically relevant, it
suggests that there is indeed an interaction between these 2 predictor variables youtube and facebook.")

(md "RMSE")
(-> evaluations flatten first :test-transform :metric)

(md "R2")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "RMSE and R2 of the intercation model are sligtly better.

These results suggest that the model with the interaction term is better than the model that contains only main effects.
So, for this specific data, we should go for the model with the interaction model.
")
