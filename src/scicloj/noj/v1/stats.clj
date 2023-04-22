(ns scicloj.noj.v1.stats
  (:require [fastmath.stats :as stats]
            ;; [scicloj.ml.core :as ml]
            ;; [scicloj.metamorph.ml :as mmml]
            ;; [scicloj.ml.metamorph :as mlmm]
            ;; [scicloj.ml.dataset :as ds]
            [tech.v3.dataset :as tmd]
            [scicloj.ml.smile.regression :as regression]
            [scicloj.metamorph.ml :as mmml]
            [tech.v3.dataset.column-filters :as cf]
            [tech.v3.dataset.modelling :as ds-mod]
            [tech.v3.datatype.functional :as fun]
            [tablecloth.api :as tc]))

(defn round
  [n scale rm]
  (.setScale ^java.math.BigDecimal (bigdec n)
             (int scale)
             ^RoundingMode (if (instance? java.math.RoundingMode rm)
                             rm
                             (java.math.RoundingMode/valueOf
                              (str (if (ident? rm) (symbol rm) rm))))))

;; Computing correlation matrices

;; Related Zulip discussion:
;; https://clojurians.zulipchat.com/#narrow/stream/151924-data-science/topic/correlation.20matrix.20plot.20.3F

(defn calc-correlations-matrix [data cols-to-use]
  (doall
   (for [col-1 cols-to-use col-2 cols-to-use]
     {:col-1 col-1
      :col-2 col-2
      :corr
      (Float/valueOf
       (str
        (round
         (stats/pearson-correlation (data col-1) (data col-2))
         2 :DOWN)))})))

;; Multivariate linear regression

(defn linear-model [dataset target covariates]
  (let [model (-> dataset
                  (tc/select-columns (cons target covariates))
                  (ds-mod/set-inference-target target)
                  (mmml/train {:model-type
                               :smile.regression/ordinary-least-square}))
        predictions (-> dataset
                        (tc/drop-columns [target])
                        (mmml/predict model)
                        target)
        r (-> dataset
              target
              (stats/correlation predictions))]
    (-> model
        mmml/explain
        (assoc :R2 (* r r)))))

(comment
  (let [n 1000
        ws (repeatedly n rand)
        xs (range n)
        ys (map (fn [w x]
                  (+ (* 3 w)
                     (* -2 x)
                     9
                     (* 1000 (rand))))
                ws xs)]
    (-> {:w ws
         :x xs
         :y ys}
        tc/dataset
        (linear-model :y [:w :x]))))
