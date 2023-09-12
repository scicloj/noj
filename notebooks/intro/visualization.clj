(ns intro.visualization
  (:require [tablecloth.api :as tc]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis.hanami.templates :as vht]
            [scicloj.noj.v1.vis :as vis]
            [scicloj.noj.v1.stats :as stats]
            [scicloj.noj.v1.datasets :as datasets]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [hiccup.core :as hiccup]
            [clojure2d.color :as color]))


;; ## Raw html
(-> "<p>Hello, <i>Noj</i>.</p>"
    vis/raw-html)

(-> [:svg {:height 210
           :width 500}
     [:line {:x1 0
             :y1 0
             :x2 200
             :y2 200
             :style "stroke:rgb(255,0,0);stroke-width:2"}]]
    hiccup/html
    vis/raw-html)

;; ## Visualizing datases with Hanami

;; Noj offers a few convenience functions to make [Hanami](https://github.com/jsa-aerial/hanami) plotting work smoothly with [Tablecloth](https://scicloj.github.io/tablecloth/) and [Kindly](https://scicloj.github.io/kindly/).



(def random-walk
  (let [n 20]
    (-> {:x (range n)
         :y (->> (repeatedly n #(- (rand) 0.5))
                 (reductions +))}
        tc/dataset)))

;; ### A simple plot

;; We can plot a Tablecloth datasete using a Hanami template:

(-> random-walk
    (vis/hanami-plot ht/point-chart
                     {:MSIZE 200}))

;; Let us look inside the resulting vega-lite space. We can see the dataset is included as CSV:

(-> random-walk
    (vis/hanami-plot ht/point-chart
                     {:MSIZE 200})
    kind/pprint)

;; ### Additional Hanami templates

;; The `scicloj.noj.v1.vis.hanami.templates` namespace add Hanami templates to Hanami's own collection.

(-> datasets/mtcars
    (vis/hanami-plot vht/boxplot-chart
                     {:X :gear
                      :XTYPE :nominal
                      :Y :mpg}))

;; ### Layers

(-> random-walk
    (vis/hanami-layers
     {:TITLE "points and a line"}
     [(vis/hanami-plot nil
                       ht/point-chart
                       {:MSIZE 400})
      (vis/hanami-plot nil
                       ht/line-chart
                       {:MSIZE 4
                        :MCOLOR "brown"})]))

;; ### Concatenation

(-> random-walk
    (vis/hanami-vconcat
     {}
     [(vis/hanami-plot nil
                       ht/point-chart
                       {:MSIZE 400
                        :HEIGHT 100
                        :WIDTH 100})
      (vis/hanami-plot nil
                       ht/line-chart
                       {:MSIZE 4
                        :MCOLOR "brown"
                        :HEIGHT 100
                        :WIDTH 100})]))

(-> random-walk
    (vis/hanami-hconcat
     {}
     [(vis/hanami-plot nil
                       ht/point-chart
                       {:MSIZE 400
                        :HEIGHT 100
                        :WIDTH 100})
      (vis/hanami-plot nil
                       ht/line-chart
                       {:MSIZE 4
                        :MCOLOR "brown"
                        :HEIGHT 100
                        :WIDTH 100})]))

;; ### Linear regression

(-> datasets/mtcars
    (stats/add-predictions :mpg [:wt]
                           {:model-type :smile.regression/ordinary-least-square})
    (vis/hanami-layers {}
                       [(vis/hanami-plot nil
                                         ht/point-chart
                                         {:X :wt
                                          :Y :mpg
                                          :MSIZE 200
                                          :HEIGHT 200
                                          :WIDTH 200})
                        (vis/hanami-plot nil
                                         ht/line-chart
                                         {:X :wt
                                          :Y :mpg-prediction
                                          :MSIZE 5
                                          :MCOLOR "purple"
                                          :YTITLE :mpg})]))

;; ### Histogram

(-> datasets/iris
    (vis/hanami-histogram :sepal-width
                          {:nbins 10}))

;; ### Combining a few things together
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
                  (stats/add-predictions :mpg [:wt]
                                         {:model-type :smile.regression/ordinary-least-square})
                  (tc/select-columns [:gear :wt :mpg :mpg-prediction])
                  (vis/hanami-layers {:TITLE (str "grear=" group-name)}
                                     [(vis/hanami-plot nil
                                                       ht/point-chart
                                                       {:X :wt
                                                        :Y :mpg
                                                        :MSIZE 200
                                                        :MCOLOR (pallete i)
                                                        :HEIGHT 200
                                                        :WIDTH 200})
                                      (vis/hanami-plot nil
                                                       ht/line-chart
                                                       {:X :wt
                                                        :Y :mpg-prediction
                                                        :MSIZE 5
                                                        :MCOLOR (pallete i)
                                                        :YTITLE :mpg})]
                                     ))))
           (vis/hanami-vconcat nil {}))))

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
                  (vis/hanami-histogram :sepal-width
                                        {:nbins 10}))))
           (vis/hanami-vconcat nil {}))))

;; Scatterplots and regression lines again, this time using Vega-Lite for layout and coloring (using its "facet" option).

(-> datasets/mtcars
    (tc/group-by [:gear])
    (stats/add-predictions :mpg [:wt]
                           {:model-type :smile.regression/ordinary-least-square})
    (tc/ungroup)
    (tc/select-columns [:gear :wt :mpg :mpg-prediction])
    (vis/hanami-layers {}
                       [(vis/hanami-plot nil
                                         ht/point-chart
                                         {:X :wt
                                          :Y :mpg
                                          :MSIZE 200
                                          :COLOR "gear"
                                          :HEIGHT 100
                                          :WIDTH 200})
                        (vis/hanami-plot nil
                                         ht/line-chart
                                         {:X :wt
                                          :Y :mpg-prediction
                                          :MSIZE 5
                                          :COLOR "gear"
                                          :YTITLE :mpg})])
    ((fn [spec]
       {:facet {:row {:field "gear"}}
        :spec (dissoc spec :data)
        :data (:data spec)}))
    kind/vega-lite)



:bye
