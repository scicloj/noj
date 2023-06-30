(ns intro.visualization
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis :as vis]
            [scicloj.noj.v1.stats :as stats]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kindly.v3.kind :as kind]
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

(def dataset1
  (-> {:x (range 9)
       :y (map +
               (range 9)
               (repeatedly 9 rand))}
      tc/dataset))

;; ### A simple plot

;; We can plot a Tablecloth datasete using a Hanami template:

(-> dataset1
    (vis/hanami-plot ht/point-chart
                     {:MSIZE 200}))

;; Let us look inside the resulting vega-lite space. We can see the dataset is included as CSV:

(-> dataset1
    (vis/hanami-plot ht/point-chart
                     {:MSIZE 200})
    kind/pprint)

;; ### Layers

(-> dataset1
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

(-> dataset1
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

(-> dataset1
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

;; ### Combining a few things together
;;
;; The following is inspired by the example at Plotnine's [main page](https://plotnine.readthedocs.io/en/stable/).
;; Note how we add regression lines here.

(def mtcars
  (-> "data/mtcars.csv"
      (tc/dataset {:key-fn keyword})))

(let [pallete (->> :accent
                   color/palette
                   (mapv color/format-hex))]
  (-> mtcars
      (tc/group-by :gear {:result-type :as-map})
      (->> (sort-by key)
           (map-indexed
            (fn [i [group-name ds]]
              (-> ds
                  (stats/add-predictions :mpg [:wt]
                                         {:model-type :smile.regression/ordinary-least-square})
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
           (vis/hanami-hconcat nil {}))))

:bye
