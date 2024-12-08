;; # Intro to Linear Regression - DRAFT 🛠

;; Here we offer an intro to [linear regression](https://en.wikipedia.org/wiki/Linear_regression)
;; following the
;; [In Depth: Linear Regression](https://jakevdp.github.io/PythonDataScienceHandbook/05.06-linear-regression.htmlhttps://jakevdp.github.io/PythonDataScienceHandbook/05.06-linear-regression.html)
;; section of the
;; [Python Data Science Handbook](https://jakevdp.github.io/PythonDataScienceHandbook/)
;; by Jake VanderPlas.

;; ## Setup

(ns noj-book.linear-regression-intro
  (:require
   [tablecloth.api :as tc]
   [tablecloth.column.api :as tcc]
   [tech.v3.datatype.datetime :as datetime]))

;; ## Reading and parsing data

(def column-name-mapping
  {"Fremont Bridge Sidewalks, south of N 34th St" :total
   "Fremont Bridge Sidewalks, south of N 34th St Cyclist West Sidewalk" :west
   "Fremont Bridge Sidewalks, south of N 34th St Cyclist East Sidewalk" :east
   "Date" :datetime})

(column-name-mapping
 "Fremont Bridge Sidewalks, south of N 34th St")

(def counts
  (tc/dataset "data/seattle-bikes-and-weather/Fremont_Bridge_Bicycle_Counter.csv.gz"
              {:key-fn column-name-mapping
               :parser-fn {"Date" [:local-date-time "MM/dd/yyyy hh:mm:ss a"]}}))

(def weather
  (tc/dataset "data/seattle-bikes-and-weather/BicycleWeather.csv.gz"
              {:key-fn keyword}))

;; ## Preprocessing

;; no good support for this in tablecloth
;; ```python
;; daily = counts.resample('d').sum()
;; ```

;; day column, group by, aggregate, sum.

(-> counts
    (tc/group-by (fn [{:keys [datetime]}]
                   {:date (datetime/local-date-time->local-date datetime)}))
    (tc/aggregate {:total (comp tcc/sum :total)}))

