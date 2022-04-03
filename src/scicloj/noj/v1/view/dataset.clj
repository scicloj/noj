(ns scicloj.noj.v1.view.dataset
  (:require [tech.v3.dataset :as tmd]
            [nextjournal.clerk :as clerk]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kindness :as kindness]
            [scicloj.clay.v1.html.table :as html.table]))


(extend-protocol kindness/Kindness
  tech.v3.dataset.impl.dataset.Dataset
  (kind [this]
    :kind/dataset))

(kindly/define-kind-behaviour!
  :kind/dataset
  {:portal.viewer (fn [v]
                    [:portal.viewer/table
                     (seq (tmd/mapseq-reader v))])})

(kindly/define-kind-behaviour!
  :kind/dataset
  {:clerk.viewer (fn [v]
                   (clerk/table {:head (tmd/column-names v)
                                 :rows (tmd/rowvecs v)}))})
