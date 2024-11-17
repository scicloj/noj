^:kindly/hide-code
(ns noj-book.smile-classification
  (:require
   [clj-http.client :as client]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [scicloj.metamorph.ml.toydata :as datasets ]
   [tablecloth.api :as tc]
   [tablecloth.pipeline :as tc-mm]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf]))
^:kindly/hide-code
(require '[scicloj.ml.smile.classification])

^:kindly/hide-code
(defn anchor-or-nothing [x text]
  (if (empty? x)
    [:div ""]
    [:div
     [:a {:href x} text]]))

^:kindly/hide-code
(defn stringify-enum [form]
  (walk/postwalk (fn [x] (do (if  (instance? Enum x) (str x) x)))
                         form))
^:kindly/hide-code
(defn docu-options [model-key]
  (->
   (tc/dataset
    (or
     (get-in @ml/model-definitions* [model-key :options])
     {:name [] :type [] :default []}))

   (tc/reorder-columns :name :type :default)))

^:kindly/hide-code
(defn docu-doc-string [model-key]
  ;;TODO needed ?
;;   (try
;;     (view/markdowns->hiccup
;;      (py/py. doc->markdown convert
;;              (or
;;               (get-in @scicloj.ml.core/model-definitions* [model-key :documentation :doc-string]) "")))
;;     (catch Exception e ""))
  ""
  )

^:kindly/hide-code
(defn render-key-info [prefix]
  (vec (concat [:span]
               (->> @ml/model-definitions*
                    (sort-by first)
                    (filter #(str/starts-with? (first %) (str prefix)))
                    (mapv
                     (fn [[key definition]]
                       [:div
                        ;; (clerk/md (format "### %s" (str key)))
                        [:h3 {:id (str key)} (str key)]
                        (anchor-or-nothing (:javadoc (:documentation definition)) "javadoc")
                        (anchor-or-nothing (:user-guide (:documentation definition)) "user guide")

                        ;; [:span (text->hiccup (or
                        ;;                       (get-in @scicloj.ml.core/model-definitions* [key :documentation :description] ) ""))]

                        [:span

                         (let [docu-ds (docu-options key)]
                           (if  (tc/empty-ds? docu-ds)
                             ""
                             (->
                              docu-ds
                              (tc/rows :as-maps)
                              seq
                              stringify-enum
                              (kind/table))))]
                        [:span
                         (docu-doc-string key)]

                        [:hr]
                        [:hr]]))))))
^:kindly/hide-code
(defn kroki [s type format]
  (client/post "https://kroki.io/" {:content-type :json
                                    :as :byte-array
                                    :form-params
                                    {:diagram_source s
                                     :diagram_type (name type)
                                     :output_format (name format)}}))

^:kindly/hide-code
(kind/hiccup
 (render-key-info :smile.classification/ada-boost))


;; In this example we will use the capability of the Ada boost classifier
;; to give us the importance of variables.

;; As data we take here the Wiscon Breast Cancer dataset, which has 30 variables.

(def df
  (->
   (datasets/breast-cancer-ds)))


;; To get an overview of the dataset, we print its summary:

(-> df tc/info)



;; Then we create a metamorph  pipeline with the ada boost model:

(def ada-pipe-fn
  (mm/pipeline
   (ds-mm/set-inference-target :class)
   (ds-mm/categorical->number [:class])
   (ml/model
    {:model-type :smile.classification/ada-boost})))


;; We run the pipeline in :fit. As we just explore the data,
;; not train.test split is needed.

(def trained-ctx
  (mm/fit-pipe df
   ada-pipe-fn))

;; "Next we take the model out of the pipeline:"
(def model
  (-> trained-ctx vals (nth 2) ml/thaw-model))

;; The variable importance can be obtained from the trained model,
(def var-importances
  (mapv
   #(hash-map :variable %1
              :importance %2)
   (map
    #(first (.variables %))
    (.. model formula predictors))
   (.importance model)))


;; and we plot the variables:

(kind/vega-lite
 {
  :data {:values
          var-importances}
  :width  800
  :height 500
  :mark {:type "bar"}
  :encoding {:x {:field :variable :type "nominal" :sort "-y"}
             :y {:field :importance :type "quantitative"}}})


(kind/hiccup
 (render-key-info ":smile.classification/decision-tree"))

;; A decision tree learns a set of rules from the data in the form
;; of a tree, which we will plot in this example.
;; We use the iris dataset:


(def iris  ^:nextjournal.clerk/no-cache  (datasets/iris-ds))



;; We make a pipe only containing the model, as the dataset is ready to
;; be used by `scicloj.ml`
(def trained-pipe-tree
  (mm/fit-pipe
   iris
   (mm/pipeline
    {:metamorph/id :model}
    (ml/model
     {:model-type :smile.classification/decision-tree}))))

;; We extract the Java object of the trained model.

(def tree-model
  (-> trained-pipe-tree :model ml/thaw-model))


;; The model has a .dot function, which returns a GraphViz textual
;; representation of the decision tree, which we render to svg using the
;; [kroki](https://kroki.io/) service.

(kind/html
 (String. (:body (kroki (.dot tree-model) :graphviz :svg)) "UTF-8"))



^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/discrete-naive-bayes"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/gradient-tree-boost"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/knn"))
;; In this example we use a knn model to classify some dummy data.
;; The training data is this:

(def df-knn
  (tc/dataset {:x1 [7 7 3 1]
               :x2 [7 4 4 4]
               :y [:bad :bad :good :good]}))



;; Then we construct a pipeline with the knn model,
;; using 3 neighbors for decision.

(def knn-pipe-fn
  (mm/pipeline
   (ds-mm/set-inference-target :y)
   (ds-mm/categorical->number [:y])
   (ml/model
    {:model-type :smile.classification/knn
     :k 3})))

;; We run the pipeline in mode fit:

(def trained-ctx-knn
  (knn-pipe-fn {:metamorph/data df-knn
                :metamorph/mode :fit}))


;; Then we run the pipeline in mode :transform with some test data
;; and take the prediction and convert it from numeric into categorical:

(->
 trained-ctx-knn
 (merge
  {:metamorph/data (tc/dataset
                    {:x1 [3 5]
                     :x2 [7 5]
                     :y [nil nil]})
   :metamorph/mode :transform})
 knn-pipe-fn
 :metamorph/data
 (ds-mod/column-values->categorical :y)
 seq)


^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/logistic-regression"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/maxent-binomial"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/maxent-multinomial"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/random-forest"))
;; The following code plots the decision surfaces of the random forest
 ;; model on pairs of features.

