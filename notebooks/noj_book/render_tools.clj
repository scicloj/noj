(ns noj-book.render-tools
  (:require
   [clj-http.client :as client]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.core :as mm]
   [scicloj.metamorph.ml :as ml]
   [scicloj.metamorph.ml.preprocessing :as preprocessing]
   [scicloj.metamorph.ml.toydata :as datasets]
   [tablecloth.api :as tc]
   [tablecloth.pipeline :as tc-mm]
   [tech.v3.dataset.metamorph :as ds-mm]
   [tech.v3.dataset.modelling :as ds-mod]
   [tech.v3.datatype.functional :as dtf])
  )

^:kindly/hide-code
(defn anchor-or-nothing [x text]
  (if (empty? x)
    [:div ""]
    [:div
     [:a {:href x} text]]))

^:kindly/hide-code
(defn stringify-enum [form]
  (walk/postwalk (fn [x] (do (if  (instance? Enum x) (str x) x)))
                 form))
^:kindly/hide-code
(defn docu-options [model-key]
  (->
   (tc/dataset
    (or
     (get-in @ml/model-definitions* [model-key :options])
     {:name [] :type [] :default []}))

   (tc/reorder-columns :name :type :default)))

^:kindly/hide-code
(defn docu-doc-string [model-key]
  ;;TODO needed ?
;;   (try
;;     (view/markdowns->hiccup
;;      (py/py. doc->markdown convert
;;              (or
;;               (get-in @scicloj.ml.core/model-definitions* [model-key :documentation :doc-string]) "")))
;;     (catch Exception e ""))
  "")
^:kindly/hide-code
(defn flatten-one-level [coll]
  (mapcat  #(if (sequential? %) % [%]) coll))


^:kindly/hide-code
(defn render-key-info [prefix]
  (->> @ml/model-definitions*
       (sort-by first)
       (filter #(str/starts-with? (first %) (str prefix)))
       (mapcat (fn [[key definition]]
                 [(kind/md (str "### " key))
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
                     (docu-doc-string key)]

                    [:hr]
                    [:hr]])]))
       kind/fragment))

^:kindly/hide-code
(defn kroki [s type format]
  (client/post "https://kroki.io/" {:content-type :json
                                    :as :byte-array
                                    :form-params
                                    {:diagram_source s
                                     :diagram_type (name type)
                                     :output_format (name format)}}))

;; (->> ["purple" "darkgreen" "brown"]
;;      (mapcat (fn [color]
;;                [(kind/md (str "### subsection: " color))
;;                 (kind/hiccup [:div {:style {:background-color color
;;                                             :color "lightgrey"}}
;;                               [:big [:p color]]])]))
;;      kind/fragment)
