(ns index
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]
            [scicloj.noj.v1.vis :as vis]))

;; Adapt Clerk to Kindly
(kind-clerk/setup!)

(def dataset1
  (-> {:x (range 9)
       :y (map +
               (range 9)
               (repeatedly 9 rand))}
      tc/dataset))

(-> dataset1
    (vis/hanami-plot ht/point-chart
                     :MSIZE 200))
