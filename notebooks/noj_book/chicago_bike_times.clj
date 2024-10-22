;; # Analysing Chicago Bike Times

;; author: Daniel Slutsky
;; last update: 2024-10-23

;; This tutorial demonstrates a simple analysis of time patterns
;; in transportation data.

;; ## Question

;; Can we distinguish weekends from weekdays
;; in terms of the hours in which people tend
;; to use their bikes?

;; ## Setup

(ns chicago-bike-times
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.datetime :as datetime]
            [scicloj.tableplot.v1.hanami :as hanami]
            [scicloj.kindly.v4.kind :as kind]))

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
      (tc/add-columns {:hour (fn [ds]
                               (->> ds
                                    :started_at
                                    (datetime/long-temporal-field
                                     :hours)))
                       :day-of-week (fn [ds]
                                      (->> ds
                                           :started_at
                                           (datetime/long-temporal-field
                                            :day-of-week)))})))


;; ## Analysis

(-> processed-trips
    (tc/group-by [:hour])
    (tc/aggregate {:n tc/row-count})
    (tc/order-by [:hour])
    (hanami/layer-bar {:=x :hour
                       :=y :n}))


(-> processed-trips
    (tc/group-by [:day-of-week :hour])
    (tc/aggregate {:n tc/row-count})
    (tc/group-by [:day-of-week])
    (tc/process-group-data #(hanami/layer-bar
                             %
                             {:=x :hour
                              :=y :n}))
    kind/table)

;; ## Conclusion

;; Yes. Weekends are different from
;; weekdays in terms of the hours
;; in which people tend to use
;; their bikes.
