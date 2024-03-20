;; # Statistical Visualization

(ns noj-book.statistical-visualization
  (:require [tablecloth.api :as tc]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis.hanami.templates :as vht]
            [scicloj.noj.v1.vis.hanami :as vis.hanami]
            [scicloj.noj.v1.vis.stats :as vis.stats]
            [scicloj.noj.v1.stats :as stats]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [hiccup.core :as hiccup]
            [clojure2d.color :as color]
            [noj-book.datasets :as datasets]))

;; ## Linear regression

(-> datasets/mtcars
    (stats/add-predictions :mpg [:wt]
                           {:model-type :smile.regression/ordinary-least-square})
    (vis.hanami/combined-plot
     ht/layer-chart
     {:X :wt
      :MSIZE 200
      :HEIGHT 200}
     :LAYER [[ht/point-chart
              {:Y :mpg
               :WIDTH 200}]
             [ht/line-chart
              {:Y :mpg-prediction
               :MSIZE 5
               :MCOLOR "purple"
               :YTITLE :mpg}]]))

;; Alternatively:

(-> datasets/mtcars
    (vis.stats/linear-regression-plot
     :mpg :wt
     {:HEIGHT 200
      :WIDTH 200
      :point-options {:MSIZE 200}
      :line-options {:MSIZE 5
                     :MCOLOR "purple"}}))

;; And in a grouped dataset case:

(-> datasets/mtcars
    (tc/group-by [:gear])
    (vis.stats/linear-regression-plot
     :mpg :wt
     {:HEIGHT 200
      :WIDTH 200
      :point-options {:MSIZE 200}
      :line-options {:MSIZE 5
                     :MCOLOR "purple"}}))


;; ## Histogram

;; A [histogram](https://en.wikipedia.org/wiki/Histogram) groups values in bins,
;; counts them,
;; and creates a corresponding bar-chart.
;;
;; The `vis.stats/histogram` functions does that behind the scenes,
;; and generates a Vega-Lite spec using Hanami.

(-> datasets/iris
    (vis.stats/histogram :sepal-width
                         {:nbins 10}))

(-> datasets/iris
    (vis.stats/histogram :sepal-width
                         {:nbins 10})
    kind/pprint)

;; The resulting spec can be customized further:

(-> datasets/iris
    (vis.stats/histogram :sepal-width
                         {:nbins 10})
    ;; varying the resulting vega-lite spec:
    (assoc :height 125
           :width 175))

:bye
