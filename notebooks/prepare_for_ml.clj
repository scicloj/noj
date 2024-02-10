(ns prepare-for-ml
  (:require [scicloj.ml.metamorph :as ml]

            [tech.v3.dataset :as ds]
            [scicloj.kindly.v4.kind :as kind]))



(comment
  (require '[scicloj.clay.v2.api :as clay]
           '[tech.v3.dataset.column :as col])




  (clay/start!))

;; # Machine learning specific functionality in `tech.ml.dataset`
;; The library `tech.ml.dataset` contains several functions
;; operating on a dataset, which are mainly used in the context of
;; machine learining. In the following we will introduce those.
;;

;;## Categorical variables
;;One typical problem in machine learning is `classification`,
;;so learning how to categorize data in different categories.
;;Sometimes data in this format is as well called "qualitative data"
;;or data having `discrete` values

;; These categories are often expressed in Clojure as of
;; being  of type `String` or `keyword`
;;
;; In `dataset` it is the `Column` which has specific support for
;; categorical data

;; Creating a column out of categorical data looks like this:
(require '[tech.v3.dataset.column :as col])
(def column-x (col/new-column  :x  [:a :b]))

;; This creates a "categorical" column, which is marked as such in
;; the column metadata.

;; Printing the var shows its "type" as being `keyword`
column-x
;; and printing its metadata show that it got marked as `categorical`
(meta column-x)

;; The column is therefore using its metadata to store important information,
;; and it is important to get used to look at it for the case of debugging
;; issues.
;; The same happens, when creating a `dataset` which is a seq
;; of columns
;;
(def categorical-ds
  (ds/->dataset
   {:x [:a :b] :y ["c" "d"]}))

categorical-ds

(map
 meta
 (vals categorical-ds))

;; ### Express categorical variables in numeric space
;; Most machine learining models can only work on numerical values,
;; both for features and the target variable.
;; So usually we need to transform categorical data into a numeric representation,
;; so each category need to be converted to a number.
;;  These numbers have no meaning for the users, so often we need to
;;  convert back into
;;  String / keyword space later on.
;;
;; Namespace `tech.ml.dataset.classification`
;; has several functions to do so.
;;
;; ### Transform categorical column into a numerical column
(require  '[tech.v3.dataset.categorical :as ds-cat])

;; These function operate on a single column, but expect a dataset and
;; a column name as input.
;;
;; We use them to find a mapping from string/keyword to a
;; numerical space (0 ... x) like this

(ds-cat/fit-categorical-map categorical-ds :x)

;; This maps value in the order of occurrence in the column to
;; 0 .. 1
;; This is a bit dangerous, as the mapping is decided by "row order",
;; which could change or be different on other subset of the data
;; So it is preferred to be specified explicitly.

(def x-mapping (ds-cat/fit-categorical-map categorical-ds :x [:a :b]))
x-mapping
;;  Now we know for sure, that :a is mapped to 0 and :b is mapped to 1.
;;  Once we have a mapping, we can use it on new data and transform it
;;  into numerical values
(def numerical-categorical-data
  (ds-cat/transform-categorical-map
   (ds/->dataset {:x [:a :b :a :b :b :b]})
   x-mapping))
numerical-categorical-data

;; We can revert it as well:
;;
(ds-cat/invert-categorical-map numerical-categorical-data x-mapping)

;;  We can as well ask about all mapping of a dataset:
(ds-cat/dataset->categorical-maps numerical-categorical-data)


;; ## **Warning:** Categorical maps attached to a column **change semantic value** of the Column
;;
;;The existence of categorical maps on a column,
;;change the semantic value of the data. When categorical maps
;;are different for two columns (for whatever reasons), it is not given
;;that the column cell value like `0` means the same in both columns.
;;Columns which have categorical maps should never be compared via
;;'clojure.core/=' as this will ignore the categorical maps.
;; (unless we are sure that the categorical maps in both are **the same**)
;; They should be converted back to their original space and then compared.
;; This is specially important for comparing `prediction` and `true value`
;; in machine learning for metric calculations.

;; ### Incorrect comparisons
;; In this ds the two columns are clearly different (the opposite even)
(def ds-with-different-cat-maps
  (->
   (ds/->dataset {:x-1 [:a :b :a :b :b :b]
                  :x-2 [:b :a :b :a :a :a]})
   (ds/categorical->number [:x-1 :x-2])))


(:x-1 ds-with-different-cat-maps)
(:x-2 ds-with-different-cat-maps)

;; By using default `/categorical->number` we get different categorical
;; maps, having different :lookup-tables
(meta (:x-1 ds-with-different-cat-maps))
(meta (:x-2 ds-with-different-cat-maps))

;;  so they are (wrongly) compared as equal
(=
 (:x-1 ds-with-different-cat-maps)
 (:x-2 ds-with-different-cat-maps))

;; ### Correct comparison
;; In order to compare them correctly,
;; we need to first revert the categorical mappings

(def reverted-ds-with-different-cat-maps
  (ds-cat/reverse-map-categorical-xforms ds-with-different-cat-maps))

(:x-1 reverted-ds-with-different-cat-maps)
(:x-2 reverted-ds-with-different-cat-maps)


;;  and can know compare them correctly as :false
(=
 (:x-1 reverted-ds-with-different-cat-maps)
 (:x-2 reverted-ds-with-different-cat-maps))

;; So it should be as well avoid to transform mapped columns
;; to other representations, which loose the mappings, like tensor
;; or primitive arrays, or even sequences

;; ### Better use same and fixed mapping
;; This issue can be avoided by specifying the mapping to use, as being
;; {:a 0  :b 1}
(def ds-with-same-cat-maps
  (->
   (ds/->dataset {:x-1 [:a :b :a :b :b :b]
                  :x-2 [:b :a :b :a :a :a]})
   (ds/categorical->number [:x-1 :x-2] [:a :b])))

;; mapping spec can be either [:a :b] or  [:a 0 :b 1]

(:x-1 ds-with-same-cat-maps)
(:x-2 ds-with-same-cat-maps)

;;  we get same categorical maps
(meta (:x-1 ds-with-same-cat-maps))
(meta (:x-2 ds-with-same-cat-maps))

;;  so they are correctly compared as not equal
(=
 (:x-1 ds-with-same-cat-maps)
 (:x-2 ds-with-same-cat-maps))


;; ## Convert several columns in one go

;;  The `dataset` namespace has as well a convenience function
;;  in which several columns can be choose for conversion.
(ds/categorical->number categorical-ds [:x :y])

;; This works as well with filter function from namespace `column-filters`
(require '[tech.v3.dataset.column-filters :as ds-cf])
;; to convert all categorical columns, for example:
(ds/categorical->number categorical-ds ds-cf/categorical)

;; ### one-hot-encoding
;;
;;For some models / use cases the categorical data need to be converted
;;in the so called `one-hot` format.
;;In this every column get multiplied by the number of categories , and
;;the each one-hot column can only have 0 and 1 values.
;;
(def one-hot-map-x (ds-cat/fit-one-hot categorical-ds :x))
(def one-hot-map-y (ds-cat/fit-one-hot categorical-ds :y))
one-hot-map-x
one-hot-map-y

categorical-ds

;;  get transformed by

(def one-hot-ds
  (-> categorical-ds
      (ds-cat/transform-one-hot one-hot-map-x)
      (ds-cat/transform-one-hot one-hot-map-y)))

;; into

one-hot-ds

;;  There are similar functions to convert this format back
;;

;;  ## Features and inference target in a dataset



;; A dataset for machine learning has always two groups of columns.
;; They can either be the `features` or the `inference targets`.
;; The goal of learining is to find the relation ship between
;; the two groups
;; and therefore be able to `predict` inference targets from features.
;; Sometimes the features are called `X` and the targets `y`.

;;  When constructing a dataset
(def ds
  (ds/->dataset {:x-1 [0 1 0]
                 :x-2 [1 0 1]
                 :y [:a :a :b]}))

;; we need to mark explicitely which columns are `features` and which are `target`
;; in order to be able to use it later for machine learning in `metamorph.ml`
;;
;; As normally only one or a few columns are inference targets,
;; we can simply mark those and the rest is regarded as features.

(require  '[tech.v3.dataset.modelling :as ds-mod])
(def modelled-ds
  (-> ds
      (ds-mod/set-inference-target :y)))     ; works as well with seq

;; This is marked as well in the column metadata.
(-> modelled-ds :y meta)


;; There are several functions to get information on features and inference targets:

(ds-mod/feature-ecount modelled-ds)

(ds-cf/feature modelled-ds)

(ds-cf/target modelled-ds)



;; ## Combining categorical transformation and modelling
;;
;;
;;  Very often we need to do transform and model for doing
;;  classification and
;;  combine the ->numeric transformation of categorical vars
;;  and the marking of inference target
(def ds-ready-for-train
  (->
   {:x-1 [0 1 0]
    :x-2 [1 0 1]
    :cat  [:a :b :c]
    :y [:a :a :b]}

   (ds/->dataset)
   (ds/categorical->number [:y])
   (ds/categorical->one-hot [:cat])
   (ds-mod/set-inference-target [:y])))

ds-ready-for-train

;;  Such a dataset is ready for training as it
;;  only contains numerical variables (having the categorical map in place for easy converting back, if needed)
;;  and the inference target is marked,
;;  as we can see in the meta data:
;;
(map meta (vals ds-ready-for-train))
;;
;; Most models in the `metamorph.ml` ecosystem can work with
;; data in this format.
;;
;;
;; Side remark:
;; If needed, data could as well be easily transformed into a tensor.
;; Most models do this internally anyway (often to primitive arrays)
(def ds-tensor
  (tech.v3.dataset.tensor/dataset->tensor ds-ready-for-train))
ds-tensor

;;  or we can do so, if needed, but this looses the notation of features /
;;  inference target
(tech.v3.tensor/->jvm ds-tensor)
