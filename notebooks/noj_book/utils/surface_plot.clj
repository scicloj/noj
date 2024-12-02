(ns noj-book.utils.surface-plot 
  (:require
   [scicloj.metamorph.core :as mm]
   [tablecloth.api :as tc]
   [tablecloth.pipeline :as tc-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf]))


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
    {:layer
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



