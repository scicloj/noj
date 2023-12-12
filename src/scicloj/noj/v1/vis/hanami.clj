(ns scicloj.noj.v1.vis.hanami
  (:require [tech.v3.dataset :as tmd]
            [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]
            [scicloj.noj.v1.vis.hanami.templates :as vht]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]
            [scicloj.noj.v1.stats :as stats]
            [tablecloth.api :as tc]))


(defn prepare-data [data]
  (when data
    (cond (string? data)          (if (paths/url? data) {:UDATA data}
                                      ;; not a url -- assuming a local path
                                      (let [file-type (paths/file-type "csv")]
                                        (case file-type
                                          "csv" {:DATA (-> data
                                                           paths/throw-if-not-exists!
                                                           slurp)
                                                 :DFMT {:type file-type}}
                                          (throw (ex-info "Unsupported file type"
                                                          {:file-type file-type})))))
          (tmd/dataset? data) {:DFMT {:type "csv"}
                               :DATA (let [{:keys [path _]}
                                           (tempfiles/tempfile! ".csv")]
                                       (-> data
                                           (tmd/write! path))
                                       (-> path
                                           slurp))}
          :else                   {:DATA data})))

(defn plot [data template options]
  (if (tc/grouped? data)
    (-> data
        (tc/aggregate {:plot (fn [group-data]
                               [(-> group-data
                                    (plot template
                                          options))])})
        (tc/rename-columns {:plot-0 :plot})
        kind/table)
    (-> data
        prepare-data
        (merge options)
        (->> (apply concat)
             (apply hc/xform template))
        kind/vega-lite)))

(defn collector [template template-key]
  (fn [common-data
       options
       plots]
    (-> common-data
        (plot template
              (merge {template-key plots}
                     options)))))

(def layers
  (collector ht/layer-chart
             :LAYER))

(def vconcat
  (collector ht/vconcat-chart
             :VCONCAT))

(def hconcat
  (collector ht/hconcat-chart
             :HCONCAT))

(defn combined-plot [dataset
                            combining-template
                            options
                            template-key
                            plot-specs]
  (-> dataset
      (plot
       combining-template
       (assoc options
              template-key
              (->> plot-specs
                   (map (partial
                         apply
                         (fn inner-plot
                           ([inner-template
                             inner-options]
                            (inner-plot nil
                                        inner-template
                                        inner-options))
                           ([inner-dataset
                             inner-template
                             inner-options]
                            (plot inner-dataset
                                         inner-template
                                         (merge options inner-options)))))))))))

(defn histogram [dataset column-name options]
  (-> column-name
      dataset
      (stats/histogram options)
      (plot vht/rect-chart
            {:X :left
             :X2 :right
             :Y :count
             :Y2 0
             :XSCALE {:zero false}})))


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
                                     "-prediction"))]
    (-> ds-with-predictions
        (combined-plot
         ht/layer-chart
         (merge {:X feature-column
                 :TITLE (format "R^2 = %.3f"
                                (-> ds-with-predictions
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
                         line-options)]]))))
