;; # Dataset operations

;; authors: Cvetomir Dimov and Daniel Slutsky

;; last change: 2025-03-30

;; ## Setup

;; * [`tablecloth.api`](https://scicloj.github.io/tablecloth/#dataset-api) - the main Tablecloth Dataset API 
;; * [`tablecloth.column.api`](https://scicloj.github.io/tablecloth/#column-api) - the Tablecloth Column API 
;; * [`tech.v3.dataset.print`](https://techascent.github.io/tech.ml.dataset/tech.v3.dataset.print.html) to control printing (from tech.ml.dataset)
;; * `clojure.string` for string processing
;; * `scicloj.kindly.v4.kind` of the [Kindly](https://scicloj.github.io/kindly-noted/) standard to control the way certain values are displayed
;; * [`tech.v3.datatype.datetime`](https://cnuernber.github.io/dtype-next/tech.v3.datatype.datetime.html) of date and time operations (from dtype-next) 

(ns noj-book.tablecloth-dataset-operations
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.dataset.print :as print]
            [clojure.string :as str]
            [scicloj.kindly.v4.kind :as kind]
            [java-time.api :as java-time]
            [tech.v3.datatype.datetime :as datetime]))

(def some-trips
  (tc/dataset {:rideable-type ["classic_bike" "electric_bike" "classic_bike"]
               :start-lat     [41.906866 41.869312286 41.95600355078549]
               :start-lng     [-87.626217 -87.673897266 -87.68016144633293]
               :end-lat       [41.92393131136619 41.8895 41.886875]
               :end-lng       [-87.63582453131676 -87.688257 -87.62603]}))

(def datetime-parser [:local-date-time
                      "yyyy-MM-dd HH:mm:ss"])

(def bike-trips-filename
  "202304-divvy-tripdata.zip")

(defonce trips
  (-> bike-trips-filename
      (tc/dataset {:key-fn    (fn [s]
                                (-> s
                                    (str/replace #"_" "-")
                                    keyword))
                   :parser-fn {"started_at" datetime-parser
                               "ended_at"   datetime-parser}})))

;; ## Summarizing datasets

;; We can use the `tc/info` function to summarize a dataset:

(-> some-trips
    tc/info)

;; ## Getting the rows of a dataset

;; Datasets are organized by columns for efficiency,
;; making use of the knowledge of homogenous types in columns.

;; Sometimes, however, it is useful to work with rows as well.

;; The `tc/rows` function provides the rows of a dataset,
;; either as vectors or as maps.
;; Note, however, that it does not copy the data. Rather,
;; it provides a rowwise *view* of the columnwise dataset.

(take 2 (tc/rows trips))

(take 2 (tc/rows trips :as-maps))

;; As you may know, Clojure shines in processing plain data,
;; structured or unstructured, such as vectors and maps of any content.
;; We do not lose any of that when using datasets, as we can still
;; view them as rows which are just maps or vectors.

;; ## Querying datasets

;; Tablecloth offers various ways to view a subset of a dataset.
;; Typically, they do not copy the data but provide views of the
;; same space in memory.

;; The first few trips:

(tc/head trips)

;; Just a few columns:
(-> trips
    (tc/select-columns [:rideable-type :started-at :ended-at])
    (print/print-range 5))

;; Only rows about classical bikes, and just a few columns:
(-> trips
    (tc/select-rows (fn [row]
                      (-> row :rideable-type (= "classic_bike"))))
    tc/head
    (tc/select-columns [:rideable-type :started-at :ended-at])
    (print/print-range 5))

;; ## Adding columns

;; Here we will demonstrate some of the ways to extend a dataset with new columns. For clarity, let us focus on a dataset with just a few of the columns:
(-> trips
    (tc/select-columns [:start-lat :end-lat])
    (print/print-range 5))

;; One can create new columns by applying the dataset equivalents to the column operations discussed above. These operations have the same names, but now take a dataset and the name of the new column as additional inputs. For example, the `min` function, when taken from the `tablecloth.api`, can be used as follows:

(-> trips
    (tc/select-columns [:start-lat :end-lat])
    (tc/min :min-lat [:start-lat :end-lat])
    (print/print-range 5))

;; When no built-in operations exist, we can use the functions `tc/map-columns` and `tc/add-columns`.

;; The `tc/map-columns` function is useful when one needs to apply a function
;; to the values in one or more of the existings columns, for every row.

(-> trips
    (tc/select-columns [:rideable-type :started-at :ended-at])
    (print/print-range 5)
    (tc/map-columns :duration
                    [:started-at :ended-at]
                    java-time/duration))

(-> trips
    (tc/select-columns [:rideable-type :started-at :ended-at])
    (print/print-range 5)
    (tc/map-columns :duration
                    [:started-at :ended-at]
                    java-time/duration)
    (tc/map-columns :duration-in-seconds
                    [:duration]
                    #(java-time/as % :seconds)))

;; The `tc/add-columns` function is useful when one needs to apply a function
;; to a whole dataset and return a whole column at once.
;; This combines nicely with the column API (`tcc`).

(-> trips
    (tc/select-columns [:rideable-type :started-at :ended-at])
    (print/print-range 5)
    (tc/map-columns :duration
                    [:started-at :ended-at]
                    java-time/duration)
    (tc/map-columns :duration-in-seconds
                    [:duration]
                    #(java-time/as % :seconds))
    (tc/add-column :duration-in-minutes
                   (fn [ds]
                     (-> ds
                         :duration-in-seconds
                         (tcc/* 1/60)))))

;; Let us also add a column of the hour where each
;; trip started -- an integer between 0 to 23.

;; To do that, we will first add the hour as a column.
;; Earlier, we did some time processing using the `java-time` API.
;; Now, we will demonstrate a different way, using the
;; [`datetime`](https://cnuernber.github.io/dtype-next/tech.v3.datatype.datetime.html)
;; namespace of dtype-next.
;; This namespace offers some handy functions that act on whole columns.

;; Let us keep this dataset in a var.

(def preprocessed-trips
  (-> trips
      (tc/select-columns [:rideable-type :started-at :ended-at])
      (print/print-range 5)
      (tc/map-columns :duration
                      [:started-at :ended-at]
                      java-time/duration)
      (tc/map-columns :duration-in-seconds
                      [:duration]
                      #(java-time/as % :seconds))
      (tc/add-column :duration-in-minutes
                     (fn [ds]
                       (-> ds
                           :duration-in-seconds
                           (tcc/* 1/60))))
      (tc/add-column :hour
                     (fn [ds]
                       (datetime/long-temporal-field
                        :hours
                        (:started-at ds))))))

preprocessed-trips

;; ## Grouping and summarizing
;; For how long is a bike used typically? We can answer this question by using one of the built-in summarizing operations. These are equivalent to the column operations that output a scalar value, but require the name of the column that we use as input.

(-> preprocessed-trips
    (tc/median :duration-in-minutes))

;; This gives us a summary measure over the entire dataset. Alternatively, we can group by the values in one of the columns. 

(-> preprocessed-trips
    (tc/group-by [:rideable-type])
    (print/print-range 5))

;; The resulting dataset is a dataset of a special kind, a grouped dataset.
;; Its `:data` column contains whole datasets, which are the groups.
;; In our case, these are the groups of bike trips starting in a given hour,
;; for every hour throughout the day.

;; We can then apply an operation conditional on the groups.
(-> preprocessed-trips
    (tc/group-by [:rideable-type])
    (tc/mean :duration-in-minutes))

;; More generally, we can use `tc/aggregate` to apply an arbitrary summary function.

;; How does bike usage change througout the day?

;; Let us see how the number of trips and their median length change by the hour.

;; Let us group the trips by the hour:

(-> preprocessed-trips
    (tc/group-by [:hour])
    (print/print-range 5))

;; Now, we can aggregate over the groups to recieve a summary.
;; The resulting summary dataset will no longer be a grouped dataset.
;; We will order the summary by the hour.   

(-> preprocessed-trips
    (tc/group-by [:hour])
    (tc/aggregate {:n-trips tc/row-count
                   :median-duration (fn [ds]
                                      (tcc/median (:duration-in-seconds ds)))})
    (tc/order-by [:hour])
    (print/print-range :all))

;; We can see a peak of usage between 17:00 to 18:00
;; and a possibly slight tendendcy for longer trips (in time)
;; around the afternoon hours.

;; For further examples of summarizing, see the [Tablecloth aggregate documentation](https://scicloj.github.io/tablecloth/#aggregate).

