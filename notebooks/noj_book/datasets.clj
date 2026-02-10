;; # Reading Datasets

;; author: Daniel Slutsky, Ken Huang

;; We may use various sources of datasets for our tutorials here.

(ns noj-book.datasets
  (:require [tablecloth.api :as tc]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [scicloj.kindly.v4.kind :as kind]
            ))

;; ## Reading data from files
;; Noj provides the correct setup, so that tablecloth can be used to read files in these
;; formats:
;;
;;  * CSV and TSV
;;  * Nippy
;;  * Arrow
;;  * Excel (xlsx) 
;;  * Parquet
;;
;; Mostly the file type gets detected from the filename suffix
;; (.csv, .tsv., .nippy, .xlsx, .parquet)

;; ### CVS and TSV
;; These support 
;; * direct read from local disk
;; * as gz compressed file
;; * over http (as well gz compressed)
;; * wrapped in input-stream

;; See below for examples.

;; ### Arrow
;; For arrow suffix detection is not possible, as there is no standard suffix,
;; so it must be given via option {:file-type arrow}
;;
;; See further documentation [here](https://techascent.github.io/tech.ml.dataset/tech.v3.libs.arrow.html#var-stream-.3Edataset).
(tc/dataset "data/alldtypes.arrow-feather" {:file-type :arrow})


;; ### Excel 
;; Read single sheet Excel file:
(comment
 (tc/dataset "data/singleSheet.xlsx"))

;;see documentation [here](https://techascent.github.io/tech.ml.dataset/tech.v3.libs.fastexcel.html).
;;
;; An Excel file can have various sheets, which can be accessed as a sequence
;; of datasets:
(comment
 (tech.v3.libs.fastexcel/workbook->datasets "data/twoSheets.xlsx"))
;;

;;see documentation [here](https://techascent.github.io/tech.ml.dataset/tech.v3.libs.fastexcel.html#var-workbook-.3Edatasets).


;; ### Parquet
;; Can be read by:
(comment (tc/dataset "data/userdata1.parquet"))

;; see documentation [here](https://techascent.github.io/tech.ml.dataset/tech.v3.libs.parquet.html#var-parquet-.3Eds).



;; ## Example datasets from rdatasets
;; One of the main collections of example dataset is the `rdatasets` namespace of [metamorph.ml](https://github.com/scicloj/metamorph.ml),
;; which can fetch datasets from the [Rdatasets](https://vincentarelbundock.github.io/Rdatasets/) collection.

(rdatasets/datasets-iris)

(rdatasets/ggplot2-mpg)

(rdatasets/openintro-simulated_scatter)

;; ## Example datasets from Plotly
;; We can also use datasets from [Plotly Sample Datasets](https://plotly.github.io/datasets/)

(tc/dataset
 "https://raw.githubusercontent.com/plotly/datasets/refs/heads/master/1962_2006_walmart_store_openings.csv"
 {:key-fn keyword
  :parser-fn {:OPENDATE :string
              :date_super :string}})

;; ## Example datasets from from tech.ml.dataset (TMD)
;; [TMD's repo](https://github.com/techascent/tech.ml.dataset/tree/master/test/data)
;; also has some datasets that we can use:

(tc/dataset
 "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
 {:key-fn keyword})

