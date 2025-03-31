;; # Creating, loading and saving datasets

;; author: Cvetomir Dimov and Daniel Slutsky

;; last change: 2025-03-30

;; ## The Tablecloth library

;; [Tablecloth](https://scicloj.github.io/tablecloth/)
;; is a table processing library
;; inspired by the dataframe ergonomics typical to the [R](https://www.r-project.org/)
;; ecosystem, specifically [Tidyverse](https://www.tidyverse.org/),
;; but offers certain advantages on top of that. 
;; It is built on top of the data structures
;; and functions of [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset),
;; a high-performance table processing library (often called TMD),
;; which is built on top of [dtype-next](https://github.com/cnuernber/dtype-next), an array-programming library.

;; You are encouraged to look into [the main documentation](https://scicloj.github.io/tablecloth/)
;; for more information. Additionally, you can look into the following resources.

;; * [Data Manipulation in Clojure Compared to R and Python](https://codewithkira.com/2024-07-18-tablecloth-dplyr-pandas-polars.html)
;; by Kira Howe (McLean) (2024-07-18)

;; * [Dealing with out-of-memory faulty csv’s with Clojure, Duckdb, and Parquet](https://lebenswelt.space/blog-posts/processing-faulty-csv-with-clojure-duckdb-parquet/)
;; by Georgy Toporkov (2024-01-22)

;; * A beginner-friendly intro by Mey Beisaron (Func Prog Sweden, 2023-03-22):

^{:kindly/kind :kind/video
  :kindly/hide-code true}
{:youtube-id "a0T_d_N7wbg"}

;; * An in-depth walkthrough by Ethan Miller (data-recur group, 2022-11-05):

^{:kindly/kind :kind/video
  :kindly/hide-code true}
{:youtube-id "kME868FvT2A"}

;; At the heart of Tablecloth are the so-called dataset
;; data structures. Datasets are table-like data structures,
;; often called data-frames in other data science platforms.

;; A lot of what we demonstrate here can also be implemented with the
;; usual Clojure data structures such as vectors and maps.
;; However, datasets offer not only performance
;; advantages in space and time, but also certain usability features,
;; which are arguably expressive and powerful.

;; We will often use [treading macros](https://clojure.org/guides/threading_macros),
;; mostly [`->`](https://clojuredocs.org/clojure.core/-%3E).
;; This approach is compatible with data science cultures such as
;; the Tidyverse in R.

;; ## Setup

;; We create a namespace and require the Tablecloth API namespaces:
;; The main Dataset API [`tablecloth.api`](https://scicloj.github.io/tablecloth/#dataset-api)
;; and the Column API [`tablecloth.column.api`](https://scicloj.github.io/tablecloth/#column-api) that we'll see below.
;; We will also use [`tech.v3.dataset.print`](https://techascent.github.io/tech.ml.dataset/tech.v3.dataset.print.html) to control printing,
;; `clojure.string` for some string processing,
;; [Kindly](https://scicloj.github.io/kindly-noted/) to control
;; the way certain things are displayed,
;; for some time calculations, and the [`datetime`](https://cnuernber.github.io/dtype-next/tech.v3.datatype.datetime.html)
;; namespace of dtype-next. 

(ns noj-book.tablecloth-table-processing
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.dataset.print :as print]
            [clojure.string :as str]
            [scicloj.kindly.v4.kind :as kind]
            [tech.v3.datatype.datetime :as datetime]))

;; ## Creating a dataset

;; Assume we have a vector of vectors representing bike trips.
;; Each trip has the type of the bike and the coordinates
;; (latitude and longitude) of the trip's start and end.
;; We can turn this data structure into a dataset:

(tc/dataset [["classic_bike" 41.906866 -87.626217 41.92393131136619 -87.63582453131676]
             ["electric_bike" 41.869312286 -87.673897266 41.8895 -87.688257]
             ["classic_bike" 41.95600355078549 -87.68016144633293 41.886875 -87.62603]]
            {:column-names [:rideable-type
                            :start-lat :start-lng
                            :end-lat :end-lng]})


;; Sometimes, data may arrive in different shapes.
;; We can also create such a dataset from a vector of maps:

(tc/dataset [{:rideable-type "classic_bike"
              :start-lat     41.906866
              :start-lng     -87.626217
              :end-lat       41.92393131136619
              :end-lng       -87.63582453131676}
             {:rideable-type "electric_bike"
              :start-lat     41.869312286
              :start-lng     -87.673897266
              :end-lat       41.8895
              :end-lng       -87.688257 }
             {:rideable-type "classic_bike"
              :start-lat     41.95600355078549
              :start-lng     -87.68016144633293
              :end-lat       41.886875
              :end-lng       -87.62603}])

;; .. and also, from a map of vectors:

(tc/dataset {:rideable-type ["classic_bike" "electric_bike" "classic_bike"]
             :start-lat     [41.906866 41.869312286 41.95600355078549]
             :start-lng     [-87.626217 -87.673897266 -87.68016144633293]
             :end-lat       [41.92393131136619 41.8895 41.886875]
             :end-lng       [-87.63582453131676 -87.688257 -87.62603]})

;; Let us hold it in a var to explore it further:

(def some-trips
  (tc/dataset {:rideable-type ["classic_bike" "electric_bike" "classic_bike"]
               :start-lat     [41.906866 41.869312286 41.95600355078549]
               :start-lng     [-87.626217 -87.673897266 -87.68016144633293]
               :end-lat       [41.92393131136619 41.8895 41.886875]
               :end-lng       [-87.63582453131676 -87.688257 -87.62603]}))

;; ## Displaying a dataset

;; In an environment compatible with the [Kindly](https://scicloj.github.io/kindly/)
;; standard, the default way a dataset is displayed is by printing it.

some-trips

;; If necessary, we may customize the printing using the [`tech.v3.dataset.print`](https://techascent.github.io/tech.ml.dataset/tech.v3.dataset.print.html) namespace of the tech.ml.dataset library.

;; For example:

(-> {:x (range 99)
     :y (repeatedly 99 rand)}
    tc/dataset
    ;; show at most 9 values
    (print/print-range 9))

;; We may also explicitly turn it into an HTML table:
(kind/table some-trips)

;; This does not matter much for now, but it can be handy when certain
;; inner values should be visualized in a certain way.

;; It is possible to use [datatables](https://datatables.net/) to reneder `kind/table`
;; and specify [datatables options](https://datatables.net/manual/options)
;; (see [the full list](https://datatables.net/reference/option/)).

(kind/table some-trips
            {:use-datatables true
             :datatables     {:scrollY 100}})

;; We may also nest the usual printed table inside other visualization
;; kinds, such as Hiccup.

(kind/hiccup
 [:div {:style {:width "60%"
                :max-height "400px"
                :overflow-x :auto
                :overflow-y :auto
                :background "floralwhite"}}
  some-trips])

;; For use in this tutorial, let us define our own customized view:

(defn compact-view [dataset]
  (kind/hiccup
   [:div {:style {:max-width "100%"
                  :max-height "400px"
                  :overflow-x :auto
                  :overflow-y :auto}}
    dataset]))

;; ## Reading datasets

;; Datasets are often read from files.

;; Let us read a file from the
;; [Chicago bike trips](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset.
;; In this case, it is a [CSV](https://en.wikipedia.org/wiki/Comma-separated_values) file
;; compressed by [gzip](https://en.wikipedia.org/wiki/Gzip), but other formats are supported as well.

;; First, let us read just a few rows:

(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 3})
     compact-view)

;; So reading a dataset is easy, but sometimes we may wish to pass a few options
;; to handle it a bit better.

;; For example, you see that by default, the column names are strings:
(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9})
     tc/column-names)

;; We can apply the `keyword` function to all of them, to conveniently have keywords instead.
(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9
                  :key-fn keyword})
     tc/column-names)

;; Even better, we may process the names to replace underscores with dashes.
(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9
                  :key-fn (fn [s]
                            (-> s
                                (str/replace #"_" "-")
                                keyword))})
     tc/column-names)

;; Also, the date-time columns are parsed as strings.
(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9
                  :key-fn (fn [s]
                            (-> s
                                (str/replace #"_" "-")
                                keyword))})
     :started-at
     tcc/typeof)

;; Let us specify our own parsing for these columns.

(def datetime-parser [:local-date-time
                      "yyyy-MM-dd HH:mm:ss"])

(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9
                  :key-fn (fn [s]
                            (-> s
                                (str/replace #"_" "-")
                                keyword))
                  ;; Note we use the original column names
                  ;; when defining the parser:
                  :parser-fn {"started_at" datetime-parser
                              "ended_at" datetime-parser}})
     :started-at
     tcc/typeof)

;; Let us now read the whole dataset and hold it in a var
;; for further exploration.
;; We use `defonce` so that next time we evaluate this
;; expression, nothing will happen.
;; This practice is handy when reading big files.

(defonce trips
  (->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
       (tc/dataset {:key-fn    (fn [s]
                                 (-> s
                                     (str/replace #"_" "-")
                                     keyword))
                    :parser-fn {"started_at" datetime-parser
                                "ended_at"   datetime-parser}})))
(compact-view
 trips)

(-> trips
    tc/info
    compact-view)

;; It is a whole month of bike trips!

;; ## What is a dataset?

;; Let us explore this data structure, our little dataset of bike trips.

;; A dataset is a value of the `Dataset` datatype defined in the tech.ml.dataset library:

(type some-trips)

;; One thing worth knowing about this datatype is that it is extended by
;; quite a few interfaces and protocols.

;; For example, it behaves as a map.

(map? some-trips)

;; The keys are the column names:
(keys some-trips)
(tc/column-names some-trips)

;; .. and the values are the columns:

(:start-lat some-trips)

;; Columns are a data structure that we will discuss in the following section.

;; ## Saving Datasets
;; We can save a dataset with `write!` as a csv or tsv file. For example:
(tc/write! some-trips "some_trips.csv")

;; Note that we can same a compressed file in format .gz directly.

(tc/write! some-trips "some_trips.tsv.gz")

;; Alternatively, one can choose the [nippy](https://github.com/taoensso/nippy) file format for better performance. It is directly supported by [tech.ml](https://techascent.github.io/tech.ml.dataset/nippy-serialization-rocks.html) and, hence, by tablecloth.
(tc/write! some-trips "some_trips.nippy")

;; ## Example Datasets

;; author: Daniel Slutsky, Ken Huang

;; ### Rdatasets
;; For our tutorials here,
;; let us fetch some datasets from [Rdatasets](https://vincentarelbundock.github.io/Rdatasets/):

(ns noj-book.datasets
  (:require [tablecloth.api :as tc]))

(def iris
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
      (tc/dataset {:key-fn keyword})
      (tc/rename-columns {:Sepal.Length :sepal-length
                          :Sepal.Width :sepal-width
                          :Petal.Length :petal-length
                          :Petal.Width :petal-width
                          :Species :species})))

iris

(def mtcars
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/mtcars.csv"
      (tc/dataset {:key-fn keyword})))

mtcars

(def scatter
  (-> "https://vincentarelbundock.github.io/Rdatasets/csv/openintro/simulated_scatter.csv"
      (tc/dataset {:key-fn keyword})))

(tc/head scatter)

;; ### Plotly
;; We can also use datasets from [Plotly Sample Datasets](https://plotly.github.io/datasets/)

(-> "https://raw.githubusercontent.com/plotly/datasets/refs/heads/master/1962_2006_walmart_store_openings.csv"
    (tc/dataset {:key-fn keyword
                 :parser-fn {:OPENDATE :string
                             :date_super :string}})
    (tc/head))

;; ### tech.ml.dataset (TMD)
;; [TMD's repo](https://github.com/techascent/tech.ml.dataset/tree/master/test/data)
;; also has some datasets that we can use:

(def stocks
  (tc/dataset
   "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
   {:key-fn keyword}))

stocks
