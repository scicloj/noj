(ns noj-book.render-tools
  (:require
   [clj-http.client :as client]
   [clojure.pprint :as pprint]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [tablecloth.api :as tc]
   [tablecloth.pipeline :as tc-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf]
   [noj-book.example-code :refer [example-code]]
   ))
  

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


(defn render-info-block [key definition remove-s level docu-doc-string-fn]
  (let [print-key (str/replace-first key remove-s "")]
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
          (docu-doc-string-fn key))]
  
       [:hr]
       [:hr]])])

)

(defn render-key-info 
  ([prefix {:keys [level remove-s 
                   docu-doc-string-fn
                   
                   ]
            :or {level "##"
                 remove-s ""}
            }
    
    ]
   
   ( kind/fragment
    (concat
     (->> @ml/model-definitions*
          (sort-by first)
          (filter #(str/starts-with? (first %) (str prefix)))
          (mapcat 
           (fn [[key definition]]
             (render-info-block key definition remove-s level docu-doc-string-fn))))
     (get example-code prefix))))
  
  ( [prefix] (render-key-info prefix {:level "##"
                                      :remove-s ""})))




