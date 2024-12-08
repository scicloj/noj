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

(defn unreasonable-trip-length? [{:keys [duration-in-seconds]}]
  (or (neg?  duration-in-seconds)
      ;; Let us consider more than three hours unreasonable.
      (> duration-in-seconds (* 3 3600))))

(-> preprocessed-trips
    (tc/group-by {:unreasoanable-trip-length
                  unreasonable-trip-length?})
    (tc/aggregate {:tripseount tc/row-count}))


;; ## Visually exploring the distribution of variables

;; Histograms:

(-> preprocessed-trips
    (plotly/layer-histogram {:=x :duration-in-seconds}))
