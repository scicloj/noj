(ns scicloj.noj.v1.vis.hanami
  (:require [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]
            [tablecloth.api :as tc]
            [tech.v3.dataset :as tmd]
            [tech.v3.dataset :as ds]))

(defn dataset->csv [dataset]
  (when dataset
    (let [{:keys [path _]}
          (tempfiles/tempfile! ".csv")]
      (-> dataset
          (ds/write! path))
      (slurp path))))

(deftype WrappedValue [value]
  clojure.lang.IDeref
  (deref [this] value))

(defn valdata-from-dataset [{:as args
                             :keys [hana/data
                                    hana/stat]}]
  (dataset->csv
   (if stat
     (stat args)
     @data)))


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
        (assoc :usermeta
               {:embedOptions {:renderer :svg}})
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


(defn update-data [template dataset-fn & args]
  (-> template
      (update-in [::ht/defaults :hana/data]
                 (fn [wrapped-data]
                   (->WrappedValue
                    (apply dataset-fn
                           @wrapped-data
                           args))))))
