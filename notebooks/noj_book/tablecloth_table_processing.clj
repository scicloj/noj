;; # Table processing with Tablecloth - DRAFT 🛠

;; author: Daniel Slutsky

;; last change: 2024-11-26


;; [Tablecloth](https://scicloj.github.io/tablecloth/)
;; is a table processing library
;; inspired by the dataframe ergonomics typicall to the [R](https://www.r-project.org/)
;; ecosystem, specifically the [Tidyverse](https://www.tidyverse.org/),
;; but offers certain advantages on top of that.

;; It is built on top of the data structures
;; and functions of [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset),
;; a high-performance table processing library, but adds its own
;; concepts and functionality.

;; In this tutorial, we will see a few of the core ideas of Tablecloth.
;; You are encouraget to look into [the main documentation](https://scicloj.github.io/tablecloth/)
;; for more information.

;; ## Recommended resources

;; * [Data Manipulation in Clojure Compared to R and Python](https://codewithkira.com/2024-07-18-tablecloth-dplyr-pandas-polars.html)
;; by Kira Howe (McLean) (2024-07-18)

;; * [Dealing with out-of-memory faulty csv’s with Clojure, Duckdb, and Parquet](https://lebenswelt.space/blog-posts/processing-faulty-csv-with-clojure-duckdb-parquet/)
;; by Georgy Toporkov (2024-01-22)

;; * A beginner-friendly intro by Mey Beisaron (Func Prog Sweden, 2023-03-22):

^{:kindly/kind :kind/video
  :kindly/hide-code true}
{:youtube-id "a0T_d_N7wbg"}

;; * An in-depth warlkthrough by Ethan Miller (data-recur group, 2022-11-05):

^{:kindly/kind :kind/video
  :kindly/hide-code true}
{:youtube-id "kME868FvT2A"}

;; ## About this tutorial

;; In this tutorial, we will demonstrate the ergonomics of so-called dataset
;; datastrucutes provided by Tablecloth.

;; We will assume basic familiarity with Clojure.

;; A lot of what we demonstrate here can also be implemented with the
;; classical Clojure data strucures: vectors and maps.
;; Datasets are table-like data structures, often called data-frames
;; in other data science platforms. They provide not only performance
;; advantages in space and time, but also certain usability features,
;; which are arguably expressive and powerful.

;; We will oftwen use [treading macros](https://clojure.org/guides/threading_macros),
;; mostly [`->`](https://clojuredocs.org/clojure.core/-%3E).
;; This approach is compatible with data science cultures such as
;; the one of the Tidyverse in R.

;; ## Setup

;; We create a namespace and require the Tablecloth API namespaces:
;; The main API `tablecloth.api`
;; and the Column API `tablecloth.column.api` that we'll see below.
;; We will also use `clojure.string` for some string processing
;; and [Kindly](https://scicloj.github.io/kindly-noted/) to control
;; the way certain things are displayed.

(ns noj-book.tablecloth-table-processing
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [clojure.string :as str]
            [scicloj.kindly.v4.kind :as kind]))

;; ## Creating a dataset

;; Assume we have a vector of vectors representing bike trips.
;; Each trip has a type of bike and the coordinates of its start and end.
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

;; Let us give it a name to explore it further:

(def some-trips
  (tc/dataset {:rideable-type ["classic_bike" "electric_bike" "classic_bike"]
               :start-lat     [41.906866 41.869312286 41.95600355078549]
               :start-lng     [-87.626217 -87.673897266 -87.68016144633293]
               :end-lat       [41.92393131136619 41.8895 41.886875]
               :end-lng       [-87.63582453131676 -87.688257 -87.62603]}))

;; ## Displaying a dataset

;; In an environment compatible with the [Kindly](https://scicloj.github.io/kindly/)
;; standard, the default rendering of a dataset is by printing it.

some-trips

;; We may control the printing using the `tech.v3.dataset.print` namespace.
;; For now, the default seems good for us.

;; We may also turn it into an HTML table:
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
   [:div {:style {:width "100%"
                  :max-height "400px"
                  :overflow-x :auto
                  :overflow-y :auto}}
    dataset]))

;; ## What is a dataset?

;; Let us explore this data structure, our little dataset of bike trips.

;; A dataset is a value of `Dataset` datatype defined in the tech.ml.dataset library:

(type some-trips)

;; One thing worth knowing about this datatype is that it is extended by
;; quite a few interfaces and protocos.

;; For example, it behaves as a map.

(map? some-trips)

;; The keys are the column names:
(keys some-trips)
(tc/column-names some-trips)

;; .. and the values are the columns:

(:start-lat some-trips)

;; Now we need to discuss what columns are.

;; ## What is a column?

(:start-lat some-trips)

;; A column is a value of `Dataset` datatype defined in the tech.ml.dataset library:

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

;; We may use Tablecloth's Column API to create and process columns.
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

;; Let us look into our latitudes in radians:
(-> some-trips
    :start-lat
    (tcc/* (/ Math/PI 180)))

;; You see, columns are typed, and this has implications for both
;; for time and space performance as well as ergonomics.

;; ## The data in columns

;; You will probably not need this detail most times, but it is worth knowing
;; that the data actually held by the Column can be accessed as the `.data` field,
;; and it can be of varying data structures.

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

;; That is thanks to the "lazy and noncaching"
;; semantics of the undelying [dtype-next](https://github.com/cnuernber/dtype-next) library,
;; which is a topic worth its own tutorial.

;; ## Summarizing datasets

;; We can use the `tc/info` function to summarize a dataset:

(compact-view
 (tc/info some-trips))

;; ## Reading datasets

;; Datasets are often read from files.

;; Let us read a file from the
;; [Chicago bike trips](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset.
;; In this case, it is a [CSV](https://en.wikipedia.org/wiki/Comma-separated_values) file
;; compressed by [gzip](https://en.wikipedia.org/wiki/Gzip), but other formats are supported as well.

;; First, let us read just a few rows:

(compact-view
 (->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
      (tc/dataset {:num-rows 3})))

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
     :started-at)

;; Let us specify our own parsing for these columns.

(def datetime-parser [:local-date-time
                      "yyyy-MM-dd HH:mm:ss"])

(->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
     (tc/dataset {:num-rows 9
                  :key-fn (fn [s]
                            (-> s
                                (str/replace #"_" "-")
                                keyword))
                  :parser-fn {"started_at" datetime-parser
                              "ended-at" datetime-parser}})
     :started-at)

;; Let us now read the whole dataset and give it a name for further processing:

(def trips
  (->  "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
       (tc/dataset {:key-fn    (fn [s]
                                 (-> s
                                     (str/replace #"_" "-")
                                     keyword))
                    :parser-fn {"started_at" datetime-parser
                                "ended-at"   datetime-parser}})))
(compact-view
 trips)

(compact-view
 (tc/info trips))

;; It is a whole month of bike trips!

;; ## Getting the rows of a dataset

;; Datasets are organized by columns for efficiency,
;; making use of the knowledge of homogenous types in columns.

;; Sometimes, however, it is useful to work with rows as well.

;; The `tc/rows` function provides the rows of a dataset,
;; either as vectors or as maps.
;; Note, however, that it does not copy the data. Rather,
;; it provides a rowwise view of the columnwise dataset.

(take 2 (tc/rows trips))

(take 2 (tc/rows trips :as-maps))

;; ## Querying datasets

;; Tablecloth offers various ways to view a subset of a dataset.
;; Typically, they do not copy the data but provide views of the
;; same space in memory.

;; The first few trips:

(compact-view
 (-> trips
     tc/head))

;; The first few trips, showing just a few columns:
(-> trips
    tc/head
    (tc/select-columns [:rideable-type :started-at :ended-at]))

;; The first few trips of classical bikes,  showing just a few columns:
(-> trips
    (tc/select-rows (fn [row]
                      (-> row :rideable-type (= "classic_bike"))))
    tc/head
    (tc/select-columns [:rideable-type :started-at :ended-at]))
