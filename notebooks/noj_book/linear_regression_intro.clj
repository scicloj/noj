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
   [scicloj.metamorph.ml :as ml]
   [fastmath.ml.regression :as reg]
   [scicloj.kindly.v4.kind :as kind]))

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

;; Our bike counts data are hourly, but the weather data is daily.
;; To join them, we will need to convert the bike hourly counts to daily counts.

;; In the Python book, this is done as follows in Pandas:
;; ```python
;; daily = counts.resample('d').sum()
;; ```

;; Tablecloth's full support for time series is still under construction.
;; For now, we will have to be a bit more verbose:

(def daily-totals
  (-> counts
      (tc/group-by (fn [{:keys [datetime]}]
                     {:date (datetime/local-date-time->local-date
                             datetime)}))
      (tc/aggregate-columns [:total :west :east]
                            tcc/sum)))


daily-totals

;; ## Prediction by weekday

;; Let us prepare the data for regression on the day of week.


(def days-of-week
  [:Mon :Tue :Wed :Thu :Fri :Sat :Sun])


;; We will convert numbers to days-of-week keywords:

(def idx->day-of-week
  (comp days-of-week dec))

;; E.g., 
(idx->day-of-week 1)
(idx->day-of-week 7)

;; Now, let us prepare the data:

(def totals-with-day-of-week
  (-> daily-totals
      (tc/add-column :day-of-week
                     (fn [ds]
                       (map idx->day-of-week
                            (datetime/long-temporal-field
                             :day-of-week
                             (:date ds)))))
      (tc/select-columns [:total :day-of-week])))

totals-with-day-of-week

(def totals-with-one-hot-days-of-week
  (-> (reduce (fn [dataset day-of-week]
                (-> dataset
                    (tc/add-column day-of-week
                                   #(-> (:day-of-week %)
                                        (tcc/eq day-of-week)
                                        ;; turn booleans into 0s and 1s
                                        (tcc/* 1)))))
              totals-with-day-of-week
              days-of-week)
      (tc/drop-columns [:day-of-week])
      (dsmod/set-inference-target :total)))

(-> totals-with-one-hot-days-of-week
    (tc/select-columns dsmod/inference-column?))

;; Let us compute the linear regression model using Fastmath.
;; We will use this wrapper function that handles a dataset
;; (a concept which is unknown to Fastmath):

(defn lm [dataset options]
  (let [inference-column-name (-> dataset
                                  dsmod/inference-target-column-names
                                  first)
        ds-without-target (-> dataset
                              (tc/drop-columns [inference-column-name]))]
    (reg/lm
     ;; ys
     (get dataset inference-column-name)
     ;; xss
     (tc/rows ds-without-target)
     ;; options
     (merge {:names (-> ds-without-target
                        tc/column-names
                        vec)}
            options))))

;; The binary columns are collinear (sum up to 1),
;; but we will avoide the intercept.
;; This way, the interpretation of each coefficient is the expected
;; bike count for the corresponding day of week.

(def days-of-week-model
  (lm totals-with-one-hot-days-of-week
      {:intercept? false}))

;; Here are the regression results:

(-> fit
    println
    with-out-str
    kind/code)

;; We can see the difference between weekends and weekdays.
