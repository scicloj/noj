;; # Transformer reference  - DRAFT 🛠

;; Note that this chapter reqiures `scicloj.ml.smile` as an additional
;; dependency to Noj.
;; [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/scicloj.ml.smile.svg)](https://clojars.org/org.scicloj/scicloj.ml.smile)


(ns noj-book.transformer-references
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [scicloj.ml.smile.classification]
   [scicloj.ml.smile.metamorph :as smile-mm]
   [scicloj.ml.smile.nlp :as nlp]
   [scicloj.ml.smile.projections :as projections]
   [scicloj.ml.smile.clustering :as clustering]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.categorical :as ds-cat]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.dataset.print]))




^:kindly/hide-code
(defn docu-fn [v]
  (let [m (meta v)]
    (kindly/hide-code 
     (kind/fragment
      [(kind/md  (str "## Transformer " "**" (:name m) "**"))
       (kind/md "----------------------------------------------------------")
       (kind/md "__Clojure doc__:\n")
       (kind/md (:doc m))
       (kind/md "----------------------------------------------------------")]))))

(docu-fn (var nlp/count-vectorize))

;; In the following we transform the text given in a dataset into a
;; map of token counts applying some default text normalization.
(def data (ds/->dataset {:text ["Hello Clojure world, hello ML word !"
                              "ML with Clojure is fun"]}))


^kind/dataset
data

;;_

(def fitted-ctx
  (mm/fit data
          (scicloj.ml.smile.metamorph/count-vectorize :text :bow)))



(:metamorph/data fitted-ctx)

(def bow-ds
  (:metamorph/data fitted-ctx))

^kind/dataset
bow-ds


;; A custom tokenizer can be specified by either passing options to
;; `scicloj.ml.smile.nlp/default-tokenize`:


(def fitted-ctx
  (mm/fit
   data
   (scicloj.ml.smile.metamorph/count-vectorize 
    :text :bow {:stopwords ["clojure"]
                :stemmer :none})))



(:metamorph/data fitted-ctx)
;; or passing in an implementation of a tokenizer function:

(def fitted-ctx
  (mm/fit
   data
   (scicloj.ml.smile.metamorph/count-vectorize
    :text :bow
    {:text->bow-fn (fn [text options]
                     {:a 1 :b 2})})))

(:metamorph/data fitted-ctx)


