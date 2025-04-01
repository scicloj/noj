;; # Introduction to Linear Regression   

;; **last update:** 2024-12-29

;; In this tutorial, we introduce the fundamentals of [linear regression](https://en.wikipedia.org/wiki/Linear_regression),
;; guided by the
;; [In Depth: Linear Regression](https://jakevdp.github.io/PythonDataScienceHandbook/05.06-linear-regression.html)
;; chapter of the
;; [Python Data Science Handbook](https://jakevdp.github.io/PythonDataScienceHandbook/)
;; by Jake VanderPlas.

;; ## Setup

(ns noj-book.linear-regression-intro-copy
  (:require
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [tablecloth.column.api :as tcc]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.dataset.modelling :as ds-mod]
   [fastmath.ml.regression :as reg]
   [scicloj.kindly.v4.kind :as kind]
   [fastmath.random :as rand]
   [scicloj.tableplot.v1.plotly :as plotly]))

## Simple Linear Regression

We begin with the classic straight-line model: for data points $(x, y)$,
we assume there is a linear relationship allowing us to predict $y$ as
$$y = ax + b.$$
In this formulation, $a$ is the slope and $b$ is the intercept,
the point where our line would cross the $y$ axis.

;; To illustrate, we'll use Fastmath and Tablecloth to create synthetic data
;; in which the relationship is known to hold with $a=2$ and $b=-5$.

;; For each row in the dataset below, we draw $x$ uniformly from 0 to 10
;; and compute $y = ax + b$ plus an extra random noise term
;; (drawn from a standard Gaussian distribution).
;; This noise is added independently for every row.

(def simple-linear-data
  (let [rng (rand/rng 1234)
        n 50
        a 2
        b -5]
    (-> {:x (repeatedly n #(rand/frandom rng 0 10))}
        tc/dataset
        (tc/map-columns :y
                        [:x]
                        (fn [x]
                          (+ (* a x)
                             b
                             (rand/grandom rng)))))))

simple-linear-data

;; Let's plot these points using Tableplot's Plotly API.

(-> simple-linear-data
    plotly/layer-point)

;; ;; ### Regression using Fastmath

;; ;; We can now fit a linear model to the data using the Fastmath library.

;; (def simple-linear-data-model
;;   (reg/lm
;;    ;; ys - a "column" sequence of `y` values:
;;    (simple-linear-data :y)
;;    ;; xss - a sequence of "rows", each containing `x` values:
;;    ;; (one `x` per row, in our case):
;;    (-> simple-linear-data
;;        (tc/select-columns [:x])
;;        tc/rows)
;;    ;; options
;;    {:names ["x"]}))

;; (type simple-linear-data-model)

;; simple-linear-data-model

;; ;; Printing the model gives a tabular summary:
;; ;; We'll capture the printed output and display it via Kindly for cleaner formatting.

;; (kind/code
;;  (with-out-str
;;    (println
;;     simple-linear-data-model)))

;; ;; As you can see, the estimated coefficients match our intercept $b$
;; ;; and slope $a$ (the coefficient of $x$).

;; ;; ### Dataset ergonomics

;; ;; Below are a couple of helper functions that simplify how we use regression with datasets
;; ;; and display model summaries. We have similar ideas under development in the
;; ;; [Tablemath](https://scicloj.github.io/tablemath) library, but it is still in
;; ;; an experimental stage and not part of Noj yet.

;; (defn lm
;;   "Compute a linear regression model for `dataset`.
;;   The first column marked as target is the target.
;;   All the columns unmarked as target are the features.
;;   The resulting model is of type `fastmath.ml.regression.LMData`,
;;   created via [Fastmath](https://github.com/generateme/fastmath).

;;   See [fastmath.ml.regression.lm](https://generateme.github.io/fastmath/clay/ml.html#lm)
;;   for `options`."
;;   ([dataset]
;;    (lm dataset nil))
;;   ([dataset options]
;;    (let [inference-column-name (-> dataset
;;                                    ds-mod/inference-target-column-names
;;                                    first)
;;          ds-without-target (-> dataset
;;                                (tc/drop-columns [inference-column-name]))]
;;      (reg/lm
;;       ;; ys
;;       (get dataset inference-column-name)
;;       ;; xss
;;       (tc/rows ds-without-target)
;;       ;; options
;;       (merge {:names (-> ds-without-target
;;                          tc/column-names
;;                          vec)}
;;              options)))))

;; (defn summary
;;   "Generate a summary of a linear model."
;;   [lmdata]
;;   (kind/code
;;    (with-out-str
;;      (println
;;       lmdata))))

;; (-> simple-linear-data
;;     (ds-mod/set-inference-target :y)
;;     lm
;;     summary)

;; ;; ### Prediction

;; ;; Once we have a linear model, we can generate new predictions.
;; ;; For instance, let's predict $y$ when $x=3$:

;; (simple-linear-data-model [3])

;; ;; ### Displaying the regression line

;; ;; We can visualize the fitted line by adding a smooth layer to our scatter plot.
;; ;; Tableplot makes this convenient:

;; (-> simple-linear-data
;;     (plotly/layer-point {:=name "data"})
;;     (plotly/layer-smooth {:=name "prediction"}))

;; ;; Alternatively, we can build the regression line explicitly.
;; ;; We'll obtain predictions and then plot them:

;; (-> simple-linear-data
;;     (tc/map-columns :prediction
;;                     [:x]
;;                     simple-linear-data-model)
;;     (plotly/layer-point {:=name "data"})
;;     (plotly/layer-smooth {:=y :prediction
;;                           :=name "prediction"}))

;; ;; ## Multiple linear regression

;; ;; We can easily extend these ideas to multiple linear predictors.

;; (def multiple-linear-data
;;   (let [rng (rand/rng 1234)
;;         n 50
;;         a0 2
;;         a1 -3
;;         b -5]
;;     (-> {:x0 (repeatedly n #(rand/frandom rng 0 10))
;;          :x1 (repeatedly n #(rand/frandom rng 0 10))}
;;         tc/dataset
;;         (tc/map-columns :y
;;                         [:x0 :x1]
;;                         (fn [x0 x1]
;;                           (+ (* a0 x0)
;;                              (* a1 x1)
;;                              b
;;                              (rand/grandom rng)))))))

;; (def multiple-linear-data-model
;;   (-> multiple-linear-data
;;       (ds-mod/set-inference-target :y)
;;       lm))

;; (summary multiple-linear-data-model)

;; ;; Visualizing multiple dimensions is more involved. In the case of two
;; ;; features, we can use a 3D scatterplot and a 3D surface.
;; ;; Let us do that using Tableplot's Plotly API.

;; (-> multiple-linear-data
;;     (plotly/layer-point {:=coordinates :3d
;;                          :=x :x0
;;                          :=y :x1
;;                          :=z :y})
;;     (plotly/layer-surface {:=dataset (let [xs (range 11)
;;                                            ys (range 11)]
;;                                        (tc/dataset
;;                                         {:x xs
;;                                          :y ys
;;                                          :z (for [y ys]
;;                                               (for [x xs]
;;                                                 (multiple-linear-data-model
;;                                                  [x y])))}))
;;                            :=mark-opacity 0.5}))


;; ;; ## Coming soon: Polynomial regression 🛠

;; ;; ## Coming soon: One-hot encoding 🛠

;; ;; ## Coming soon: Regularization 🛠

;; ;; ## Example: Predicting Bicycle Traffic

;; ;; As in the Python Data Science Handbook, we'll try predicting the daily number
;; ;; of bicycle trips across the Fremont Bridge in Seattle. The features will include
;; ;; weather, season, day of week, and related factors.

;; ;; ### Reading and parsing data

;; (def column-name-mapping
;;   {"Fremont Bridge Sidewalks, south of N 34th St" :total
;;    "Fremont Bridge Sidewalks, south of N 34th St Cyclist West Sidewalk" :west
;;    "Fremont Bridge Sidewalks, south of N 34th St Cyclist East Sidewalk" :east
;;    "Date" :datetime})

;; (column-name-mapping
;;  "Fremont Bridge Sidewalks, south of N 34th St")

;; (def counts
;;   (tc/dataset "data/seattle-bikes-and-weather/Fremont_Bridge_Bicycle_Counter.csv.gz"
;;               {:key-fn column-name-mapping
;;                :parser-fn {"Date" [:local-date-time "MM/dd/yyyy hh:mm:ss a"]}}))

;; counts

;; (def weather
;;   (tc/dataset "data/seattle-bikes-and-weather/BicycleWeather.csv.gz"
;;               {:key-fn keyword}))

;; weather

;; ;; ### Preprocessing

;; ;; The bike counts come in hourly data, but our weather information is daily.
;; ;; We'll need to aggregate the hourly counts into daily totals before combining the datasets.

;; ;; In the Python handbook, one does:
;; ;; ```python
;; ;; daily = counts.resample('d').sum()
;; ;; ```

;; ;; Since Tablecloth's time series features are still evolving, we'll be a bit more explicit:

;; (def daily-totals
;;   (-> counts
;;       (tc/group-by (fn [{:keys [datetime]}]
;;                      {:date (datetime/local-date-time->local-date
;;                              datetime)}))
;;       (tc/aggregate-columns [:total :west :east]
;;                             tcc/sum)))

;; daily-totals

;; ;; ### Prediction by day-of-week

;; ;; Next, we'll explore a simple regression by day of week.

;; (def days-of-week
;;   [:Mon :Tue :Wed :Thu :Fri :Sat :Sun])

;; ;; We'll convert the numeric day-of-week to the corresponding keyword:

;; (def idx->day-of-week
;;   (comp days-of-week dec))

;; ;; For example,
;; (idx->day-of-week 1)
;; (idx->day-of-week 7)

;; ;; Now, let's build our dataset:

;; (def totals-with-day-of-week
;;   (-> daily-totals
;;       (tc/add-column :day-of-week
;;                      (fn [ds]
;;                        (map idx->day-of-week
;;                             (datetime/long-temporal-field
;;                              :day-of-week
;;                              (:date ds)))))
;;       (tc/select-columns [:total :day-of-week])))

;; totals-with-day-of-week

;; (def totals-with-one-hot-days-of-week
;;   (-> (reduce (fn [dataset day-of-week]
;;                 (-> dataset
;;                     (tc/add-column day-of-week
;;                                    #(-> (:day-of-week %)
;;                                         (tcc/eq day-of-week)
;;                                         ;; convert booleans to 0/1
;;                                         (tcc/* 1)))))
;;               totals-with-day-of-week
;;               days-of-week)
;;       (tc/drop-columns [:day-of-week])
;;       (ds-mod/set-inference-target :total)))

;; (-> totals-with-one-hot-days-of-week
;;     (tc/select-columns ds-mod/inference-column?))

;; ;; Since the binary columns sum to 1, they're collinear, and we won't use an intercept.
;; ;; This way, each coefficient directly reflects the expected bike count
;; ;; for that day of week.

;; (def days-of-week-model
;;   (lm totals-with-one-hot-days-of-week
;;       {:intercept? false}))

;; ;; Let's take a look at the results:

;; (-> days-of-week-model
;;     println
;;     with-out-str
;;     kind/code)

;; ;; We can clearly see weekend versus weekday differences.

;; ;; ### Coming soon: more predictors for the bike counts 🛠

