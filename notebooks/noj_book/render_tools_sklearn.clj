
(ns noj-book.render-tools-sklearn
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
   [libpython-clj2.python :as py]))

(py/initialize!)
(def doc->markdown (py/import-module "docstring_to_markdown"))

(defn docu-doc-string [model-key]
  (try
    (kind/md
     (py/py. doc->markdown convert
             (or
              (get-in @ml/model-definitions* [model-key :documentation :doc-string]) "")))
    (catch Exception e "")))
