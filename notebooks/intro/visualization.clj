(ns intro.visualization
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis :as vis]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kindly.v3.kind :as kind]
            [hiccup.core :as hiccup]
            hiccup.util))


;; ## Adapt Clerk to Kindly
(kind-clerk/setup!)

;; ## Raw html
(-> "<p>Hello, <i>Noj</i>.</p>"
    vis/raw-html)

(-> [:svg {:height 210
           :width 500}
     [:line {:x1 0
             :y1 0
             :x2 200
             :y2 200
             :style "stroke:rgb(255,0,0);stroke-width:2"}]]
    hiccup/html
    vis/raw-html)

;; ## Visualizing datases with Hanami
(def dataset1
  (-> {:x (range 9)
       :y (map +
               (range 9)
               (repeatedly 9 rand))}
      tc/dataset))

(-> dataset1
    (vis/hanami-plot ht/point-chart
                     :MSIZE 200))


:bye
