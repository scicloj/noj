;; # Python (experimental 🛠)

;; author: Daniel Slutsky

(ns noj-book.python
  (:require [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [scicloj.noj.v1.vis.python :as vis.python]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]))


;; ## Using Python visualizations

;; Noj offers methods to include Python plots in [Kindly](https://scicloj.github.io/kindly-noted/kindly) visualizations: the `vis.python/with-pyplot` macro and the `vis.python/pyplot` function.

;; They are based on the [Parens for Pyplot](http://gigasquidsoftware.com/blog/2020/01/18/parens-for-pyplot/) blog post at Squid's blog.

(require-python '[numpy :as np]
                '[numpy.random :as np.random]
                'matplotlib.pyplot
                '[seaborn :as sns]
                'json)

(def sine-data
  (-> {:x (range 0 (* 3 np/pi) 0.1)}
      tc/dataset
      (tc/add-column :y #(tcc/sin (:x %)))))

(vis.python/with-pyplot
  (matplotlib.pyplot/plot
   (:x sine-data)
   (:y sine-data)))

(vis.python/pyplot
 #(matplotlib.pyplot/plot
   (:x sine-data)
   (:y sine-data)))

;; https://seaborn.pydata.org/tutorial/introduction
(let [tips (sns/load_dataset "tips")]
  (sns/set_theme)
  (vis.python/pyplot
   #(sns/relplot :data tips
                 :x "total_bill"
                 :y "tip"
                 :col "time"
                 :hue "smoker"
                 :style "smoker"
                 :size "size")))


:bye
