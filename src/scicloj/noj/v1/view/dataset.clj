(ns scicloj.noj.v1.view.dataset
  (:require [tech.v3.dataset :as ds]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kindness :as kindness]
            [nextjournal.clerk :as clerk]))


(extend-protocol kindness/Kindness
  tech.v3.dataset.impl.dataset.Dataset
  (kind [this]
    :kind/dataset))

(kindly/define-kind-behaviour!
  :kind/dataset
  {:portal.viewer (fn [v]
                    [:portal.viewer/hiccup
                     [:portal.viewer/markdown (-> v
                                                  println
                                                  with-out-str)]])})
(kindly/define-kind-behaviour!
  :kind/dataset
  {:clerk.viewer (fn [v]
                   (-> v
                       ds/mapseq-reader
                       clerk/table))})
