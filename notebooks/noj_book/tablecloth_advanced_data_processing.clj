;; # Advanced Table Processing with Tablecloth - draft 🛠

;; authors: Cvetomir Dimov and Daniel Slutsky

;; last change: 2025-01-15

;; ## Setup

(ns noj-book.tablecloth-advanced-table-processing
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.dataset.print :as print]
            [clojure.string :as str]
            [scicloj.kindly.v4.kind :as kind]
            [java-time.api :as java-time]
            [tech.v3.datatype.datetime :as datetime]))

;; ## Pivoting

;; The following is inpired by [R4DS](https://r4ds.had.co.nz) [Tidy data / Pivoting](https://r4ds.had.co.nz/tidy-data.html#pivoting).

(def table4a
  (tc/dataset
   [["Aghanistan" 745 2666]
    ["Brazil" 37737 80488]
    ["China" 212258 213766]]
   {:column-names [:country 1999 2000]}))

(def table4b
  (tc/dataset
   [["Aghanistan" 19987071 20595360]
    ["Brazil" 172006362 174504898]
    ["China" 1272915272 1280428583]]
   {:column-names [:country 1999 2000]}))

(-> table4a
    (tc/pivot->longer [1999 2000]
                      {:target-columns :year
                       :value-column-name :cases}))

(-> table4a
    (tc/pivot->longer (fn [column-name]
                        (not= column-name :country))
                      {:target-columns :year
                       :value-column-name :cases}))

(-> table4a
    (tc/pivot->longer (complement #{:country})
                      {:target-columns :year
                       :value-column-name :cases}))

(-> table4a
    (tc/pivot->longer (complement #{:country})
                      {:target-columns :year
                       :value-column-name :cases})
    (tc/order-by [:country :year]))

[(-> table4a
     (tc/pivot->longer (complement #{:country})
                       {:target-columns :year
                        :value-column-name :cases}))
 (-> table4b
     (tc/pivot->longer (complement #{:country})
                       {:target-columns :year
                        :value-column-name :population}))]

(tc/left-join
 (-> table4a
     (tc/pivot->longer (complement #{:country})
                       {:target-columns :year
                        :value-column-name :cases}))
 (-> table4b
     (tc/pivot->longer (complement #{:country})
                       {:target-columns :year
                        :value-column-name :population}))
 [:country :year])


(def table2
  (-> (tc/left-join
       (-> table4a
           (tc/pivot->longer (complement #{:country})
                             {:target-columns :year
                              :value-column-name :cases}))
       (-> table4b
           (tc/pivot->longer (complement #{:country})
                             {:target-columns :year
                              :value-column-name :population}))
       [:country :year])
      (tc/select-columns [:country :year :cases :population])
      (tc/pivot->longer [:cases :population]
                        {:target-columns :type
                         :value-column-name :count})
      (tc/order-by [:country :year :type])))

table2

(-> table2
    (tc/pivot->wider [:type]
                     [:count]))
