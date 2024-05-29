(ns scicloj.noj.v1.vis.hanamicloth
  (:require [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]
            [tablecloth.api :as tc]
            [tech.v3.dataset :as tmd]
            [tech.v3.dataset :as ds]
            [tech.v3.datatype.functional :as fun]
            [scicloj.metamorph.ml :as ml]
            [tech.v3.dataset.modelling :as modelling]
            [scicloj.metamorph.ml.toydata :as toydata]))

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

(defn submap->csv [{:as submap
                    :keys [hana/data
                           hana/stat]}]
  (dataset->csv
   (if stat
     (stat submap)
     @data)))

(def default-extenstions
  {:hana/csv submap->csv
   :VALDATA :hana/csv
   :DFMT {:type "csv"}})

(defn dataset->defaults [dataset]
  {:hana/data (->WrappedValue dataset)})

(defn vega-lite-xform [template]
  (-> template
      hc/xform
      kind/vega-lite))

(defn base
  ([dataset-or-template]
   (base dataset-or-template {}))

  ([dataset-or-template submap]
   (if (tc/dataset? dataset-or-template)
     ;; a dataest
     (base dataset-or-template
           ht/view-base
           submap)
     ;; a template
     (-> dataset-or-template
         (update ::ht/defaults merge submap)
         (assoc :kindly/f #'vega-lite-xform)
         kind/fn)))

  ([dataset template submap]
   (-> template
       (update ::ht/defaults merge
               default-extenstions
               (dataset->defaults dataset))
       (base submap))))

(defn plot [& template]
  (->> template
       (apply base)
       vega-lite-xform))

(defn layer
  ([context template subs]
   (if (tc/dataset? context)
     (layer (base context {})
            template
            subs)
     ;; else - the context is already a template
     (-> context
         (merge ht/layer-chart)
         (update-in [::ht/defaults :LAYER]
                    (comp vec conj)
                    (assoc template
                           :data ht/data-options
                           ::ht/defaults (merge default-extenstions
                                                subs)))))))


(defn layer-point
  ([context]
   (layer-point context {}))
  ([context submap]
   (layer context ht/point-layer submap)))

(defn layer-line
  ([context]
   (layer-line context {}))
  ([context submap]
   (layer context ht/line-layer submap)))


(def smooth-stat
  (fn [{:as submap
        :keys [hana/data]}]
    (let [[Y X predictors group] (map submap [:Y :X :predictors :hana/group])
          predictors (or predictors [X])
          predictions-fn (fn [dataset]
                           (let [nonmissing-Y (-> dataset
                                                  (tc/drop-missing [Y]))]
                             (if (-> predictors count (= 1))
                               ;; simple linear regression
                               (let [model (fun/linear-regressor (-> predictors first nonmissing-Y)
                                                                 (nonmissing-Y Y))]
                                 (->> predictors
                                      first
                                      dataset
                                      (map model)))
                               ;; multiple linear regression
                               (let [_ (require 'scicloj.ml.smile.regression)
                                     model (-> nonmissing-Y
                                               (modelling/set-inference-target Y)
                                               (tc/select-columns (cons Y predictors))
                                               (ml/train {:model-type
                                                          :smile.regression/ordinary-least-square}))]
                                 (-> dataset
                                     (tc/drop-columns [Y])
                                     (ml/predict model)
                                     (get Y))))))
          update-data-fn (fn [dataset]
                           (if group
                             (-> dataset
                                 (tc/group-by group)
                                 (tc/add-or-replace-column Y predictions-fn)
                                 tc/ungroup)
                             (-> dataset
                                 (tc/add-or-replace-column Y predictions-fn))))
          new-data (update-data-fn @data)]
      new-data)))



(defn layer-smooth
  ([context]
   (layer-smooth context {}))
  ([context submap]
   (layer context
          ht/line-layer
          (assoc submap
                 :hana/stat smooth-stat))))


(defn update-data [template dataset-fn & submap]
  (-> template
      (update-in [::ht/defaults :hana/data]
                 (fn [wrapped-data]
                   (->WrappedValue
                    (apply dataset-fn
                           @wrapped-data
                           submap))))))

(delay
  (-> (toydata/iris-ds)
      (base ht/point-chart
            {:X :sepal_width
             :Y :sepal_length
             :MSIZE 200})))

(delay
  (-> (toydata/iris-ds)
      (base {:X :sepal_width
             :Y :sepal_length
             :MSIZE 200})
      layer-point))

(delay
  (-> (toydata/iris-ds)
      (base {:X :sepal_width
             :Y :sepal_length})
      (layer-point {:MSIZE 200})))

(delay
  (-> (toydata/iris-ds)
      (base)
      (layer-point {:X :sepal_width
                    :Y :sepal_length
                    :MSIZE 200})))

(delay
  (-> (toydata/iris-ds)
      (base {:TITLE "dummy"
             :MCOLOR "green"
             :X :sepal_width
             :Y :sepal_length})
      (layer-point
       {:MSIZE 100})
      (layer-line
       {:MSIZE 4
        :MCOLOR "brown"})
      (update-data tc/random 20)))


(delay
  (-> (toydata/iris-ds)
      (base {:TITLE "dummy"
             :MCOLOR "green"
             :X :sepal_width

             :Y :sepal_length})
      (layer-point {:MSIZE 100})
      (layer-line {:MSIZE 4
                   :MCOLOR "brown"})
      (update-data tc/random 20)
      plot
      (assoc :background "lightgrey")))

(delay
  (-> (toydata/iris-ds)
      (tc/select-columns [:sepal_width :sepal_length])
      (base {:X :sepal_width
             :Y :sepal_length})
      layer-point
      layer-smooth))

(delay
  (-> (toydata/iris-ds)
      (base {:X :sepal_width
             :Y :sepal_length
             :COLOR "species"
             :hana/group [:species]})
      layer-point
      layer-smooth))

(delay
  (-> (toydata/iris-ds)
      (tc/concat (tc/dataset {:sepal_width (range 4 10)
                              :sepal_length (repeat 6 nil)}))
      (tc/map-columns :relative-time
                      [:sepal_length]
                      #(if % "Past" "Future"))
      (base {:X :sepal_width
             :Y :sepal_length
             :COLOR "relative-time"})
      layer-point
      layer-smooth))


(delay
  (-> (toydata/iris-ds)
      (base {:X :sepal_width
             :Y :sepal_length})
      layer-point
      (layer-smooth {:predictors [:petal_width
                                  :petal_length]})))
