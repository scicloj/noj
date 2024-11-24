;; # Table processing with Tablecloth

;; author: Daniel Slutsky

;; last change: 2024-11-24


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

;; ## Setup

;; We create a namespace and require the Tablecloth API namespaces:
;; The main API `tablecloth.api`
;; and the Column API `tablecloth.column.api` that we'll see below.

(ns noj-book.tablecloth-table-processing 
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]))

;; ## About this tutorial

;; In this tutorial, we will demonstrate the ergonomics of so-called dataset
;; datastrucutes provided by Tablecloth.

;; A lot of what we demonstrate can also be implemented with the
;; classical Clojure data strucures: vectors and maps.
;; Datasets are table-like data structures, often  called data-frames
;; in other data science platforms. They provide not only performance
;; advantages in memory and time, but also certain usability features,
;; which are arguably expressive and powerful.

;; We will oftwen use [treading macros](https://clojure.org/guides/threading_macros),
;; mostly [`->`](https://clojuredocs.org/clojure.core/-%3E).
;; This approach is compatible with data science cultures such as
;; the one of the Tidyverse in R.

;; ## Creating datasets

;; Assume we have a Clojure data structure representing bike trips.
;; Each trip has a type of bike and the coordinates of its start and end.

(def bike-trips-as-vectors 
  [["classic_bike" 41.906866 -87.626217 41.92393131136619 -87.63582453131676]
   ["electric_bike" 41.869312286 -87.673897266 41.8895 -87.688257]
   ["classic_bike" 41.95600355078549 -87.68016144633293 41.886875 -87.62603]])

;; We can turn this data structure into a dataset:

(-> bike-trips-as-vectors
    tc/dataset)

;; We may add column names:

(-> bike-trips-as-vectors
    (tc/dataset {:column-names [:rideable_type
                                :start_lat :start_lng
                                :end_lat :end_lng]}))


;; We may also


;; ## Reading datasets

;; Datasets are oftewn read from files.

;; Let us read a file from the 
;; [Chicago bike trips](https://www.kaggle.com/datasets/godofoutcasts/cyclistic-bike-share-2023) dataset.
;; In this case, it is a [CSV](https://en.wikipedia.org/wiki/Comma-separated_values) file 
;; compressed by [gzip](https://en.wikipedia.org/wiki/Gzip), but other formats are supported as well.

(def trips
  (tc/dataset "data/chicago-bikes/202304_divvy_tripdata.csv.gz"
              {:key-fn keyword}))

