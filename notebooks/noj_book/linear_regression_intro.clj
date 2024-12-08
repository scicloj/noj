(ns noj-book.linear-regression-intro
  (:require
   [tablecloth.api :as tc]
   [tablecloth.column.api :as tcc]
   [tech.v3.datatype.datetime :as datetime]))

datetime/long-temporal-field

(def column-name-mapping
  {"Fremont Bridge Sidewalks, south of N 34th St" :total
   "Fremont Bridge Sidewalks, south of N 34th St Cyclist West Sidewalk" :west
   "Fremont Bridge Sidewalks, south of N 34th St Cyclist East Sidewalk" :east
   "Date" :datetime})

#_(column-name-mapping "Fremont Bridge Sidewalks, south of N 34th St")

(def counts
  (tc/dataset "data/seattle-bikes-and-weather/Fremont_Bridge_Bicycle_Counter.csv"
              {:key-fn column-name-mapping
               :parser-fn {"Date" [:local-date-time "MM/dd/yyyy hh:mm:ss a"]}}))

(def weather
  (tc/dataset "data/seattle-bikes-and-weather/BicycleWeather.csv"
              {:key-fn keyword}))

;; no good support for this in tablecloth
;; daily = counts.resample('d').sum()

;; day column, group by, aggregate, sum.

(-> counts
    (tc/group-by (fn [{:keys [datetime]}]
                   (datetime/local-date-time->local-date datetime)))
    (tc/aggregate #(tcc/sum (% :total))))

(-> counts
    (tc/group-by (fn [{:keys [datetime]}]
                   (datetime/local-date-time->local-date datetime)))
    (tc/aggregate (comp tcc/sum :total)))

(-> counts
    (tc/group-by (fn [{:keys [datetime]}]
                   {:date (datetime/local-date-time->local-date datetime)}))
    (tc/aggregate {:total (comp tcc/sum :total)}))


(comment
  ;; Example from tablecloth README
  (-> "https://raw.githubusercontent.com/techascent/tech.ml.dataset/master/test/data/stocks.csv"
      (tc/dataset {:key-fn keyword})
      (tc/group-by (fn [row]
                     {:symbol (:symbol row)
                      :year (tech.v3.datatype.datetime/long-temporal-field :years (:date row))}))
      (tc/aggregate #(tcc/sum (% :price)))
      (tc/order-by [:symbol :year])
      (tc/head 10)))

datetime/local-date-time->local-date
