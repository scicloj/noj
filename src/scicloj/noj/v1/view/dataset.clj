(ns scicloj.noj.v1.view.dataset
  (:require [tech.v3.dataset :as tmd]
            [nextjournal.clerk :as clerk]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kindness :as kindness]
            [scicloj.clay.v1.tool.html.table :as html.table]))


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
                   (clerk/table {:head (tmd/column-names v)
                                 :rows (tmd/rowvecs v)}))})

(kindly/define-kind-behaviour!
  :kind/dataset
  {:html.viewer (fn [v]
                  (-> {:column-names (tmd/column-names v)
                       :row-vectors (tmd/rowvecs v)}
                      html.table/->table-hiccup
                      html.table/table-hiccup->datatables-html))})
