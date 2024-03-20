;; # More visualization examples (experimental 🛠)

;; author: Daniel Slutsky

(ns noj-book.more-visualization
  (:require [aerial.hanami.templates :as ht]
            [clojure2d.color :as color]
            [noj-book.datasets :as datasets]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.noj.v1.stats :as stats]
            [scicloj.noj.v1.vis.hanami :as vis.hanami]
            [scicloj.noj.v1.vis.stats :as vis.stats]
            [tablecloth.api :as tc]))

;; ## Combining a few things together
;;
;; The following is inspired by the example at Plotnine's [main page](https://plotnine.readthedocs.io/en/stable/).
;; Note how we add regression lines here. We take care of layout and colouring on our side, not using Vega-Lite for that.

(let [pallete (->> :accent
                   color/palette
                   (mapv color/format-hex))]
  (-> datasets/mtcars
      (tc/group-by :gear {:result-type :as-map})
      (->> (sort-by key)
           (map-indexed
            (fn [i [group-name ds]]
              (-> ds
                  (vis.stats/linear-regression-plot
                   :mpg :wt
                   {:TITLE (str "grear=" group-name)
                    :X :wt
                    :MCOLOR (pallete i)
                    :HEIGHT 200
                    :WIDTH 200
                    :point-options {:MSIZE 200}
                    :line-options {:MSIZE 5}}))))
           (vis.hanami/vconcat nil {}))))

;; Alternatively, using a grouped dataset:

(let [pallete (->> :accent
                   color/palette
                   (mapv color/format-hex))]
  (-> datasets/mtcars
      (tc/map-columns :color [:gear] #(-> % (- 3) pallete))
      (tc/group-by [:gear])
      (vis.stats/linear-regression-plot
       :mpg :wt
       {:X :wt
        :MCOLOR {:expr "datum.color"}
        :HEIGHT 200
        :WIDTH 200
        :point-options {:MSIZE 200}
        :line-options {:MSIZE 5}})
      (tc/order-by [:gear])))

;; A similar example with histograms:

(let [pallete (->> :accent
                   color/palette
                   (mapv color/format-hex))]
  (-> datasets/iris
      (tc/group-by :species {:result-type :as-map})
      (->> (sort-by key)
           (map-indexed
            (fn [i [group-name ds]]
              (-> ds
                  (vis.stats/histogram :sepal-width
                                       {:nbins 10}))))
           (vis.hanami/vconcat nil {}))))

;; Scatterplots and regression lines again,
;; this time using Vega-Lite for layout and coloring
;; (using its "facet" option).


(-> datasets/mtcars
    (tc/group-by [:gear])
    (stats/add-predictions :mpg [:wt]
                           {:model-type :smile.regression/ordinary-least-square})
    (tc/ungroup)
    (tc/select-columns [:gear :wt :mpg :mpg-prediction])
    (vis.hanami/combined-plot
     ht/layer-chart
     {}
     :LAYER [[ht/point-chart
              {:X :wt
               :Y :mpg
               :MSIZE 200
               :COLOR "gear"
               :HEIGHT 100
               :WIDTH 200}]
             [ht/line-chart
              {:X :wt
               :Y :mpg-prediction
               :MSIZE 5
               :COLOR "gear"
               :YTITLE :mpg}]])
    ((fn [spec]
       {:facet {:row {:field "gear"}}
        :spec (dissoc spec :data)
        :data (:data spec)}))
    kind/vega-lite)
