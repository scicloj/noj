(ns scicloj.noj.v1.stats
  (:require [fastmath.stats :as stats]
            [scicloj.metamorph.ml :as mmml]
            [scicloj.ml.smile.regression]
            [scicloj.noj.v1.util :as util]
            [tablecloth.api :as tc]
            [tech.v3.dataset :as tmd]
            [tech.v3.dataset.modelling :as ds-mod]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]))

;; Multivariate linear regression

(defn regression-model [dataset target features options]
  (let [mmml-model (-> dataset
                       (tc/select-columns (cons target features))
                       (ds-mod/set-inference-target target)
                       (mmml/train options))
        predict (fn [ds]
                  (-> ds
                      (tc/drop-columns [target])
                      (mmml/predict mmml-model)
                      target))
        predictions (-> dataset
                        predict)
        r (-> dataset
              target
              (stats/correlation predictions))]
    (-> mmml-model
        (assoc :explained mmml/explain
               :R2 (* r r)
               :predict predict
               :predictions predictions))))

(defn linear-regression-model [dataset target features]
  (regression-model dataset target features
                    {:model-type :smile.regression/ordinary-least-square}))

(defn add-predictions [dataset target features options]
  (let [process-fn (fn [ds]
                     (let [{:as model
                            :keys [predictions]} (-> ds
                                                     (regression-model target features options))]
                       (-> ds
                           (tc/add-column (util/concat-keywords target :prediction)
                                          (-> predictions
                                              (vary-meta
                                               assoc :model (-> model
                                                                (dissoc [:model-data
                                                                         :predict
                                                                         :predictions]))))))))]
    (if (tc/grouped? dataset)
      (tc/process-group-data dataset process-fn)
      (process-fn dataset))))
