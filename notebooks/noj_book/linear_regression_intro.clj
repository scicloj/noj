;; # Intro to Linear Regression - DRAFT 🛠

;; Here we offer an intro to [linear regression](https://en.wikipedia.org/wiki/Linear_regression)
;; following the
;; [In Depth: Linear Regression](https://jakevdp.github.io/PythonDataScienceHandbook/05.06-linear-regression.html)
;; section of the
;; [Python Data Science Handbook](https://jakevdp.github.io/PythonDataScienceHandbook/)
;; by Jake VanderPlas.

;; ## Setup

(ns noj-book.linear-regression-intro
  (:require
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [tablecloth.column.api :as tcc]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.dataset.modelling :as dsmod]
   [scicloj.metamorph.ml :as ml]))

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

counts

(def weather
  (tc/dataset "data/seattle-bikes-and-weather/BicycleWeather.csv.gz"
              {:key-fn keyword}))

weather

;; ## Preprocessing

;; no good support for this in tablecloth
;; ```python
;; daily = counts.resample('d').sum()
;; ```

;; day column, group by, aggregate, sum.

(def daily-totals
  (-> counts
      (tc/group-by (fn [{:keys [datetime]}]
                     {:date (datetime/local-date-time->local-date
                             datetime)}))
      (tc/aggregate-columns [:total :west :east]
                            tcc/sum)))


daily-totals

(:date daily-totals)

(datetime/long-temporal-field
 :day-of-week
 (:date daily-totals))

(def idx->day-of-week
  (comp [:Mon :Tue :Wed :Thu :Fri :Sat :Sun]
        dec))

(idx->day-of-week 1)
(idx->day-of-week 7)

(def data-for-prediction
  (-> daily-totals
      (tc/select-columns [:date :total])
      (tc/add-column :dow
                     (fn [ds]
                       (map idx->day-of-week
                            (datetime/long-temporal-field
                             :day-of-week
                             (:date ds)))))
      (ds/categorical->one-hot [:dow])
      (tc/drop-columns [:date :dow-Sun])
      (dsmod/set-inference-target :total)))

data-for-prediction

;; C + A0*Mon + A1*Tue + ... + A5*Sat
;; The prediction for Mon: C+A0
;; The prediction for Sun: C

(-> data-for-prediction
    :total
    meta)
