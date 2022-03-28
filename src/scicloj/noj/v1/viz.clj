(ns scicloj.noj.v1.viz
  (:require [tech.v3.dataset :as tmd]
            [aerial.hanami.common :as hc]
            [scicloj.viz.api]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kind :as kind]
            [scicloj.clay.v1.view]))

(defn data [data]
  (scicloj.viz.api/data data))

(defn viz [& args]
  (-> (apply scicloj.viz.api/viz args)
      (kindly/consider kind/vega-lite)))
