(load-file "notebooks/noj_book/utils/eval_code.clj")

(ns noj-book.utils.example-code
  (:require
   [clj-http.client :as client]
   [noj-book.utils.eval-code :refer [->eval-code]]
   [noj-book.utils.surface-plot :refer [surface-plot]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [scicloj.metamorph.ml.toydata :as datasets]
   [scicloj.ml.smile.classification]
   [scicloj.ml.smile.regression]
   [scicloj.ml.tribuo]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf]))

(defn kroki [s type format]
  (client/post "https://kroki.io/" {:content-type :json
                                    :as :byte-array
                                    :form-params
                                    {:diagram_source s
                                     :diagram_type (name type)
                                     :output_format (name format)}}))

(def iris-test
  (datasets/iris-ds))




;; Standarise the data:
(def iris-std
  (mm/pipe-it
   iris-test
   (preprocessing/std-scale [:sepal-length :sepal-width :petal-length :petal-width] {})))



(def example-code
  {"org.tribuo.classification.baseline.DummyClassifierTrainer"
   (->eval-code
    ^:kindly/hide-code
    (kind/md "The DummyClassifier predicts a value, using a 'dummy' algorithm ")
    (kind/md "It can for example always predict a :CONSTANT value")
    (def df
      (->
       (tc/dataset  {:a [1 2]  :target [:x :x]})
       (ds-mod/set-inference-target :target)))
    (kind/table df)
    (def model (ml/train df {:model-type :scicloj.ml.tribuo/classification
                             :tribuo-components [{:name "dummy"
                                                  :type "org.tribuo.classification.baseline.DummyClassifierTrainer"
                                                  :properties {:dummyType :CONSTANT
                                                               :constantLabel "c"}}]
                             :tribuo-trainer-name "dummy"}))
    ^:kindly/hide-code
    (kind/md "'c' in this case:")
    (ml/predict df model))
   
   :smile.regression/ordinary-least-square
   (->eval-code
    ^:kindly/hide-code
    (kind/md "In this example we will explore the relationship between the
body mass index (bmi) and a diabetes indicator.")


    ^:kindly/hide-code
    (kind/md "First we load the data and split into train and test sets.")

    (def diabetes (datasets/diabetes-ds))
    (def diabetes-train
      (ds/head diabetes 422))


    (def diabetes-test
      (ds/tail diabetes 20))


    ^:kindly/hide-code
    (kind/md "Next we create the pipeline, converting the target variable to
          a float value, as needed by the model.")


    (def ols-pipe-fn
      (mm/pipeline
       (ds-mm/select-columns [:bmi :disease-progression])
       (mm/lift tc/convert-types :disease-progression :float32)
       (ds-mm/set-inference-target :disease-progression)
       {:metamorph/id :model} (ml/model {:model-type :smile.regression/ordinary-least-square})))

    ^:kindly/hide-code
    (kind/md "We can then fit the model, by running the pipeline in mode `:fit`.")



    (def fitted
      (mm/fit diabetes-train ols-pipe-fn))

    ^:kindly/hide-code
    (kind/md "Next we run the pipe-fn in `:transform` and extract the prediction
          for the disease progression:")

    (def diabetes-test-prediction
      (-> diabetes-test
          (mm/transform-pipe ols-pipe-fn fitted)
          :metamorph/data
          :disease-progression))
    diabetes-test-prediction

    ^:kindly/hide-code
    (kind/md "The truth is available in the test dataset.")


    (def diabetes-test-trueth
      (-> diabetes-test
          :disease-progression))
    diabetes-test-trueth



    ^:kindly/hide-code
    (kind/md "The smile Java object of the `LinearModel` is in the pipeline as well:")

    (def model-instance
      (-> fitted :model  (ml/thaw-model)))

    ^:kindly/hide-code
    (kind/md "This object contains all information regarding the model fit
              such as coefficients and formula.")

    (-> model-instance .coefficients seq)
    (-> model-instance .formula str)

    ^:kindly/hide-code
    (kind/md "Smile also generates a String with the result of the linear
              regression as part of the `toString()` method of class `LinearModel`:")


    (kind/code
     (str model-instance))


    ^:kindly/hide-code
    (kind/md "This tells us that there is a statistically significant
          (positive) correlation between the bmi and the diabetes
          disease progression in this data.")


    ^:kindly/hide-code
    (kind/md "At the end we can plot the truth and the prediction on the test data,
          and observe the linear nature of the model.")


    (kind/vega-lite
     {:layer [{:data {:values (map #(hash-map :disease-progression %1 :bmi %2 :type :truth)
                                   diabetes-test-trueth
                                   (:bmi  diabetes-test))}

               :width 500
               :height 500
               :mark {:type "circle"}
               :encoding {:x {:field :bmi :type "quantitative"}
                          :y {:field :disease-progression :type "quantitative"}
                          :color {:field :type}}}

              {:data {:values (map #(hash-map :disease-progression %1 :bmi %2 :type :prediction)
                                   diabetes-test-prediction
                                   (:bmi diabetes-test))}

               :width 500
               :height 500
               :mark {:type "line"}
               :encoding {:x {:field :bmi :type "quantitative"}
                          :y {:field :disease-progression :type "quantitative"}
                          :color {:field :type}}}]}))

   :smile.regression/lasso
   (->eval-code
    ^:kindly/hide-code
    (kind/md "")
    ^:kindly/hide-code
    (kind/md "We use the diabetes dataset and will show how
              [Lasso](https://en.wikipedia.org/wiki/Lasso_(statistics)) regression
              regulates the different variables, and the regulation depends
              on the `lambda` parameter.")

    ^:kindly/hide-code
    (kind/md "First we make a function to create pipelines with different `lambda`s.")

    (defn make-pipe-fn [lambda]
      (mm/pipeline
       (ds-mm/update-column :disease-progression (fn [col] (map #(double %) col)))
       (mm/lift tc/convert-types :disease-progression :float32)
       (ds-mm/set-inference-target :disease-progression)
       {:metamorph/id :model} (ml/model {:model-type :smile.regression/lasso
                                         :lambda (double lambda)})))

    :kindly/hide-code
    (kind/md "Now we go over a sequence of `lambda`s, fit a pipeline for all of them,
          and store the coefficients for each predictor variable:")
    (def diabetes (datasets/diabetes-ds))
    (ds/column-names diabetes)
    (ds/shape diabetes)

    (def coefs-vs-lambda
      (flatten
       (map
        (fn [lambda]
          (let [fitted
                (mm/fit-pipe
                 diabetes
                 (make-pipe-fn lambda))

                model-instance
                (-> fitted
                    :model
                    (ml/thaw-model))

                predictors
                (map
                 #(first (.variables %))
                 (seq
                  (.. model-instance formula predictors)))]

            (map
             #(hash-map :log-lambda (dtf/log10 lambda)
                        :coefficient %1
                        :predictor %2)
             (-> model-instance .coefficients seq)
             predictors)))
        (range 1 100000 100))))

    ^:kindly/hide-code
    (kind/md "Then we plot the coefficients over the log of `lambda`.")


    (kind/vega-lite
     {:data {:values coefs-vs-lambda}

      :width 500
      :height 500
      :mark {:type "line"}
      :encoding {:x {:field :log-lambda :type "quantitative"}
                 :y {:field :coefficient :type "quantitative"}
                 :color {:field :predictor}}})

    ^:kindly/hide-code
    (kind/md "This shows that an increasing `lambda` regulates more and more variables
          to zero. This plot can be used as well to find important variables,
          namely the ones which stay > 0 even with large `lambda`."))


   :smile.classification/random-forest
   (->eval-code
    ^:kindly/hide-code
    (kind/md "The following code plots the decision surfaces of the random forest
          model on pairs of features.")


    ^:kindly/hide-code
    (kind/md "We use the Iris dataset for this.")


    iris-std


    ^:kindly/hide-code
    (kind/md "The next function creates a [Vega-Lite](https://vega.github.io/vega-lite/)
              specification for the random forest decision surface for a given pair of column names.")


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

    (kind/vega-lite (surface-plot iris [:sepal-length :sepal-width] rf-pipe :smile.classification/random-forest))

    (kind/vega-lite
     (surface-plot iris-std [:sepal-length :petal-length] rf-pipe :smile.classification/random-forest))

    (kind/vega-lite
     (surface-plot iris-std [:sepal-length :petal-width] rf-pipe :smile.classification/random-forest))
    (kind/vega-lite
     (surface-plot iris-std [:sepal-width :petal-length] rf-pipe :smile.classification/random-forest))
    (kind/vega-lite
     (surface-plot iris-std [:sepal-width :petal-width] rf-pipe :smile.classification/random-forest))
    (kind/vega-lite
     (surface-plot iris-std [:petal-length :petal-width] rf-pipe :smile.classification/random-forest)))
   :smile.classification/knn
   (->eval-code
    ^:kindly/hide-code
    (kind/md "In this example we use a [k-NN](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm)
              model to classify some dummy data.  The training data is this:")


    (def df-knn
      (tc/dataset {:x1 [7 7 3 1]
                   :x2 [7 4 4 4]
                   :y [:bad :bad :good :good]}))
    df-knn

    ^:kindly/hide-code
    (kind/md "Then we construct a pipeline with the k-NN model,
              using 3 neighbors for decision.")


    (def knn-pipe-fn
      (mm/pipeline
       (ds-mm/set-inference-target :y)
       (ds-mm/categorical->number [:y])
       (ml/model
        {:model-type :smile.classification/knn
         :k 3})))

    ^:kindly/hide-code
    (kind/md "We run the pipeline in mode `:fit`:")



    (def trained-ctx-knn
      (knn-pipe-fn {:metamorph/data df-knn
                    :metamorph/mode :fit}))

    ^:kindly/hide-code
    (kind/md "Then we run the pipeline in mode `:transform` with some test data,
              take the prediction, and convert it from numeric into categorical:")

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
              of a tree, that we will plot in this example. We use the iris dataset:")



    (def iris (datasets/iris-ds))
    iris

    ^:kindly/hide-code
    (kind/md "We make a pipe only containing the model, 
              as the dataset is ready to be used by `metamorph.ml`.")

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
    (kind/md "The model has a `.dot` function, which returns a [GraphViz](https://graphviz.org/) textual
              representation of the decision tree. We render to svg using the
              [kroki](https://kroki.io/) service.")


    (kind/html
     (try 
       (String. (:body (kroki (.dot tree-model) :graphviz :svg)) "UTF-8")
       (catch Exception e 
         (do (println "kroki failed")
             (.printStackTrace e)
             (println (.st))
             "Kroki not available")
         )
       )
     
     )
    
    
    )
   :smile.classification/ada-boost
   (->eval-code
    ^:kindly/hide-code
    (kind/md "In this example we will use the capability of the AdaBoost classifier
                              to give us the importance of variables.")
    ^:kindly/hide-code
    (kind/md "As data we take here the Wisconsin Breast Cancer dataset, which has 30 variables.")
    (def df
      (->
       (datasets/breast-cancer-ds)))
    (tc/column-names df)
    ^:kindly/hide-code
    (kind/md "To get an overview of the dataset, we print its summary:")

    (-> df tc/info)

    ^:kindly/hide-code
    (kind/md "Then we create a metamorph pipeline with the AdaBoost model:")


    (def ada-pipe-fn
      (mm/pipeline
       (ds-mm/set-inference-target :class)
       (ds-mm/categorical->number [:class])
       (ml/model
        {:model-type :smile.classification/ada-boost})))

    ^:kindly/hide-code
    (kind/md "We run the pipeline in `:fit`. As we just explore the data, no train/test split is needed.")




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
