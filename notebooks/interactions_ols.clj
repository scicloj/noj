(ns interactions-ols
  (:require [tablecloth.api :as tc]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [fastmath.stats :as fmstats]
            [tech.v3.dataset.math :as std-math]
            [tech.v3.datatype.functional :as dtf]
            [scicloj.metamorph.ml.toydata :as datasets]))

(md "This examples how, how to do interactions in linear regression with `scicloj.ml`")

(md "Taking ideas from: "

    "http://www.sthda.com/english/articles/40-regression-analysis/164-interaction-effect-in-multiple-regression-essentials/#comments-list")

(defn pp-str [x]
  (with-out-str (clojure.pprint/pprint x)))

(md "First we load the data:")
(def marketing (tc/dataset "https://github.com/scicloj/datarium-CSV/raw/main/data/marketing.csv.gz" {:key-fn keyword}))

(md "## Additive model")

(md "Firts we build an additive model, which model equation is 'sales = b0 + b1 * youtube + b2 * facebook'")

(def additive-pipeline
  (ml/pipeline
   (mm/set-inference-target :sales)
   (mm/drop-columns [:newspaper])
   {:metamorph/id :model}
   (mm/model {:model-type :smile.regression/ordinary-least-square})))


(md "We evaluate it, ")
(def evaluations
  (ml/evaluate-pipelines
   [additive-pipeline]
   (ds/split->seq marketing :holdout)
   ml/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print the result:")
^kind/hiccup
(text->hiccup
 (str
  (-> evaluations flatten first :fit-ctx :model ml/thaw-model str)))

(md "We have the following metrices:")
(md "RMSE")
(-> evaluations flatten first :test-transform :metric)

(md "R2")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "## Interaction effects")
(md "Now we add interaction effects to it, resulting in this model equation: 'sales = b0 + b1 * youtube + b2 * facebook + b3 * (youtube * facebook)'")
(def pipe-interaction
  (ml/pipeline
   (mm/drop-columns [:newspaper])
   (mm/add-column :youtube*facebook (fn [ds] (dtf/* (ds :youtube) (ds :facebook))))
   (mm/set-inference-target :sales)
   {:metamorph/id :model}(mm/model {:model-type :smile.regression/ordinary-least-square})))

(md "Again we evaluate the model,")
(def evaluations
  (ml/evaluate-pipelines
   [pipe-interaction]
   (ds/split->seq marketing :holdout)
   ml/rmse
   :loss
   {:other-metrices [{:name :r2
                      :metric-fn fmstats/r2-determination}]}))


(md "and print it and the performance metrices:")
^kind/hiccup
(text->hiccup
 (str
  (-> evaluations flatten first :fit-ctx :model ml/thaw-model str)))

(md "As the multiplcation of 'youtube * facebook' is as well statistically relevant, it
suggests that there is indeed an interaction between these 2 predictor variables youtube and facebook.")

(md "RMSE")
(-> evaluations flatten first :test-transform :metric)

(md "R2")
(-> evaluations flatten first :test-transform :other-metrices first :metric)

(md "RMSE and R2 of the intercation model are sligtly better."
    "These results suggest that the model with the interaction term is better than the model that contains only main effects.
So, for this specific data, we should go for the model with the interaction model.
")
