(ns noj-book.render-tools
  (:require
   [clj-http.client :as client]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [tablecloth.pipeline :as tc-mm]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [tablecloth.api :as tc]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf]
  )
  

(defn anchor-or-nothing [x text]
  (if (empty? x)
    [:div ""]
    [:div
     [:a {:href x} text]]))


(defn stringify-enum [form]
  (walk/postwalk (fn [x] (do (if  (instance? Enum x) (str x) x)))
                 form))
(defn docu-options[model-key]
  (->
   (tc/dataset
    (or
     (get-in @ml/model-definitions* [model-key :options])
     {:name [] :type [] :default []}))

   (tc/reorder-columns :name :type :default)))




(defn flatten-one-level [coll]
  (mapcat  #(if (sequential? %) % [%]) coll))

(str/replace "hello" "he" "" )


(defn render-key-info 
  ([prefix {:keys [level remove-s docu-doc-string-fn]}]
   (->> @ml/model-definitions*
        (sort-by first)
        (filter #(str/starts-with? (first %) (str prefix)))
        (mapcat (fn [[key definition]]
                  (let [print-key (str/replace-first key remove-s "" )
                         ]
                    [(kind/md (str level " " print-key))
                     (kind/hiccup
                      [:span
                       (anchor-or-nothing (:javadoc (:documentation definition)) "javadoc")
                       (anchor-or-nothing (:user-guide (:documentation definition)) "user guide")

                       (let [docu-ds (docu-options key)]
                         (if  (tc/empty-ds? docu-ds)
                           ""
                           (->
                            docu-ds
                            (tc/rows :as-maps)
                            seq
                            stringify-enum
                            (kind/table))))
                       [:span
                        (when (fn? docu-doc-string-fn)
                          (docu-doc-string-fn key)
                          )
                        ]

                       [:hr]
                       [:hr]])])))
        kind/fragment))
  
  ( [prefix] (render-key-info prefix {:level "##"
                                      :remove-s ""})))

(defn kroki [s type format]
  (client/post "https://kroki.io/" {:content-type :json
                                    :as :byte-array
                                    :form-params
                                    {:diagram_source s
                                     :diagram_type (name type)
                                     :output_format (name format)}}))

(defn stepped-range [start end n-steps]
  (let [diff (- end start)]
    (range start end (/ diff n-steps))))

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
    {
     :layer
     [{:data {:values (seq (tc/rows grid-ds-prediction :as-maps))}
       :title (str "Decision surfaces for model: " model-name " - " cols)
       ;:width 400
       ;:height 400
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

(def iris-test
  (tc/dataset
   "https://raw.githubusercontent.com/scicloj/metamorph.ml/main/test/data/iris.csv" {:key-fn keyword}))




;; Standarise the data:
(def iris-std
  (mm/pipe-it
   iris-test
   (preprocessing/std-scale [:sepal_length :sepal_width :petal_length :petal_width] {})))



