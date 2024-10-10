;; # Data Visualization with Echarts - DRAFT 🛠

;; In this tutorial, we explore how we can use
;; [Apache Echarts](https://echarts.apache.org/)
;; to visualize data.

(ns noj-book.echarts
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

;; ## Getting started

;; Let us look into the example from the Echarts
;; [getting started](https://echarts.apache.org/handbook/en/get-started/)
;; tutorial.

;; ### Specifying a plot

;; We may create a Clojure data structure that will match
;; the Javascript data structure in the example.
;; When we annotate it with [Kindly](https://scicloj.github.io/kindly-noted/),
;; tools such as [Clay](https://scicloj.github.io/clay)
;; will convert the Clojure data to JSON and pass them
;; to be visualized in the browser.

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data [5, 20, 36, 10, 10, 20]}]})

;; ### Styling

;; The second argument for Kindly's `kind` function
;; can be used to specify options such as the style:

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data [5, 20, 36, 10, 10, 20]}]}
 {:style {:height "200px"}})

;; ### Passing datasets

;; Now, what do we do if our data is held in a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset?

(def sales
  (-> {:item ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]
       :amount [5, 20, 36, 10, 10, 20]}
      tc/dataset
      (tc/set-dataset-name "Sales")))

sales

;; A dataset is also a map, and the keys are the column names:

(map? sales)

(keys sales)

;; So, we may extract the relevant columns and refer to them in the plot spec.

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data (:item sales)}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data (:amount sales)}]}
 {:style {:height "200px"}})

;; We may also use map [destructuring](https://clojure.org/guides/destructuring):

(let [{:keys [item amount]} sales]
  (kind/echarts
   {:tooltip {}
    :legend {:data ["sales"]}
    :xAxis {:data item}
    :yAxis {}
    :series [{:name "sales"
              :type "bar"
              :data amount}]}
   {:style {:height "200px"}}))

;; ## Common Charts

;; Ready to see more charts in action?

;; Let's explore more examples from the ECharts
;; [How To Guides](https://echarts.apache.org/handbook/en/how-to/chart-types/bar/basic-bar#multi-series-bar-chart).

;; ### Bar

;; #### Multi-series Bar Chart

;; You’ve already seen a basic bar chart in [Specifying a plot](#specifying-a-plot).
;; Now, let’s continue with a Multi-series Bar Chart. To show multiple series in
;; the same chart, you need to add one more array under the series.

(def data-for-multi-series
  (-> {:days ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
       :values-a [23, 24, 18, 25, 27, 28, 25]
       :values-b [26, 24, 18, 22, 23, 20, 27]}
      tc/dataset))

data-for-multi-series

(let [{:keys [days values-a values-b]} data-for-multi-series]
  (kind/echarts
   {:tooltip {}
    :xAxis {:data days}
    :yAxis {}
    :series [{:type "bar"
              :data values-a}
             {:type "bar"
              :data values-b}]}))

;; #### Stacked Bar Chart

;; Sometimes, we hope to not only figure series separately but also the trend
;; of the sum. It's a good choice to implement it by using the stacked bar chart.

;; We can do it by simply set the same string type value for a group of series in
;; stack.

(def data-for-stack
  (-> {:x-axis-data ["A", "B", "C", "D", "E"]
       :values-a [10, 22, 28, 43, 49]
       :values-b [5, 4, 3, 5, 10]}
      tc/dataset))

(let [{:keys [x-axis-data values-a values-b]} data-for-stack]
  (kind/echarts
   {:tooltip {}
    :xAxis {:data x-axis-data}
    :yAxis {}
    :series [{:type "bar"
              :data values-a
              :stack "x"}
             {:type "bar"
              :data values-b
              :stack "x"}]}))

;; #### Bar Racing Chart

;; Bar race is a chart that shows changes in the ranking of data over time.

;; TODO

;; #### Waterfall Chart

;; There is no waterfall series in Apache ECharts, but we can simulate the
;; effect using a stacked bar chart.

;; Assuming that the values in the data array represent an increase or decrease
;; from the previous value.

(def data-for-waterfall
  [900, 345, 393, -108, -154, 135, 178, 286, -119, -361, -203])

data-for-waterfall

;; That is, the first data is 900 and the second data 345 represents the addition
;; of 345 to 900, etc. When presenting this data as a stepped waterfall chart, we
;; can use three series: the first is a non-interactive transparent series to
;; implement the suspension bar effect, the second series is used to represent
;; positive numbers, and the third series is used to represent negative numbers.

(let [positive (map #(if (>= % 0) % "-") data-for-waterfall)
      negative (map #(if (< % 0) (- %) "-") data-for-waterfall)
      suspended-cumulative (let [cumulative-values (reductions + 0 (drop-last data-for-waterfall))]
                             (map-indexed (fn [idx val]
                                            (if (< val 0)
                                              (+ (nth cumulative-values idx) val)
                                              (nth cumulative-values idx)))
                                          data-for-waterfall))]
  (kind/echarts
   {:title {:text "Waterfall"}
    :grid {:left "3%"
           :right "4%"
           :bottom "3%"
           :containLabel true}
    :xAxis {:type "category"
            :splitLine {:show false}
            :data (map #(str "Oct/" %) (range 1 12))}
    :yAxis {:type "value"}
    :series [{:type "bar"
              :stack "all"
              :itemStyle {:normal {:barBorderColor "rgba(0,0,0,0)"
                                   :color "rgba(0,0,0,0)"}
                          :emphasis {:barBorderColor "rgba(0,0,0,0)"
                                     :color "rgba(0,0,0,0)"}}
              :data suspended-cumulative}
             {:name "positive"
              :type "bar"
              :stack "all"
              :data positive}
             {:name "negative"
              :type "bar"
              :stack "all"
              :data negative
              :itemStyle {:color "#f33"}}]}))
