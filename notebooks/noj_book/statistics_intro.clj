;; ## Distributions of single variables - DRAFT 🛠

;; author: Cvetomir Dimov and Daniel Slutsky

;; last change: 2025-02-24

;; In this tutorial, we will demonstrate how to visualize and summarize variables. 

;; ## Setup and data

;; We will use the Chicago bike trips dataset, that is read and processed at
;; the [Intro to Table Processing with Tablecloth](./noj_book.tablecloth_table_processing.html).

(ns noj-book.statistics-intro
  (:require [tablecloth.api :as tc]
            [noj-book.tablecloth-table-processing]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.stats :as stats]))

(def preprocessed-trips
  noj-book.tablecloth-table-processing/preprocessed-trips)

;; ## Checking basic statistics of variables in a dataset.

(-> preprocessed-trips
    (tc/select-columns [:hour :duration-in-seconds])
    tc/info)

;; We see that the duration in seconds has some
;; unreasonable values: trips of negative length
;; and trips which are many-hours-long.

;; Let us check how frequent that is.

(defn duration-diagnostics [{:keys [duration-in-minutes]}]
  {:negative-duration (neg? duration-in-minutes)
   :unreasonably-long-duration (> duration-in-minutes (* 2 60))})

(-> preprocessed-trips
    (tc/group-by duration-diagnostics)
    (tc/aggregate {:trips tc/row-count}))

;; ## Data cleaning

;; Let us keep only trips of reasonable duration.

(def clean-trips
  (-> preprocessed-trips
      (tc/select-rows (fn [{:keys [duration-in-minutes]}]
                        (<= 0
                            duration-in-minutes
                            40)))))

;; ## Visually exploring the distribution of variables
;; The distribution of start hour:
(-> clean-trips
    (tc/group-by [:hour])
    (tc/aggregate {:n tc/row-count})
    (plotly/layer-bar {:=x :hour
                       :=y :n}))

;; The distribution of trip duration:
;; Let us use histograms -- binning the values and counting.

(-> clean-trips
    (plotly/layer-histogram {:=x :duration-in-minutes
                             :=histogram-nbins 100}))

;; The distribution of trip duration
;; in different parts of the day:

(-> clean-trips
    (tc/map-columns :day-part
                    [:hour]
                    (fn [hour]
                      (cond (<= 6 hour 12) :morning
                            (<= 12 hour 18) :afternoon
                            (<= 18 hour 23) :evening
                            :else :night)))
    (plotly/layer-histogram {:=x :duration-in-minutes
                             :=histogram-nbins 100
                             :=color :day-part
                             :=mark-opacity 0.8}))

;; TODO: Use density estimates rather than histograms here.

;; The distribution of trip duration
;; for different bike types:

(-> clean-trips
    (plotly/layer-histogram {:=x :duration-in-minutes
                             :=histogram-nbins 100
                             :=color :rideable-type
                             :=mark-opacity 0.8}))

;; ## Describing the distribution of a continuous variable
;; The [`fastmath` library](https://generateme.github.io/fastmath/clay/core.html) contains function for computing the most common measures of sample central tendency and variability (see the [Statistics](https://generateme.github.io/fastmath/clay/stats.html) section). We will demonstrate a few. 
(-> clean-trips
    (tc/aggregate {:n tc/row-count
                   :mean-duration #(-> % :duration-in-seconds stats/mean)
                   :min-duration #(-> % :duration-in-seconds stats/minimum)
                   :max-duration #(-> % :duration-in-seconds stats/maximum)
                   :median-duration #(-> % :duration-in-seconds stats/median)
                   :q1-duration #(-> % :duration-in-seconds stats/stats-map :Q1)
                   :percentile10-duration #(-> % :duration-in-seconds (stats/percentile 10))
                   :sd-duration #(-> % :duration-in-seconds stats/stddev)
                   :cov-duration #(-> % :duration-in-seconds stats/variation)
                   :mad-duration #(-> % :duration-in-seconds stats/mad)
                   :iqr-duration #(-> % :duration-in-seconds stats/iqr)}))

;; Note that the function `stats-map` outputs many of the most commonly used statistics, including the ones computed above.
(-> clean-trips
    :duration-in-minutes
    stats/stats-map
    (select-keys [:Size :Min :Max :Range :Mean :Median :Mode :Q1 :Q3
                  :SD :Variance :MAD :SEM :IQR :Kurtosis :Skewness]))

;; Box plots and violin plots can be used to visualize several of these summary statistics.
(-> clean-trips
    (plotly/layer-boxplot
     {:=y :duration-in-minutes}))

;; These can also be produced by group to compare the distributions across different groups. 
(-> clean-trips
    (plotly/layer-violin
     {:=x :rideable-type 
      :=y :duration-in-minutes
      :=box-visible true
      :=color :rideable-type}))

;; ## Robust statistics
;; Outliers can have a significant influence on some summary values, such as mean or standard deviation. This why we cleaned the bike trips data in the first place.
;; Removing outliers can be done systematically by removing the most extreme percentage of the data.
(-> preprocessed-trips
    :duration-in-minutes
    stats/trim
    stats/stats-map)

;; A similar approach is winsorizing the data, which involves removing the extremes of the distribution and replacing their values with the most extreme remaining values. 
(-> preprocessed-trips
    :duration-in-minutes
    stats/winsor
    stats/stats-map)

;; Note that summary statistics such median, quartiles, and MAD are robust to outliers as well. 

;; ## Significance testing
;; We can test whether bike trip durations are significantly different from a value with a one sample Studend t-test. By default, the significance level is `:alpha = 0.05` and the value is `:mu = 0`. 
(-> clean-trips
    :duration-in-minutes
    (stats/t-test-one-sample))

;; `:alpha` and `:mu` can be changed as follows:
(-> clean-trips
    :duration-in-minutes
    (stats/t-test-one-sample {:alpha 0.01 :mu 10.75}))

;; We can compare the means of two groups with a two sample Student t-test. Let us compare the means of bike trips with a classic bike with those with an electric bike.
(stats/t-test-two-samples
 (-> clean-trips
     (tc/select-rows #(= "electric_bike" (:rideable-type %)))
     :duration-in-minutes)
 (-> clean-trips
     (tc/select-rows #(= "classic_bike" (:rideable-type %)))
     :duration-in-minutes))
