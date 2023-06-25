(ns scicloj.noj.v1.vis
  (:require [tech.v3.dataset :as tmd]
            [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]))

(defn hanami-data [data]
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

(defn hanami-plot [data template options]
  (-> data
      hanami-data
      (merge options)
      (->> (apply concat)
           (apply hc/xform template))
      (kindly/consider :kind/vega-lite)))

(defn hanami-collector [template template-key]
  (fn [common-data
       options
       plots]
    (-> common-data
        (hanami-plot template
                     (merge {template-key plots}
                            options))
        (kindly/consider :kind/vega-lite))))

(def hanami-layers
  (hanami-collector ht/layer-chart
                    :LAYER))

(def hanami-vconcat
  (hanami-collector ht/vconcat-chart
                    :VCONCAT))

(def hanami-hconcat
  (hanami-collector ht/hconcat-chart
                    :HCONCAT))


(defn raw-html [html]
  (-> [:div
       {:dangerouslySetInnerHTML
        {:__html html}}]
      (kindly/consider :kind/hiccup)))

(defn iframe [html]
  (-> [:div
       [:h1 ".."]
       [:iframe
        {:srcimg html}]]
      (kindly/consider :kind/hiccup)))
