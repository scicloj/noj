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

(ns noj-book.chicago-bike-times
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
            [fastmath.core :as fastmath]
            [java-time.api :as java-time]))

;; ## Reading data

;; You may learn more about the [Cyclistic Bike Share 2023](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset in our [Chicago bike trips](https://scicloj.github.io/clojure-data-scrapbook/projects/geography/chicago-bikes/index.html) tutorial.

(defonce raw-trips
  (-> "https://divvy-tripdata.s3.amazonaws.com/202304-divvy-tripdata.zip"
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
      (tc/add-columns {:day-of-week (fn [ds]
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
                      [:started_at]
                      #(java-time/truncate-to % :hours))))


(-> processed-trips
    (tc/select-columns [:started_at :truncated-datetime :day :day-of-week :hour]))

;; ## The time series of hourly counts

(def hourly-time-series
  (-> processed-trips
      (tc/group-by [:truncated-datetime])
      (tc/aggregate {:n tc/row-count})
      (tc/order-by [:truncated-datetime])))

(-> hourly-time-series
    (plotly/layer-line {:=x :truncated-datetime
                        :=y :n}))

;; We can visibly see the seasonal pattern of days,
;; and maybe also some seasonality of weeks.

;; ## Analysis

;; Counts by hour

(-> processed-trips
    (tc/group-by [:hour])
    (tc/aggregate {:n tc/row-count})
    (tc/order-by [:hour])
    (plotly/layer-bar {:=x :hour
                       :=y :n}))

;; Counts by day-of-week and hour

(def i->day (comp [:Mon :Tue :Wed :Thu :Fri :Sat :Sun] dec))

(-> processed-trips
    (tc/group-by [:day-of-week :hour])
    (tc/aggregate {:n tc/row-count})
    (tc/group-by [:day-of-week])
    (tc/aggregate {:plot (fn [ds]
                           [(plotly/layer-bar
                             ds
                             {:=x :hour
                              :=y :n})])})
    (tc/order-by [:day-of-week])
    (tc/map-columns :day-of-week
                    [:day-of-week]
                    i->day)
    kind/table)


;; ## Intermediate conclusion

;; The pictures show that weekends are different from
;; weekdays in terms of the hours in which people tend to use
;; their bikes.

;; ## Exploring further - DRAFT

;; How are they different?

#_(-> processed-trips
      (tc/group-by [:day-of-week :hour])
      (tc/aggregate {:n tc/row-count})
      (tc/log :logn :n)
      (tc/add-column :predicted-logn
                     (fn [ds]
                       (-> ds
                           (ds/categorical->one-hot [:day-of-week :hour])
                           (dsmod/set-inference-target :logn)
                           (tc/drop-columns [:day-of-week-7
                                             :hour-23
                                             :n])
                           (ml/train {:model-type :fastmath/ols})
                           :model-data
                           :fitted)))
      :predicted-logn
      (tc/exp :predicted-n :predicted-logn)
      (tc/map-columns :time
                      [:day-of-week :hour]
                      (fn [dow h]
                        (+ h (* 24 (- dow 1)))))
      (tc/order-by [:time])
      (plotly/base [:=x :time])
      (plotly/layer-bar {:=y :logn})
      (plotly/layer-line {:=y :predicted-logn})
      (tc/group-by :day-of-week {:result-type :as-map})
      (->> (into (sorted-map)))
      (update-vals (fn [ds]
                     (-> ds
                         (tc/order-by [:hour])
                         (plotly/base {:=x :hour})
                         ;; (plotly/layer-bar {:=y :logn})
                         (plotly/layer-line {:=y :predicted-logn}))))
      (update-keys i->day))
