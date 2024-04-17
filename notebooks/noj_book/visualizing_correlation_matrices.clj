;; # Visualizing correlation matrices (experimental 🛠) - DRAFT

;; This tutorial explores various ways to visualize a correlation matrix
;; as a heatmap.
;; It is inspired by the discussion at the [Clojurians Zulip chat](https://scicloj.github.io/docs/community/chat/):
;; [#data-science > correlation matrix plot ?](https://clojurians.zulipchat.com/#narrow/stream/151924-data-science/topic/correlation.20matrix.20plot.20.3F)

;; author: Daniel Slutsky

(ns noj-book.visualizing-correlation-matrices
  (:require [fastmath.stats]
            [tablecloth.api :as tc]
            [noj-book.datasets]))

;; ## Auxiliary functions

;; Rounding numbers:

(defn round
  [n scale rm]
  (.setScale ^java.math.BigDecimal (bigdec n)
             (int scale)
             ^RoundingMode (if (instance? java.math.RoundingMode rm)
                             rm
                             (java.math.RoundingMode/valueOf
                              (str (if (ident? rm) (symbol rm) rm))))))

;; For example (see [RoundingMode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html))

(round (/ 2.0 3) 2 :DOWN)
(round (/ 2.0 3) 2 :UP)
(round (/ 2.0 3) 2 :HALF_EVEN)

;; ## Computing a correlation matrix and representing it as a dataset:

(defn correlations-dataset [data columns-to-use]
  (let [matrix (->> columns-to-use
                    (mapv #(get data %))
                    fastmath.stats/correlation-matrix)]
    (->> matrix
         (map-indexed
          (fn [i row]
            (prn [i])
            (let [coli (columns-to-use i)]
              (->> row
                   (map-indexed
                    (fn [j corr]
                      (prn [i j])
                      (let [colj (columns-to-use j)]
                        {:i i
                         :j j
                         :coli coli
                         :colj colj
                         :corr corr
                         :corr-str (format "%.02f" corr)})))))))
         (apply concat)
         tc/dataset)))

;; For example:
(-> noj-book.datasets/iris
    (correlations-dataset [:sepal-length :sepal-width :petal-length :petal-width]))
