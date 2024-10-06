;; # Data Visualization with Echarts - DRAFT 🛠

;; In this tutorial, we explore how we can use
;; [Apache Echarts](https://echarts.apache.org/)
;; to visualize data.

(ns noj-book.echarts
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

;; ## Getting started

;; Let us look into the example from the Echarts
;; [getting started](https://echarts.apache.org/handbook/en/get-started/)
;; tutorial.

;; ### Specifying a plot

;; We may create a Clojure data structure that will match
;; the Javascript data structure in the example.
;; When we annotate it with [Kindly](https://scicloj.github.io/kindly-noted/),
;; tools such as [Clay](https://scicloj.github.io/clay)
;; will convert the Clojure data to JSON and pass them
;; to be visualized in the browser.

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data [5, 20, 36, 10, 10, 20]}]})

;; ### Styling

;; The second argument for Kindly's `kind` function
;; can be used to specify options such as the style:

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data [5, 20, 36, 10, 10, 20]}]}
 {:style {:height "200px"}})

;; ### Passing datasets

;; Now, what do we do if our data is held in a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset?

(def sales
  (tc/dataset
   {:item ["Shirts", "Cardigans", "Chiffons", "Pants", "Heels", "Socks"]
    :amount [5, 20, 36, 10, 10, 20]}))

;; A dataset is also a map, and the keys are the column names:

(map? sales)

(keys sales)

;; So, we may extract the relevant columns and refer to them in the plot spec.

(kind/echarts
 {:tooltip {}
  :legend {:data ["sales"]}
  :xAxis {:data (:item sales)}
  :yAxis {}
  :series [{:name "sales"
            :type "bar"
            :data (:amount sales)}]}
 {:style {:height "200px"}})

;; We may also use map [destructuring](https://clojure.org/guides/destructuring):

(let [{:keys [item amount]} sales]
  (kind/echarts
   {:tooltip {}
    :legend {:data ["sales"]}
    :xAxis {:data item}
    :yAxis {}
    :series [{:name "sales"
              :type "bar"
              :data amount}]}
   {:style {:height "200px"}}))
