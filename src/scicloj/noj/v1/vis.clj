(ns scicloj.noj.v1.vis
  (:require [tech.v3.dataset :as tmd]
            [aerial.hanami.common :as hc]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]))

(defn hanami-data [data]
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
        :else                   {:DATA data}))

(defn hanami-plot [data template & args]
  (-> data
      hanami-data
      (->> (apply concat args)
           (apply hc/xform template))
      (kindly/consider :kind/vega-lite)))

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
