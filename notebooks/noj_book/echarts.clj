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
            :type :bar
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
            :type :bar
            :data [5, 20, 36, 10, 10, 20]}]}
 {:style {:height "200px"}})

;; Note that these styles are on Kindly's side,
;; so they can't instruct on what styles would be applied to charts themselves.

;; ### ECharts' Option Object
;; It looks like there isn't docs yet about the option object passed to
;; [setOption](https://echarts.apache.org/en/api.html#echartsInstance.setOption)
;; at Echarts' website,
;; and yet it's important to know what options are available out there.

;; So, here we've collected some info about keys and values of this vital object:
;; - :xAxis, whose value is an object with these keys:
;;   - :type, its value could be:
;;     - :category
;;     - :time
;; - :yAxis, whose value is an object with these keys:
;;   - :type, its value could be:
;;     - :value
;; - :series, it contains an array of data series, each element of which contains:
;;   - :type, it specifies what type of chart to make and the value could be:
;;     - :bar, a bar chart
;;     - :line, a line chart
;;   - :symbol, the symbol shown for a data point, which by default is a tiny white circle, its value could be:
;;     - :none, just hide the symbol
;; - :title, things about the chart title, whose value is an object with keys:
;;   - :text, the title text
;; - :legend, chart legend
;;   - :data, whose value is an array of legend names
;; - :tooltip, it would pop up a tooltip when your pointer hover over data points in the chart, which is something you definitely want to have.
;;   - :trigger, whose value could be:
;;     - :axis, show all series data of the current X on the tooltip.
;;   - :order, by what order to show the values, which could be one of:
;;     - :valueDesc

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
            :type :bar
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
              :type :bar
              :data amount}]}
   {:style {:height "200px"}}))

;; ## Common Charts

;; Ready to see more charts in action?

;; Let's explore more examples from the ECharts
;; [How To Guides](https://echarts.apache.org/handbook/en/how-to/chart-types/bar/basic-bar#multi-series-bar-chart).

;; To make it work in Clay, you may also find it helpful to first try out each chart
;; by clicking into the ones interesting to you on
;; [Echarts' Examples Page](https://echarts.apache.org/examples/en/index.html),
;; where it lists all of the charts, so that you can get a sense of how it works using JSON.

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
    :series [{:type :bar
              :data values-a}
             {:type :bar
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
    :series [{:type :bar
              :data values-a
              :stack "x"}
             {:type :bar
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
    :xAxis {:type :category
            :splitLine {:show false}
            :data (map #(str "Oct/" %) (range 1 12))}
    :yAxis {:type :value}
    :series [{:type :bar
              :stack "all"
              :itemStyle {:normal {:barBorderColor "rgba(0,0,0,0)"
                                   :color "rgba(0,0,0,0)"}
                          :emphasis {:barBorderColor "rgba(0,0,0,0)"
                                     :color "rgba(0,0,0,0)"}}
              :data suspended-cumulative}
             {:name "positive"
              :type :bar
              :stack "all"
              :data positive}
             {:name "negative"
              :type :bar
              :stack "all"
              :data negative
              :itemStyle {:color "#f33"}}]}))

;; ### Line
;; #### Dataset Preparation
;; Before we plot any line charts, it's helpful to prepare the dataset first.

(def stocks
  (tc/dataset
   "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
   {:key-fn keyword}))

;; This dataset originally contains three columns:
(tc/head stocks)

;; To make it better serve this tutorial, let's widen it:
(def reshaped-stocks
  (-> stocks
      (tc/pivot->wider [:symbol] [:price] {:drop-missing? false})
      (tc/rename-columns keyword)))

reshaped-stocks

;; As you can see, now it has a date column and a few other columns for every company's stock price.

;; #### Basic Line Chart

;; First let's try it out with some simple data manually:

(kind/echarts
 {:tooltip {}
  :xAxis {:type :category
          :data ["A" "B" "C"]}
  :yAxis {:type :value}
  :series [{:type :line
            :data [120 200 150]}]})

;; Now, we can try the prepared dataset to see how it goes:

(kind/echarts
 {:tooltip {}
  :xAxis {:type :category
          :data (tc/column reshaped-stocks :date)}
  :yAxis {:type :value}
  :series [{:type :line
            :data (tc/column reshaped-stocks :MSFT)}]})

;; #### Stacked Line Chart

;; Now let's stack a few more lines onto the same chart by adding more data `series`:

(kind/echarts
 {:xAxis {:type :category
          :data (tc/column reshaped-stocks :date)}
  :yAxis {:type :value}
  :series [{:type :line
            :data (tc/column reshaped-stocks :MSFT)}
           {:type :line
            :data (tc/column reshaped-stocks :AMZN)}]})

;; So far so good. But it's confusing without legend for these lines, so let's add it.

(kind/echarts
 {:xAxis {:type :category
          :data (tc/column reshaped-stocks :date)}
  :yAxis {:type :value}
  :legend {:data [:MSFT :AMZN]}
  :series [{:type :line
            :data (tc/column reshaped-stocks :MSFT)
            :name :MSFT}
           {:type :line
            :data (tc/column reshaped-stocks :AMZN)
            :name :AMZN}]})

;; Please note that you can toggle each line on and off by clicking its legend, which is a really nice feature.

;; It's a bit tedious to have column names here and there,
;; so we can define a helper function here to make life easier (maybe someday we can expand `kind` with this):

(defn echarts-line
  "Return a line chart as echart.
  - `ds` the dataset.
  - `x-col` the column name of the dataset for the x axis.
  - `y-cols` the column names for the data series.
  - `series-fn` the function to add more info to a series.
  "
  ([ds x-col y-cols]
   (echarts-line ds x-col y-cols nil))
  ([ds x-col y-cols series-fn]
   (kind/echarts
    {:xAxis {:type :category
             :data (tc/column ds x-col)}
     :yAxis {:type :value}
     :legend {:data y-cols}
     :tooltip {}
     :series (->> y-cols
                  (map (fn [col]
                         (let [series {:type :line
                                       :data (tc/column ds col)
                                       :name col}]
                           (if series-fn
                             (series-fn series)
                             series)))))})))

;; Now, if you want to have a single line chart, you can just do this:

(echarts-line reshaped-stocks :date [:AMZN])

;; Or a stacked line chart? No problem, just add more columns:

(echarts-line reshaped-stocks :date [:AMZN :GOOG])

;; So it looks like "Basic Line" and "Stacked Line" are just the same thing,
;; the only difference lies on how many lines we want to plot.

;; #### Smooth Line

;; You can smooth the line a little bit if you think it's too sharp:

(echarts-line reshaped-stocks :date [:AMZN :GOOG] #(assoc % :smooth true))

;; #### Area Chart

;; Associating the data series with a `areaStyle` will make it an area chart.

(let [colors {:AMZN "#9b59b6"
              :MSFT "#3498db"}]
  (echarts-line reshaped-stocks
                :date
                [:AMZN :MSFT]
                #(assoc % :areaStyle {:color (get colors (:name %))})))

;; #### Step Line Chart
;; Attach an extra `:step` attribute to each series will make it a step chart.
;; Depending on where you want the change occur on chart for every step line,
;; you can specify `:start`, `:middle`, or `:end`.

(echarts-line (tc/head reshaped-stocks)
              :date
              [:AMZN :MSFT]
              #(assoc % :step (if (= (:name %) :AMZN)
                                :middle
                                :start)))
