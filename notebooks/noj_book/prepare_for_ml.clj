;; # Machine learning specific functionality in `tech.ml.dataset`

;; author: Carsten Behring

;; The library `tech.ml.dataset` contains several functions
;; operating on a dataset, which are mainly used in the context of
;; machine learning. In the following we will introduce those.
;;

(ns noj-book.prepare-for-ml)


;;## Categorical variables
;;One typical problem in machine learning is `classification`,
;;so learning how to categorize data in different categories.
;;Sometimes data in this format is as well called "qualitative data"
;;or data having `discrete` values.
;;
;; These categories are often expressed in Clojure as of
;; being  of type `String` or `keyword`
;;
;; In `dataset` it is the `Column` which has specific support for
;; categorical data.
;;
;; Creating a column out of categorical data looks like this:
(require '[tech.v3.dataset.column :as col]
         '[tech.v3.dataset :as ds])
(def column-x (col/new-column  :x  [:a :b]))

;; This creates a "categorical" column, which is marked as such in
;; the column metadata.

;; Printing the var shows its "type" as being `keyword`
column-x
;; and printing its metadata shows that it got marked as `categorical`
(meta column-x)

;; The column is therefore using its metadata to store important
;; information, and it is important to get used to look at it
;; for the case of debugging issues.
;;
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

;; ### Transform categorical variables to numerical space
;; Most machine learning models can only work on numerical values,
;; both for features and the target variable.
;; So usually we need to transform categorical data into a numeric
;; representation, so each category need to be converted to a number.
;;
;;  These numbers have often no meaning for the users,
;;  so often we need to convert back into
;;  String / keyword space later on.
;;
;; Namespace `tech.v3.dataset.categorical`
;; has several functions to do so.
;;
;; ### Transform categorical column into a numerical column
(require  '[tech.v3.dataset.categorical :as ds-cat])

;; These functions operate on a single column, but expect a dataset and
;; a column name as input.
;;
;; We use them to calculate a mapping from string/keyword to a
;; numerical space (0 ... x) like this

(ds-cat/fit-categorical-map categorical-ds :x)

;; This maps the values in their order of occurrence in the column to
;; 0 .. 1
;; This is a bit dangerous, as the mapping is decided by "row order",
;; which could change or be different on other subset of the data, like
;; test/train splits
;;
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


;; ## Convert several columns in one go

;; The `dataset` namespace has a convenience function
;; in which several columns can be selected for conversion.
(ds/categorical->number categorical-ds [:x :y])

;; This works as well with filter function from namespace `column-filters`
(require '[tech.v3.dataset.column-filters :as ds-cf])
;; to convert all categorical columns, for example:
(ds/categorical->number categorical-ds ds-cf/categorical)


(->
 (ds/->dataset {:x [:a :b]
                :y [:c :d]})
 (ds/categorical->number [:x :y] [:a :b :c :d]))

(->
 (ds/->dataset {:x [:a :b]
                :y [:c :d]})
 (ds/categorical->number [:x :y] [:a 0 :b 1 :c 2 :d 3]))

;; ## **Warning:** Pitfalls of Categorical maps
;;
;; ### Automatic mapping might result in surprising results
;; We need to be careful when visually inspecting columns without reverting
;; the categorical maps.
;;
;; Applying the following map to a dataset
(ds-cat/fit-categorical-map (ds/->dataset {:x ["true" "false" ]}) :x)
;; would result in columns in which '0' would mean 'true', and
;; '1' would mean 'false'
;;
;; ### float vs int
;;
;; The categories can get mapped to int or float
(def ds-with-float-and-int-mappings
  (->
   (ds/->dataset {:x-float [:a :b]
                  :x-int [:a :b]})
   (ds/categorical->number [:x-float] [] :float64)
   (ds/categorical->number [:x-int]   [] :int)))

;; Comparing such columns might not bring the expected result, even though the
;; categorical maps and values look very similar
ds-with-float-and-int-mappings
(map meta
     (vals ds-with-float-and-int-mappings))

;; ### Categorical maps attached to a column **change semantic value** of the Column
;;
;;The existence of categorical maps on a column,
;;change the semantic value of the data. When categorical maps
;;are different for two columns (for whatever reasons), it is not given
;;that the column cell value like `0` means the same in both columns.
;;Columns which have categorical maps should never be compared via
;;`clojure.core/=` as this will ignore the categorical maps.
;; (unless we are sure that the categorical maps in both are **the same**)
;; They should be converted back to their original space and then compared.
;; This is specially important for comparing `prediction` and `true value`
;; in machine learning for metric calculations.

;; See the following example to illustrate this.

;; #### Incorrect comparisons
;; In the following the two columns are clearly different
;; (the opposite even)
(def ds-with-different-cat-maps
  (->
   (ds/->dataset {:x-1 [:a :b :a :b :b :b]
                  :x-2 [:b :a :b :a :a :a]})
   (ds/categorical->number [:x-1 :x-2])))

;; The resulting columns look the same, but are not
(:x-1 ds-with-different-cat-maps)
(:x-2 ds-with-different-cat-maps)

;; By using default `categorical->number` we get different categorical
;; maps, having different :lookup-tables
(meta (:x-1 ds-with-different-cat-maps))
(meta (:x-2 ds-with-different-cat-maps))

;;  so they are (wrongly) compared as equal
(=
 (:x-1 ds-with-different-cat-maps)
 (:x-2 ds-with-different-cat-maps))

;; #### Correct comparison
;; In order to compare them correctly,
;; we need to first revert the categorical mappings

(def reverted-ds-with-different-cat-maps
  (ds-cat/reverse-map-categorical-xforms ds-with-different-cat-maps))

(:x-1 reverted-ds-with-different-cat-maps)
(:x-2 reverted-ds-with-different-cat-maps)


;;  and now they compare correctly as :false
(=
 (:x-1 reverted-ds-with-different-cat-maps)
 (:x-2 reverted-ds-with-different-cat-maps))

;; So it should be as well avoided to transform mapped columns
;; to other representations, which loose the mappings, like tensor
;; or primitive arrays, or even sequences

;; #### Use the same and fixed mapping
;; This issue can be avoided by specifying concretely the mapping
;; to be used, as being for example {:a 0  :b 1}
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


;; These 3 pitfalls can be avoided by explicitly specifying the mappings,
;; so using the 4-arity of conversion functions.

(def ds-with-explicit-mapping
  (->
   (ds/->dataset {:x-1 [:a :b :a :b :b :b]
                  :x-2 [:b :a :b :a :a :a]})
   (ds/categorical->number [:x-1 :x-2] [:a :b] :int)))

ds-with-explicit-mapping

(map meta (vals ds-with-explicit-mapping))



;; ### one-hot-encoding
;;
;;For some models / use cases the categorical data need to be converted
;;in the so called `one-hot` format.
;;In this every column get multiplied by the number of categories , and
;;then each one-hot column can only have 0 and 1 values.
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

;;  There are similar functions to convert this format back.
;;

;;  ## Features and inference target in a dataset



;; A dataset for supervised machine learning has always two groups of
;; columns.
;; They can either be the `features` or the `inference targets`.
;; The goal of the learning is to find the relationship between
;; the two groups
;; and therefore be able to `predict` inference targets from features.
;; Sometimes the features are called `X` and the targets `y`.

;;  When constructing a dataset
(def ds
  (ds/->dataset {:x-1 [0 1 0]
                 :x-2 [1 0 1]
                 :y [:a :a :b]}))

;; we need to mark explicitly which columns are `features` and which are
;; `targets` in order to be able to use the dataset later for
;; machine learning in `metamorph.ml`
;;
;; As normally only one or a few columns are inference targets,
;; we can simply mark those and the other columns are regarded as features.

(require  '[tech.v3.dataset.modelling :as ds-mod])
(def modelled-ds
  (-> ds
      (ds-mod/set-inference-target :y)))
;; (works as well with a seq)


;; This is marked as well in the column metadata.
(-> modelled-ds :y meta)


;; There are several functions to get information on features and
;; inference targets:

(ds-mod/feature-ecount modelled-ds)

(ds-cf/feature modelled-ds)

(ds-cf/target modelled-ds)



;; ## Combining categorical transformation and modelling
;;
;;
;;  Very often we need to do transform and model for doing
;;  classification and
;;  combine the ->numeric transformation of categorical vars
;;  and the marking of inference targets.
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
;;  only contains numerical variables which have the categorical maps
;;  in place for easy converting back, if needed.
;;  The inference target is marked as well,
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
(require 'tech.v3.dataset.tensor)

(def ds-tensor
  (tech.v3.dataset.tensor/dataset->tensor ds-ready-for-train))
ds-tensor

;;  or we can do so, if needed, but this looses the notation of features /
;;  inference target
(tech.v3.tensor/->jvm ds-tensor)