;; We use the Iris dataset for this.

(def iris-test
  (tc/dataset
   "https://raw.githubusercontent.com/scicloj/metamorph.ml/main/test/data/iris.csv" {:key-fn keyword}))




;; Standarise the data:
(def iris-std
  (mm/pipe-it
   iris-test
   (preprocessing/std-scale [:sepal_length :sepal_width :petal_length :petal_width] {})))






;; The next function creates a vega specification for the random forest
;; decision surface for a given pair of column names.

^:kindly/hide-code
(defn make-iris-pipeline [model-options]
  (mm/pipeline
   (ds-mm/set-inference-target :species)
   (ds-mm/categorical->number [:species])
   (ml/model model-options)))



(def rf-pipe
  (make-iris-pipeline
   {:model-type :smile.classification/random-forest}))

^:kindly/hide-code
(defn stepped-range [start end n-steps]
  (let [diff (- end start)]
    (range start end (/ diff n-steps))))


^:kindly/hide-code
(defn surface-plot [iris cols raw-pipe-fn model-name]
  (let [pipe-fn
        (mm/pipeline
         (tc-mm/select-columns (concat [:species] cols))
         raw-pipe-fn)

        fitted-ctx
        (pipe-fn
         {:metamorph/data iris
          :metamorph/mode :fit})
        ;; getting plot boundaries
        min-x (- (-> (get iris (first cols)) dtf/reduce-min) 0.2)
        min-y (- (-> (get iris (second cols)) dtf/reduce-min) 0.2)
        max-x (+ (-> (get iris (first cols)) dtf/reduce-max) 0.2)
        max-y (+ (-> (get iris (second cols)) dtf/reduce-max) 0.2)


        ;; make a grid for the decision surface
        grid
        (for [x1 (stepped-range min-x max-x 100)
              x2 (stepped-range min-y max-y 100)]

          {(first cols) x1
           (second cols) x2
           :species nil})

        grid-ds (tc/dataset grid)


        ;; predict for all grid points
        prediction-grid
        (->
         (pipe-fn
          (merge
           fitted-ctx
           {:metamorph/data grid-ds
            :metamorph/mode :transform}))
         :metamorph/data
         (ds-mod/column-values->categorical :species)
         seq)

        grid-ds-prediction
        (tc/add-column grid-ds :predicted-species prediction-grid)


        ;; predict the iris data points from data set
        prediction-iris
        (->
         (pipe-fn
          (merge
           fitted-ctx
           {:metamorph/data iris
            :metamorph/mode :transform}))
         :metamorph/data

         (ds-mod/column-values->categorical :species)
         seq)

        ds-prediction
        (tc/add-column iris :true-species (:species iris)
                       prediction-iris)]

    ;; create a 2 layer Vega lite specification
    {:layer
     [{:data {:values (seq (tc/rows grid-ds-prediction :as-maps))}
       :title (str "Decision surfaces for model: " model-name " - " cols)
       :width 500
       :height 500
       :mark {:type "square" :opacity 0.9 :strokeOpacity 0.1 :stroke nil},
       :encoding {:x {:field (first cols)
                      :type "quantitative"
                      :scale {:domain [min-x max-x]}
                      :axis {:format "2.2"
                             :labelOverlap true}}

                  :y {:field (second cols) :type "quantitative"
                      :axis {:format "2.2"
                             :labelOverlap true}
                      :scale {:domain [min-y max-y]}}

                  :color {:field :predicted-species}}}


      {:data {:values (seq (tc/rows ds-prediction :as-maps))}

       :width 500
       :height 500
       :mark {:type "circle" :opacity 1 :strokeOpacity 1},
       :encoding {:x {:field (first cols)
                      :type "quantitative"
                      :axis {:format "2.2"
                             :labelOverlap true}
                      :scale {:domain [min-x max-x]}}

                  :y {:field (second cols) :type "quantitative"
                      :axis {:format "2.2"
                             :labelOverlap true}
                      :scale {:domain [min-y max-y]}}


                  :fill {:field :true-species} ;; :legend nil

                  :stroke {:value :black}
                  :size {:value 300}}}]}))


(kind/vega-lite (surface-plot iris [:sepal_length :sepal_width] rf-pipe :smile.classification/random-forest))

(kind/vega-lite
 (surface-plot iris-std [:sepal_length :petal_length] rf-pipe :smile.classification/random-forest))

(kind/vega-lite
 (surface-plot iris-std [:sepal_length :petal_width] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:sepal_width :petal_length] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:sepal_width :petal_width] rf-pipe :smile.classification/random-forest))
(kind/vega-lite
 (surface-plot iris-std [:petal_length :petal_width] rf-pipe :smile.classification/random-forest))


^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/sparse-logistic-regression"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/sparse-svm"))

^:kindly/hide-code
(kind/hiccup (render-key-info ":smile.classification/svm"))
