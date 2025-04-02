;; # Operating with columns

;; authors: Cvetomir Dimov and Daniel Slutsky

;; last change: 2025-03-30

;; ## Setup

;; For this tutorial, we will require the main Dataset API [`tablecloth.api`](https://scicloj.github.io/tablecloth/#dataset-api), and the Column API [`tablecloth.column.api`](https://scicloj.github.io/tablecloth/#column-api).

(ns noj-book.tablecloth-column-operations
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]))

;; In addition, we will load the dataset `some-trips` that we used in the previous tutorial. 

(def some-trips
  (tc/dataset {:rideable-type ["classic_bike" "electric_bike" "classic_bike"]
               :start-lat     [41.906866 41.869312286 41.95600355078549]
               :start-lng     [-87.626217 -87.673897266 -87.68016144633293]
               :end-lat       [41.92393131136619 41.8895 41.886875]
               :end-lng       [-87.63582453131676 -87.688257 -87.62603]}))

;; ## What is a column?

(:start-lat some-trips)

;; A column is a value of `Column` datatype defined in the tech.ml.dataset library:

(-> some-trips
    :start-lat
    type)

;; This datatype is also extended by quite a few interfaces and protocols.

;; For example, it is sequential.

(-> some-trips
    :start-lat
    sequential?)

;; So, we can use the sequence abstraction with it:

(->> (:start-lat some-trips)
     (take 2))

(->> (:rideable-type some-trips)
     (filter #{"classic_bike"}))

;; It is also assiciative:

(-> some-trips
    :rideable-type
    (assoc 2 "my strange and unique bike"))

;; ## Working with Columns

;; We may use Tablecloth's [Column API](https://scicloj.github.io/tablecloth/#column-api) to create and process columns.
;; For example:

(tcc/column ["classic_bike" "electrical_bike" "classic_bike"])

;; What is the average latitude where trips tend to start?
(-> some-trips
    :start-lat
    tcc/mean)

;; What is the type of elements in this Column?
(-> some-trips
    :start-lat
    tcc/typeof)

;; Let us look into our latitudes in radians rather than degrees:
(-> some-trips
    :start-lat
    (tcc/* (/ Math/PI 180)))

;; You see, columns are typed, and this has implications for both
;; for time and space performance as well as ergonomics.

;; ## The data in columns

;; You will probably not need this detail most times, but it is worth knowing
;; that the data actually held by the Column can be accessed as the `.data` field,
;; and it can be of varying data structures (see [list of all datatypes](https://scicloj.github.io/tablecloth/#datatypes)).

;; For example:

(-> some-trips
    :start-lat
    .data
    type)

(-> some-trips
    :rideable-type
    .data
    type)

(-> (range 9)
    tcc/column
    .data
    type)

;; Behind the scenes, tech.ml.dataset makes sure to use efficient data structures
;; for columns, so that, e.g., random access by index will be efficient:

(-> some-trips
    :start-lat
    (nth 2))

;; The following is quick too!
(-> (range 1000000)
    (tcc/* 1000)
    (nth 10000))

;; Here we rely on the "lazy and noncaching"
;; semantics of the undelying [dtype-next](https://github.com/cnuernber/dtype-next) library,
;; which is a topic worth its own tutorial.

;; ## Operations on columns
;; In the examples with columns above, we used `tcc/*` to multiply the values of a column by a scalar. The result was another column. This operation can be applied to multiple two (or more) columns as well, or a mixture of columns and scalars.

(tcc/* (:start-lat some-trips)
       (:end-lng some-trips)
       3
       4)

;; A [long list](https://cljdoc.org/d/scicloj/tablecloth/7.029.2/api/tablecloth.api) of other operations have been implemented, among which arithmetic operations, trigonometric functions, and various predicates. Note that the `tcc/min` and `tcc/max` functions output a column as well.

(tcc/min (:start-lat some-trips)
         (:end-lat some-trips))

;; They also work with scalar inputs.

(tcc/min (:start-lat some-trips)
         100)

(tcc/min (:start-lat some-trips)
         -100)

;; Other operations produce a scalar as an output. These include various measures of central tendency.
(-> some-trips
    :start-lat
    tcc/mean)

(-> some-trips
    :end-lat
    tcc/median)

;; Important operations in this category are functions that reduce over the columns, such as `reduce-*`, `reduce-+`, `reduce-min`,  and `reduce-max`. For example, the following can be used to find the maximum value in a column:

(-> some-trips
    :start-lng
    tcc/reduce-max)
