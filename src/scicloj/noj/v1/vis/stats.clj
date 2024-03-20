(ns scicloj.noj.v1.vis.stats
  (:require [scicloj.noj.v1.vis.hanami :as hanami]
            [scicloj.noj.v1.stats :as stats]
            [scicloj.noj.v1.vis.hanami.templates :as vht]
            [aerial.hanami.templates :as ht]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

(defn histogram [dataset column-name options]
  (let [hist (-> column-name
                 dataset
                 (stats/histogram options))
        step (->> hist
                  :left
                  (take 2)
                  reverse
                  (apply -))]
    (-> hist
        (hanami/plot ht/bar-chart
                     {:X :left
                      :X2 :right
                      :Y :count})
        (assoc-in [:encoding :x :bin] {:binned true
                                       :step step})
        (assoc-in [:encoding :x :title] column-name))))


(defn linear-regression-plot [dataset target-column feature-column
                              {:as options
                               :keys [point-options
                                      line-options]}]
  (let [ds-with-predictions
        (-> dataset
            (stats/add-predictions target-column [feature-column]
                                   {:model-type :smile.regression/ordinary-least-square}))
        prediction-column-name (keyword
                                (str (name target-column)
                                     "-prediction"))
        process-fn (fn [ds]
                     (-> ds
                         (hanami/combined-plot
                          ht/layer-chart
                          (merge {:X feature-column
                                  :TITLE (format "R^2 = %.3f"
                                                 (-> ds
                                                     prediction-column-name
                                                     meta
                                                     :model
                                                     :R2))}
                                 options)
                          :LAYER [[ht/point-chart
                                   (merge {:Y target-column}
                                          point-options)]
                                  [ht/line-chart
                                   (merge {:Y prediction-column-name
                                           :YTITLE target-column}
                                          line-options)]])))]
    (if (tc/grouped? ds-with-predictions)
      (-> ds-with-predictions
          (tc/aggregate {:plot (fn [group-data]
                                 [(process-fn group-data)])})
          (tc/rename-columns {:plot-0 :plot})
          kind/table)
      (process-fn ds-with-predictions))))
