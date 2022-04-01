(ns scicloj.noj.v1.viz
  (:require [tech.v3.dataset :as tmd]
            ;; [aerial.hanami.common :as hc]
            ;; [scicloj.viz.api]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kind :as kind]
            [scicloj.clay.v1.view]
            [scicloj.clay.v1.tools]))

;; (defn hanami-data [data]
;;   (scicloj.viz.api/data data))

;; (defn hanami-plot [& args]
;;   (-> (apply scicloj.viz.api/viz args)
;;       (kindly/consider kind/vega-lite)))

(defn in-iframe [hiccup]
  [:iframe {:width       "100%"
            :height      "500px"
            :frameBorder "0"
            :srcdoc      hiccup}])