(docu-fn (var smile-mm/bow->SparseArray))
;; Now we convert the
;; [bag-of-words](https://en.wikipedia.org/wiki/Bag-of-words_model)
;; map to a sparse array of class `smile.util.SparseArray`:

(def ctx-sparse
  (mm/fit
   bow-ds
   (smile-mm/bow->SparseArray :bow :sparse)))

ctx-sparse


^kind/dataset
(:metamorph/data ctx-sparse)

;; The `SparseArray` instances look like this:
(zipmap
 (:text bow-ds)
 (map seq
      (-> ctx-sparse :metamorph/data :sparse)))

(docu-fn (var smile-mm/bow->sparse-array))
;; Now we convert the bag-of-words map to a sparse array of class
;; `java primitive int array`:

(def ctx-sparse
  (mm/fit
   bow-ds
   (smile-mm/bow->sparse-array :bow :sparse)))

ctx-sparse

;; We also see the sparse representation as indices against the vocabulary
;; of the non-zero counts.

(zipmap
 (:text bow-ds)
 (map seq
      (-> ctx-sparse :metamorph/data :sparse)))

;; In both `->sparse` functions we can control the vocabulary via
;; the option to pass in a different / custom function which creates
;; the vocabulary from the bow (bag-of-words) maps.

(def ctx-sparse
  (mm/fit
   bow-ds
   (smile-mm/bow->SparseArray
    :bow :sparse
    {:create-vocab-fn
     (fn [bow] (nlp/->vocabulary-top-n bow 1))})))


ctx-sparse

(def ctx-sparse
  (mm/fit
   bow-ds
   (smile-mm/bow->SparseArray
    :bow :sparse
    {:create-vocab-fn
     (fn [_]
       ["hello" "fun"])})))


ctx-sparse


(docu-fn (var smile-mm/bow->tfidf))
;; Here we calculate the [tf-idf](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)
;; score from the bag-of-words:

^kind/dataset
(mm/pipe-it
 bow-ds
 (smile-mm/bow->tfidf :bow :tfidf {}))



(docu-fn (var ml/model))
;; The `model` transformer allows to execute all machine learning models
;; which register themself inside the `metamorph.ml` system via the function
;; `scicloj.metamorph.ml/define-model!`.
;; Models can be added at runing by require relevant namespaces
;; as documented in the various "model reference" chapters of the Noj book.
;; The currently defined models can be looked up via
;; `(ml/model-definition-names)`.



;; We use the Iris data for this example:

(def iris
  (->
   (ds/->dataset
    "https://raw.githubusercontent.com/scicloj/metamorph.ml/main/test/data/iris.csv" {:key-fn keyword})
   (tech.v3.dataset.print/print-range 5)))



^kind/dataset
iris

(def train-test
  (ds-mod/train-test-split iris))

;; The pipeline specifies the inference target,
;; transforms the target to categorical, and applies the model function.
(def pipe-fn
  (mm/pipeline
   (mm/lift ds-mod/set-inference-target :species)
   (mm/lift ds/categorical->number [:species])
   {:metamorph/id :model}
   (ml/model {:model-type :smile.classification/logistic-regression})))

;; First we run the training:
(def fitted-ctx
  (mm/fit
   (:train-ds train-test)
   pipe-fn))


^:kindly/hide-code
(defn dissoc-in [m ks]
  (let [parent-path (butlast ks)
        leaf-key (last ks)]
    (if (= (count ks) 1)
      (dissoc m leaf-key)
      (update-in m parent-path dissoc leaf-key))))

(dissoc-in  fitted-ctx [:model :model-data])

;; and then prediction on the test set:

(def transformed-ctx
  (mm/transform-pipe (:test-ds train-test) pipe-fn fitted-ctx))

(-> transformed-ctx
    (dissoc-in [:model :model-data])
    (update-in [:metamorph/data] #(tech.v3.dataset.print/print-range % 5)))

;; and we get the predictions: 
^kind/dataset
(-> transformed-ctx
    :metamorph/data
    (ds-cat/reverse-map-categorical-xforms)
    (ds/select-columns [:species])
    (ds/head))


(docu-fn (var preprocessing/std-scale))
;; We can use the `std-scale` transformer to center and scale data.
;; Lets take some example data:
(def data
  (tc/dataset
   [[100 0.001]
    [8   0.05]
    [50  0.005]
    [88  0.07]
    [4   0.1]]
   {:layout :as-row}))

^kind/dataset
data

;; Now we can center each column arround 0 and scale
;; it by the standard deviation  of the column

^kind/dataset
(mm/pipe-it
 data
 (preprocessing/std-scale [0 1] {}))


(docu-fn (var preprocessing/min-max-scale))

;; The `min-max` scaler scales columns in a specified interval,
;; by default from -0.5 to 0.5

^kind/dataset
(mm/pipe-it
 data
 (preprocessing/min-max-scale [0 1] {}))

(docu-fn (var projections/reduce-dimensions))

;;### PCA example

;; In this example we run [PCA](https://en.wikipedia.org/wiki/Principal_component_analysis) on some data.

(require '[scicloj.metamorph.ml.toydata :as toydata])

;; We use the Sonar dataset. It has 60 columns of quantitative data,
;; which are certain measurements from a sonar device.
;; The original purpose of the dataset is to learn to detect rock vs metal
;; from the measurements.

(def sonar
  (toydata/sonar-ds))

; sample 10x10:
^kind/dataset
(ds/select-by-index sonar (range 10) (range 10))


(def col-names (map #(keyword (str "x" %))
                    (range 60)))

;; First we create and run a pipeline that computes the PCA.
;; In this pipeline we do not fix the number of columns, as we want to
;; plot the result for all numbers of components (up to 60).

(def fitted-ctx
  (mm/fit
   sonar
   (projections/reduce-dimensions :pca-cov 60
                         col-names
                         {})))


;; The next function transforms the result from the fitted pipeline
;; into [vega-lite](https://vega.github.io/vega-lite/)-compatible format for plotting.
;; It accesses the underlying Smile Java object to get the data on
;; the cumulative variance for each PCA component.
(defn create-plot-data [ctx]
  (map
   #(hash-map :principal-component %1
              :cumulative-variance %2)
   (range)
   (-> ctx vals (nth 2) :fit-result :model bean :cumulativeVarianceProportion)))

;; Next we plot the cumulative variance over the component index:
^kind/vega-lite
{:$schema "https://vega.github.io/schema/vega-lite/v5.json"
 :width 850
 :data {:values
        (create-plot-data fitted-ctx)}
 :mark "line" ,
 :encoding
 {:x {:field :principal-component, :type "nominal"},
  :y {:field :cumulative-variance, :type "quantitative"}}}

;; From the plot we see that transforming the data via PCA and reducing
;; it from 60 dimensions to about 25 would still preserve the full variance.
;; Looking at this plot, we could now make a decision, how many dimensions
;; to keep.
;; We could, for example, decide that keeping 60 % of the variance
;; is enough, which would result in keeping the first 2 dimensions.

;; So our pipeline becomes:


(def fitted-ctx
  (mm/fit
   sonar
   (projections/reduce-dimensions :pca-cov 2
                                  col-names
                                  {})

   (ds-mm/select-columns  [:material "pca-cov-0" "pca-cov-1"])
   (ds-mm/shuffle)))

^kind/dataset
(:metamorph/data fitted-ctx)

;; As the data is now 2-dimensional, it is easy to plot:

(def scatter-plot-data
  (-> fitted-ctx
      :metamorph/data
      (ds/select-columns [:material "pca-cov-0" "pca-cov-1"])
      (ds/rows :as-maps)))


^kind/vega
{:$schema "https://vega.github.io/schema/vega-lite/v5.json"
 :data {:values scatter-plot-data}
 :width 500
 :height 500

 :mark :circle
 :encoding
 {:x {:field "pca-cov-0"  :type "quantitative"}
  :y {:field "pca-cov-1"  :type "quantitative"}
  :color {:field :material}}}

;; The plot shows that the reduction to 2 dimensions does not create
;; linear separable areas of `M` and `R`. So a linear model will not be
;; able to predict well the material from the 2 PCA components.

;; It even seems that the reduction to 2 dimensions removes
;; too much information for predicting of the material for any type of model.


(docu-fn (var clustering/cluster))
