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
            [scicloj.metamorph.ml.toydata :as toydata]
            [fastmath.stats]
            [noj-book.datasets :as datasets]))



(defn dataset->csv [dataset]
  (when dataset
    (let [{:keys [path _]}
          (tempfiles/tempfile! ".csv")]
      (-> dataset
          (ds/write! path))
      (slurp path))))

(defn submap->csv [{:as submap
                    :keys [hana/data
                           hana/stat]}]
  (dataset->csv
   (if stat
     (stat submap)
     @data)))


(def default-extenstions
  {;; defaults for original Hanami templates
   :VALDATA :hana/csv
   :DFMT {:type "csv"}
   ;; defaults for hanamicloth templates
   :hana/csv submap->csv
   :opacity :hana/opacity
   :row :hana/row
   :column :hana/column
   :color :hana/color
   :size :hana/size
   :tooltip :hana/tooltip})

(def encoding-base
  {:opacity :hana/opacity
   :row :hana/row
   :column :hana/column
   :color :hana/color
   :size :hana/size
   :tooltip :hana/tooltip})

(def xy-encoding
  (assoc
   encoding-base
   :x {:field :X
       :type :XTYPE
       :bin :XBIN
       :timeUnit :XUNIT
       :axis :XAXIS
       :scale :XSCALE
       :stack :XSTACK
       :sort :XSORT
       :aggregate :XAGG}
   :y {:field :Y
       :type :YTYPE
       :bin :YBIN
       :timeUnit :YUNIT
       :axis :YAXIS
       :scale :YSCALE
       :stack :YSTACK
       :sort :YSORT
       :aggregate :YAGG}
   :x2 :hana/x2-encoding
   :y2 :hana/y2-encoding))

(def text-encoding
  (-> encoding-base
      (dissoc :tooltip)
      (assoc
       :text {:field :TXT
              :type :TTYPE
              :axis :TAXIS
              :scale :TSCALE}
       :color :TCOLOR)))

(def view-base
  {:usermeta :USERDATA
   :title :TITLE
   :height :HEIGHT
   :width :WIDTH
   :background :BACKGROUND
   :selection :SELECTION
   :data data-options
   :transform :TRANSFORM
   :encoding :ENCODING})

(def mark-base
  {:type :MARK, :point :POINT,
   :size :MSIZE, :color :MCOLOR,
   :stroke :MSTROKE :strokeDash :MSDASH
   :tooltip :MTOOLTIP
   :filled :MFILLED})

(def bar-layer
  {:mark (assoc mark-base :type "bar")
   :selection :SELECTION
   :transform :TRANSFORM
   :encoding :ENCODING})

(def line-layer
  {:mark (assoc mark-base :type "line")
   :selection :SELECTION
   :transform :TRANSFORM
   :encoding :ENCODING})

(def point-layer
  {:mark (assoc mark-base :type "circle")
   :selection :SELECTION
   :transform :TRANSFORM
   :encoding :ENCODING})

(def text-layer
  {:mark (assoc mark-base :type "text"
                :dx :DX
                :dy :DY
                :xOffset :XOFFSET
                :yOffset :YOFFSET
                :angle :ANGLE
                :align :ALIGN
                :baseline :BASELINE
                :font :FONT
                :fontStyle :FONTSTYLE
                :fontWeight :FONTWEIGHT
                :fontSize :FONTSIZE
                :lineHeight :LINEHEIGHT
                :limit :LIMIT)
   :encoding text-encoding} )

(def rect-layer
  {:mark (assoc mark-base :type "rect")
   :encoding (dissoc encoding-base :tooltip)})

(def area-layer
  {:mark (assoc mark-base :type "area")
   :selection :SELECTION
   :transform :TRANSFORM
   :encoding :ENCODING})

(def gen-encode-layer
  {:height :HEIGHT, :width :WIDTH
   :mark :MARK
   :transform :TRANSFORM
   :selection :SELECTION
   :encoding :ENCODING})


(def empty-chart
  {:usermeta :USERDATA})

(def bar-chart
  (assoc view-base
         :mark (merge mark-base {:type "bar"})))

(def line-chart
  (assoc view-base
         :mark (merge mark-base {:type "line"})))

(def point-chart
  (assoc view-base
         :mark (merge mark-base {:type "circle"})))

(def area-chart
  (assoc view-base
         :mark (merge mark-base {:type "area"})))


(def layer-chart
  {:usermeta :USERDATA
   :title  :TITLE
   :height :HEIGHT
   :width :WIDTH
   :background :BACKGROUND
   :layer :LAYER
   :transform :TRANSFORM
   :resolve :RESOLVE
   :data data-options
   :config default-config})



(deftype WrappedValue [value]
  clojure.lang.IDeref
  (deref [this] value))





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


;; (defn layer-histogram
;;   ([context]
;;    (layer-smooth context {}))
;;   ([context submap]
;;    (layer context
;;           ht/bar-layer
;;           (assoc submap
;;                  :hana/stat histogram-stat))))


;; (defn histogram [dataset column-name {:keys [nbins]}]
;;   (let [{:keys [bins max step]} (-> column-name
;;                                     dataset
;;                                     (fastmath.stats/histogram nbins))
;;         left (map first bins)]
;;     (-> {:left (map first bins)
;;          :right (concat (rest left)
;;                         [max])
;;          :count (map second bins)}
;;         tc/dataset
;;         (hanami/plot ht/bar-chart
;;                      {:X :left
;;                       :X2 :right
;;                       :Y :count})
;;         (assoc-in [:encoding :x :bin] {:binned true
;;                                        :step step})
;;         (assoc-in [:encoding :x :title] column-name))))


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


(def random-walk
  (let [n 20]
    (-> {:x (range n)
         :y (->> (repeatedly n #(- (rand) 0.5))
                 (reductions +))}
        tc/dataset)))

(-> random-walk
    (plot ht/point-chart
          {:MSIZE 200}))

(-> random-walk
    (plot ht/point-chart
          {:MSIZE 200})
    kind/pprint)


(-> datasets/mtcars
    (plot ht/boxplot-chart
          {:X :gear
           :XTYPE :nominal
           :Y :mpg}))

(-> datasets/iris
    (plot ht/rule-chart
          {:X :sepal-width
           :Y :sepal-length
           :X2 :petal-width
           :Y2 :petal-length
           :OPACITY 0.2
           :SIZE 3
           :COLOR "species"}))
