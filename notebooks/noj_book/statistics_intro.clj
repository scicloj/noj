;; ## Intro to statistics - DRAFT 🛠

;; In this tutorial, we will demonstrate some basic methods of statistics.

;; ## Setup and data

;; We will use the Chicago bike trips dataset, that is read and processed at
;; the [Intro to Table Processing with Tablecloth](./noj_book.tablecloth_table_processing.html).

(ns noj-book.statistics-intro
  (:require [tablecloth.api :as tc]
            [noj-book.tablecloth-table-processing]
            [scicloj.tableplot.v1.plotly :as plotly]))

(def preprocessed-trips
  noj-book.tablecloth-table-processing/preprocessed-trips)

;; ## Checking basic statistics of variables

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
                            (* 2 60))))))

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

