(ns noj-book.render-tools-sklearn
  (:require
   [libpython-clj2.python :as py]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml :as ml]))

(py/initialize!)
(def doc->markdown (py/import-module "docstring_to_markdown"))

(defn docu-doc-string [model-key]
  (try
    (kind/md
     (py/py. doc->markdown convert
             (or
              (get-in @ml/model-definitions* [model-key :documentation :doc-string]) "")))
    (catch Exception e "")))
