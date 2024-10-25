;; # Analysing Chicago Bike Times - DRAFT 🛠

;; author: Daniel Slutsky
;;
;; last update: 2024-10-23

;; This tutorial demonstrates a simple analysis of time patterns
;; in transportation data.

;; ## Question

;; Can we distinguish weekends from weekdays
;; in terms of the hours in which people tend
;; to use their bikes?

;; ## Setup

(ns noj-book/chicago-bike-times
  (:require [tablecloth.api :as tc]
            [tech.v3.dataset :as ds]
            [tech.v3.dataset.modelling :as dsmod]
            [tech.v3.datatype.datetime :as datetime]
            [tech.v3.dataset.reductions :as reductions]
            [scicloj.metamorph.ml :as ml]
            [scicloj.kindly.v4.kind :as kind]
            [clojure.string :as str]
            [scicloj.metamorph.ml.regression]
            [scicloj.tableplot.v1.hanami :as hanami]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.transform :as transform]
            [fastmath.core :as fastmath]))


;; ## Reading data

;; You may learn more about the [Cyclistic Bike Share 2023](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset in our [Chicago bike trips](https://scicloj.github.io/clojure-data-scrapbook/projects/geography/chicago-bikes/index.html) tutorial.

(defonce raw-trips
  (-> "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
      (tc/dataset {:key-fn keyword
                   :parser-fn {"started_at"
                               [:local-date-time
                                "yyyy-MM-dd HH:mm:ss"]
                               "ended_at"
                               [:local-date-time
                                "yyyy-MM-dd HH:mm:ss"]}})))

;; ## Processing data

(def processed-trips
  (-> raw-trips
      (tc/add-columns {:day (fn [ds]
                              (->> ds
                                   :started_at
                                   (datetime/long-temporal-field
                                    :days)))
                       :day-of-week (fn [ds]
                                      (->> ds
                                           :started_at
                                           (datetime/long-temporal-field
                                            :day-of-week)))
                       :hour (fn [ds]
                               (->> ds
                                    :started_at
                                    (datetime/long-temporal-field
                                     :hours)))})
      (tc/map-columns :truncated-datetime
                      [:day :hour]
                      (fn [d h]
                        (format "2023-04-%02dT%02d:00:00" d h)))))


(-> processed-trips
    (tc/select-columns [:started_at :truncated-datetime :day :day-of-week :hour]))

;; ## The time series of hourly counts

(-> processed-trips
    (tc/group-by [:truncated-datetime])
    (tc/aggregate {:n tc/row-count})
    (tc/order-by [:truncated-datetime])
    (plotly/layer-line {:=x :truncated-datetime
                        :=y :n}))

;; ## Analysis

;; Counts by hour

(-> processed-trips
    (tc/group-by [:hour])
    (tc/aggregate {:n tc/row-count})
    (tc/order-by [:hour])
    (plotly/layer-bar {:=x :hour
                       :=y :n}))

;; Counts by day-of-week and hour

(-> processed-trips
    (tc/group-by [:day-of-week :hour])
    (tc/aggregate {:n tc/row-count})
    (tc/group-by :day-of-week)
    (tc/without-grouping->
        (tc/order-by [:name]))
    (tc/process-group-data #(plotly/layer-bar
                             %
                             {:=x :hour
                              :=y :n}))
    kind/table)

;; ## Intermeidate conclusion

;; The pictutres show that weekends are different from
;; weekdays in terms of the hours in which people tend to use
;; their bikes.

;; ## Exploring further

;; How are they different?

;; (draft)

(-> processed-trips
    (tc/group-by [:day-of-week :hour])
    (tc/aggregate {:n tc/row-count})
    (tc/order-by [:day-of-week :hour])
    (ds/categorical->one-hot [:day-of-week :hour])
    (dsmod/set-inference-target :n)
    (tc/drop-columns [:day-of-week-7 :hour-23])
    (ml/train {:model-type :fastmath/ols})
    :model-data
    :residuals
    :raw)
