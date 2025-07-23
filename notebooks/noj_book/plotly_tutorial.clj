;; # Data Visualization with Plotly

;; author: Cvetomir Dimov

;; last change: 2025-05-13
;; ## Setup
(ns noj-book.plotly-tutorial
  (:require [scicloj.kindly.v4.kind :as kind]
            [fitdistr.core :as fd]
            [fitdistr.distributions :as fdd]
            [clojure.math :as math]))

;; ## Introduction
;; [Plotly](https://plotly.com/) is a popular data visualization library that can be accessed from multiple programming languages such as [Javascript](https://plotly.com/javascript/) and [Python](https://plotly.com/python/). There is even a [library](https://plotly.com/ggplot2/) that translates R's ggplot specifications to Plotly. Relevant to Noj, it is [one of the backends](https://scicloj.github.io/tableplot/tableplot_book.plotly_walkthrough.html) that the [Tableplot](https://scicloj.github.io/tableplot/) library supports. With the previous tutorial, we presented how to work directly with Tableplot. This has the advantage of interoperating nicely with the Noj stack. 

;; With this tutorial, we will present how to directly specify a Plotly plot from Clojure. This might come in handy if one would like to use features that are currently unsupported by Tableplot. Our task is simplified by the fact that it is [one of the data visualization kinds](https://scicloj.github.io/kindly-noted/kinds.html#plotly) supported by [Kindly](https://scicloj.github.io/kindly-noted/). There are multiple example plots at [Plotly JS's website](https://plotly.com/javascript) that can be translated to Clojure in a few simple steps.

;; ## How is a Plotly JS plot specified?

;; At the heart of a Plotly plot is the *trace*. It is a JSON data structure that contains data (e.g., values on the x- and y-axes) and a plot type (e.g., a scatter plot or a histogram) together with additional plot parameters that specify how the data should be visualized. Multiple traces can be fed to Plotly as a vector so that they are jointly visualized. By default, they are visualized at the same location, that is, they are overlayed. One can optionally specify how the traces should be laid out and fix other plot properties (e.g., the plot title or the axes ranges) in another JSON data structure called the plot *layout*.

;; ## How to specify a Plotly plot in Clojure?

;; All that is needed to specify a Plotly plot in Clojure is to translate traces and layout to Clojure maps. These then need to be set as the values of another map with keys `:data` (for the traces) and `:layout` (for the layout). The kind of this map needs to be specified as "plotly". That is all. Here we will provide three examples from the [Plotly JS website](https://plotly.com/javascript/) to showcase this procedure. 

;; ## Example 1: Three scatter plots
;; Let us start with a [scatterplot](https://plotly.com/javascript/line-and-scatter/). In this case, the plot consists of three traces. The first plots only points, the second only lines, and the third both points and lines. Translating each trace to a Clojure map is as simple removing the colon after the key and changing the key to a keyword.

(def trace11
  {:x [1, 2, 3, 4]
   :y [10, 15, 13, 17]
   :mode "markers"
   :type "scatter"})

(def trace12
  {:x [2, 3, 4, 5]
   :y [16, 5, 11, 9]
   :mode "lines"
   :type "scatter"})

(def trace13
  {:x [1, 2, 3, 4]
   :y [12, 9, 15, 12]
   :mode "lines+markers"
   :type "scatter"})

;; Just as in the JS example, the points need to be put in a vector, after which we can visualize it by specifying the kind.

(kind/plotly {:data [trace11 trace12 trace13]})

;; ## Example 2: A pie chart

;; One reason we would like to use Plotly directly is to specify plots that are currently missing from Tableplot. One such a plot is a pie chart. We will reproduce the [simplest example](https://plotly.com/javascript/pie-charts/) from the Plotly JS website. The pie chart consists of a single trace. 

(def trace21
  {:values [19, 26, 55]
   :labels ["Residential", "Non-Residential", "Utility"]
   :type "pie"})

;; You might have noticed that the previous plot was rather wide. We can specify the plot size in its layout. Here we can use the default values from the example, which makes for a proportional plot. 

(def layout2
  {:height 400
   :width 500})

(kind/plotly {:data [trace21] :layout layout2})

;; ## Example 3: 2D histogram contour plot
;; Finally, we will develop a more complex example to show how the Noj stack interplays with this approach and, also, to define a more complex layout. We will reproduce a [2D histogram contour plot with histogram subplots](https://plotly.com/javascript/2d-density-plots/) from the Plotly JS examples. This plot visualizes the bivariate distribution of two randomly generated variables. In addition, histogram subplots visualize the uni-variate distribution of each variable. Generating the plot consists of generating the random data, specifying all traces and layout, and then calling `kind/plotly`.

;; ### Random number generation

;; Our two variables, `x` and `y`, and power functions of `t`, which is uniformly distributed between -1 and 2.2.

(def t
  (->> (range 0 2001)
       (map #(/ % 2000))
       (map #(* % 2.2))
       (map #(- % 1))))

;; Both `x` and `y` are generated by adding random noise drawn from a normal distribution with a mean of 0 and SD of 0.3. We define it with the `distribution` function from the `fitdistr` package. 
(def xy-distr (fd/distribution :normal {:mu 0 :sd 0.3}))

;; `x` is `t` to the third power. 
(def x
  (map +
       (fd/->seq xy-distr (count t))
       (map #(math/pow % 3) t)))

;; `y` is `t` to the sixth power. 
(def y
  (map +
       (fd/->seq xy-distr (count t))
       (map #(math/pow % 6) t)))

;; ### Translating a Plotly specification to Clojure
;; The first trace specifies a scatterplot of the two variables:
(def trace31
  {:x x,
   :y y,
   :mode "markers",
   :name "points",
   :marker {
            :color "rgb(102,0,0)",
            :size 2,
            :opacity 0.4
            },
   :type "scatter"})

;; The second trace adds a histogram 2d contour of the same data. 

(def trace32
  {:x x,
   :y y,
   :name "density",
   :ncontours 20,
   :colorscale "Hot",
   :reversescale true,
   :showscale false,
   :type "histogram2dcontour"})

;; The third and fourth traces specify the histograms of our two variables. Note that the `:yaxis` of `trace3` and the `:xaxis` of `trace4` are not the same as those of `trace1` and `trace2`. This is because we don't want these histograms to overlap with the 2d contour plot.

(def trace33
  {:x x,
   :name "x density",
   :marker {:color "rgb(102,0,0)"},
   :yaxis "y2",
   :type "histogram"})

(def trace34
  {:y y,
   :name "y density",
   :marker {:color "rgb(102,0,0)"},
   :xaxis "x2",
   :type "histogram"})

;; The layout JSON specification is transformed to a Clojure map just as trivially. It defines two x-axis regions and two y-axis regions, which take 85% and 15% of each axis, respectively. 

(def layout3
  {:showlegend false,
   :autosize false,
   :width 600,
   :height 550,
   :margin {:t 50},
   :hovermode "closest",
   :bargap 0,
   :xaxis {:domain [0, 0.85],
           :showgrid false,
           :zeroline false},
   :yaxis {:domain [0, 0.85],
           :showgrid false,
           :zeroline false},
   :xaxis2 {:domain [0.85, 1],
            :showgrid false,
            :zeroline false},
   :yaxis2 {:domain [0.85, 1],
            :showgrid false,
            :zeroline false}})

;; Here is the final plot.

(kind/plotly {:data [trace31, trace32, trace33, trace34]
              :layout layout3})

