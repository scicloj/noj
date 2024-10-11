;; ## Introduction - Chapter 01
(ns introduction-c01
  (:require [fastmath.core :as fm-core :refer [sigmoid, round]]
            [fastmath.stats :as fm-stats :refer [standardize]]
            [fastmath.random :as fm-rand]
            [fastmath.vector :as fm-vec]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [aerial.hanami.core :as hmi-core]
            [aerial.hanami.templates :as hmi-tmplate]
            [aerial.hanami.common :as hmi-cmmon]
            [tablecloth.api :as tc]
            [scicloj.hanamicloth.v1.plotlycloth :as ploclo]
            [tablecloth.column.api :as tcc]
            [tech.v3.datatype.datetime :as datetime]
            [tech.v3.dataset.print :as print]
            [scicloj.kindly.v4.kind :as kind]
            [clojure.string :as str]
            [scicloj.kindly.v4.api :as kindly]
            ))

;; ### Set initial seed for sampling  operations
(def seed (let [seed 123
                _ (fm-rand/set-seed! seed)]
            seed))
(def n 100)

(defn sample-gaussian [mean std-dev size]
  (->> {:mu mean :sd std-dev}
       (fm-rand/distribution :normal)
       fm-rand/->seq
       (take size)
       fm-vec/array-vec))

;; ### Tuition dataset
(def tuitions
  (->> [1000 300 n]
       (apply sample-gaussian)
       fm-vec/round))
(take 5 tuitions)

;; ### Tablets dataset: 3 ways of calculation

;; * brevity oriented
(def tablets
  (pmap #(->> {:trials 1 :p %}
              (fm-rand/distribution :binomial)
              fm-rand/sample
              boolean)
        (fm-vec/sigmoid (fm-vec/div (fm-vec/shift tuitions (- (fm-stats/mean tuitions)))
                                    (fm-stats/stddev tuitions)))))

;; * a fastmath oriented code
(def tablets
  (pmap fm-rand/brand
        (fm-vec/sigmoid (fm-vec/div (fm-vec/shift tuitions (- (fm-stats/mean tuitions)))
                                    (fm-stats/stddev tuitions)))))

;; * vector oriented code
(def tablets
  (do (fm-vec/fmap
      (fm-vec/sigmoid (fm-vec/div (fm-vec/shift tuitions (- (fm-stats/mean tuitions)))
                                  (fm-stats/stddev tuitions)))
      #(->> {:trials 1 :p %}
            (fm-rand/distribution :binomial)
            fm-rand/sample))))
(take 5 tablets)

;; ### The ENEM scores
(def enem-score
  (doall (-> (fm-vec/fmap (fm-vec/shift (fm-vec/add (fm-vec/mult tablets -50)
                                                    (fm-vec/mult tuitions 0.7))
                                        200)
                          #(fm-rand/grand % 200))
             fm-stats/rescale
             (fm-vec/mult 1000))))
(take 10 enem-score)


;; ### The combined dataset: Tuitions, Tablets, ENEM scores
(def data
  (-> {:enem-score enem-score
       :tuitions (vec tuitions)
       :tablets (mapv {0.0 false 1.0 true 0 false 1 true} tablets)}
      (tc/dataset {:dataset-name "ENEM score by Tablet in Class"})))
(print/print-range data 6)



;; ### ENEM scores against Tablets
(-> data
    (ploclo/layer-boxplot
     {:=x :tablets
      :=y :enem-score
      :=title "ENEM score by Tablet in Class"
      :=color :tablets}))  

;; ### ATE vs ATT
(-> {:i [1 2 3 4]
     :Y0 [500 600 800 700]
     :Y1 [450 600 600 750]
     :T  [0 0 1 1]
     :Y  [500 600 600 750]
     :TE [-50 0 -200 50]}
    tc/dataset
    (print/print-range 10))

(-> {:i [1 2 3 4]
     :Y0 [500 600 800 700]
     :Y1 [450 600 600 750]
     :T  [0 0 1 1]
     :Y  [500 600 600 750]
     :TE [##NaN ##NaN ##NaN ##NaN]}
    tc/dataset
    (print/print-range 10))

;; ### ENEM score by Tuition Cost
(-> data
    (ploclo/layer-point
     {:=x :tuitions
      :=y :enem-score
      :=title "ENEM score by Tuition Cost"
      :=color :tablets}))  
