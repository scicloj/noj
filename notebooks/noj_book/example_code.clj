(ns noj-book.example-code
  (:require
   [clj-http.client :as client]
   [noj-book.eval-code :refer [->eval-code]]
   [noj-book.surface-plot :refer [surface-plot]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.ml.smile.classification]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [scicloj.metamorph.ml.toydata :as datasets]
   [tablecloth.api :as tc]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]))

(defn kroki [s type format]
  (client/post "https://kroki.io/" {:content-type :json
                                    :as :byte-array
                                    :form-params
                                    {:diagram_source s
                                     :diagram_type (name type)
                                     :output_format (name format)}}))

(def iris-test
  (tc/dataset
   "https://raw.githubusercontent.com/scicloj/metamorph.ml/main/test/data/iris.csv" {:key-fn keyword}))




;; Standarise the data:
(def iris-std
  (mm/pipe-it
   iris-test
   (preprocessing/std-scale [:sepal_length :sepal_width :petal_length :petal_width] {})))


^:kindly/hide-code
(kind/md "")


(def example-code
  {:smile.classification/random-forest
   (->eval-code
    ^:kindly/hide-code
    (kind/md "The following code plots the decision surfaces of the random forest
          model on pairs of features.")


    ^:kindly/hide-code
    (kind/md "We use the Iris dataset for this.")


    iris-std


    ^:kindly/hide-code
    (kind/md "The next function creates a vega specification for the random forest
              decision surface for a given pair of column names.")


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
    (def iris (datasets/iris-ds))

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
     (surface-plot iris-std [:petal_length :petal_width] rf-pipe :smile.classification/random-forest)))
   :smile.classification/knn
   (->eval-code
    ^:kindly/hide-code
    (kind/md "In this example we use a knn model to classify some dummy data.
              The training data is this:")


    (def df-knn
      (tc/dataset {:x1 [7 7 3 1]
                   :x2 [7 4 4 4]
                   :y [:bad :bad :good :good]}))
    df-knn

    ^:kindly/hide-code
    (kind/md "Then we construct a pipeline with the knn model,
              using 3 neighbors for decision.")


    (def knn-pipe-fn
      (mm/pipeline
       (ds-mm/set-inference-target :y)
       (ds-mm/categorical->number [:y])
       (ml/model
        {:model-type :smile.classification/knn
         :k 3})))

    ^:kindly/hide-code
    (kind/md "We run the pipeline in mode fit:")



    (def trained-ctx-knn
      (knn-pipe-fn {:metamorph/data df-knn
                    :metamorph/mode :fit}))

    ^:kindly/hide-code
    (kind/md "Then we run the pipeline in mode :transform with some test data
              and take the prediction and convert it from numeric into categorical:")

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
     seq))
   :smile.classification/decision-tree
   (->eval-code
    ^:kindly/hide-code
    (kind/md "A decision tree learns a set of rules from the data in the form
              of a tree, which we will plot in this example.
              We use the iris dataset:")



    (def iris (datasets/iris-ds))
    iris

    ^:kindly/hide-code
    (kind/md "We make a pipe only containing the model, as the dataset is ready to be used by `scicloj.ml`")

    (def trained-pipe-tree
      (mm/fit-pipe
       iris
       (mm/pipeline
        {:metamorph/id :model}
        (ml/model
         {:model-type :smile.classification/decision-tree}))))

    ^:kindly/hide-code
    (kind/md "We extract the Java object of the trained model.")



    (def tree-model
      (-> trained-pipe-tree :model ml/thaw-model))
    tree-model
    ^:kindly/hide-code
    (kind/md "The model has a .dot function, which returns a GraphViz textual
              representation of the decision tree, which we render to svg using the
              [kroki](https://kroki.io/) service.")


    (kind/html
     (String. (:body (kroki (.dot tree-model) :graphviz :svg)) "UTF-8")))
   :smile.classification/ada-boost
   (->eval-code
    ^:kindly/hide-code
    (kind/md "In this example we will use the capability of the Ada boost classifier
                              to give us the importance of variables.")
    ^:kindly/hide-code
    (kind/md "As data we take here the Wiscon Breast Cancer dataset, which has 30 variables.")
    (def df
      (->
       (datasets/breast-cancer-ds)))
    (tc/column-names df)
    ^:kindly/hide-code
    (kind/md "To get an overview of the dataset, we print its summary:")

    (-> df tc/info)

    ^:kindly/hide-code
    (kind/md "Then we create a metamorph  pipeline with the ada boost model:")


    (def ada-pipe-fn
      (mm/pipeline
       (ds-mm/set-inference-target :class)
       (ds-mm/categorical->number [:class])
       (ml/model
        {:model-type :smile.classification/ada-boost})))

    ^:kindly/hide-code
    (kind/md "We run the pipeline in :fit. As we just explore the data,not train.test split is needed.")




    (def trained-ctx
      (mm/fit-pipe df
                   ada-pipe-fn))

    ^:kindly/hide-code
    (kind/md "Next we take the model out of the pipeline:")


    (def model
      (-> trained-ctx vals (nth 2) ml/thaw-model))
    ^:kindly/hide-code
    (kind/md "The variable importance can be obtained from the trained model,")


    (def var-importances
      (mapv
       #(hash-map :variable %1
                  :importance %2)
       (map
        #(first (.variables %))
        (.. model formula predictors))
       (.importance model)))
    var-importances

    ^:kindly/hide-code
    (kind/md "and we plot the variables:")



    (kind/vega-lite
     {:data {:values
             var-importances}
      :width  800
      :height 500
      :mark {:type "bar"}
      :encoding {:x {:field :variable :type "nominal" :sort "-y"}
                 :y {:field :importance :type "quantitative"}}}))})
