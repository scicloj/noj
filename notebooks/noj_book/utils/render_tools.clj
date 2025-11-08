(ns noj-book.utils.render-tools
  (:require
   [clojure.string :as str]
   [clojure.walk :as walk]
   [noj-book.utils.example-code :refer [example-code]]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml :as ml]
   [tablecloth.api :as tc]))
  

(defn anchor-or-nothing [x text]
  (if (empty? x)
    [:div ""]
    [:div
     [:a {:href x} text]]))


(defn stringify-enum [form]
  (walk/postwalk (fn [x] (do (if  (instance? Enum x) (str x) x)))
                 form))

(def model-key :smile.classification/ada-boost)
(defn docu-options [model-key]
  (let [options (get-in @ml/model-definitions* [model-key :options])
        params (->> options (drop 2))
        names
        (map first params)

        defaults
        (map #(-> % second :default)  params)

        types
        (map #(-> % second :type)  params)
        

        descriptions
        (map #(-> % second :description) params)

        lookup-tables
        (repeat (count params) nil)
        ds
        (->
         (tc/dataset
          {:name names
           :type types
           :default defaults
           :description descriptions})
         (tc/reorder-columns :name :type :default :description))]
    (if  (some some? lookup-tables)
      (->
       (assoc ds :lookup-table lookup-tables)
       (tc/reorder-columns :name :type :default :description :lookup-table))
      ds)))

(some some? [nil 1])

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
       [:hr]])]))

(defn render-key-info 
  ([prefix {:keys [level remove-s 
                   docu-doc-string-fn]
            :or {level "##"
                 remove-s ""}}]
   
   (kind/fragment
    (concat
     (->> @ml/model-definitions*
          (sort-by first)
          (filter #(str/starts-with? (first %) (str prefix)))
          (mapcat 
           (fn [[key definition]]
             (concat
              (render-info-block key definition remove-s level docu-doc-string-fn)
              (get example-code key))))))))
  
  ([prefix] (render-key-info prefix {:level "##"
                                     :remove-s ""})))



