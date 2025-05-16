;; # Underlying libraries

^:kindly/hide-code
(ns noj-book.underlying-libraries 
  (:require
   [clojure.edn :as edn]
   [scicloj.kindly.v4.kind :as kind]
   [tablecloth.api :as tc]))

;; Noj consists of the following libraries:

;; (See also the list of [other recommended libraries](./noj_book.recommended_libraries.html),
;; which are not included in Noj.)

;; ## High-performance computing
;; * [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) (TMD) - high-perfrormance table processing [(reference)](https://techascent.github.io/tech.ml.dataset/)
;; * [dtype-next](https://github.com/cnuernber/dtype-next) - high-performance array-programming [(reference)](https://cnuernber.github.io/dtype-next/)
;; * [ham-fisted](https://github.com/cnuernber/ham-fisted) - high-performace data structures and operations [(reference)](https://cnuernber.github.io/ham-fisted/)

;; ## Data processing
;; * [Tablecloth](https://github.com/scicloj/tablecloth) - dataset processing on top of TMD [(reference)](https://scicloj.github.io/tablecloth/)
;; * [tcutils](https://github.com/scicloj/tcutils) - utility functions for Tablecloth datasets - 🛠 early stage [(reference)](https://scicloj.github.io/tcutils/)
;; * [tmd-parquet](https://github.com/techascent/tech.parquet) - TMD bindings for [Parquet](https://parquet.apache.org/) format
;; * [clojure.java-time](https://github.com/dm3/clojure.java-time) - Java 8 Date-Time API for Clojure [(reference)](https://dm3.github.io/clojure.java-time/)

;; ## Math and statistics
;; * [Fastmath](https://github.com/generateme/fastmath) - math & stats - alpha stage of version 3 [(reference)](https://generateme.github.io/fastmath/clay/)
;; * [Fitdistr](https://github.com/generateme/fitdistr)
;; * [same-ish](https://github.com/microsoft/same-ish) - approximate comparisons - useful for notebook testability

;; ## Machine learning
;; * [metamorph.ml](https://github.com/scicloj/metamorph.ml) - machine learning platform [(reference)](https://cljdoc.org/d/scicloj/metamorph.ml)
;; * [scicloj.ml.tribuo](https://github.com/scicloj/scicloj.ml.tribuo) - [Tribuo](https://tribuo.org/) machine learning models
;; * [sklearn-clj](https://github.com/scicloj/sklearn-clj) - Plugin to use [sklearn](https://scikit-learn.org/) models in metamorph.ml

;; ## Visualization
;; * [Tableplot](https://github.com/scicloj/tableplot) - easy layered graphics [(reference)](https://scicloj.github.io/tableplot/)
;; * [Hanami](https://github.com/jsa-aerial/hanami) - interactive datavis
;; * [Emmy-viewers](https://github.com/mentat-collective/emmy-viewers?tab=readme-ov-file) - math visualization
;; * [Kindly](https://github.com/scicloj/kindly-noted) - datavis standard [(reference)](https://scicloj.github.io/kindly-noted/kindly)

;; ## Bridges to other languages
;; * [libpython-clj](https://github.com/clj-python/libpython-clj) - Python bindings [(reference)](https://clj-python.github.io/libpython-clj/)
;; * [kind-pyplot](https://github.com/scicloj/kind-pyplot) - Python plotting [(reference)](https://scicloj.github.io/kind-pyplot/)
;; * [ClojisR](https://github.com/scicloj/clojisr) - R bindings [(reference)](https://clj-python.github.io/libpython-clj/)

;; ## Notebooks
;; * [Clay](https://github.com/scicloj/clay) - REPL-friendly notebooks and datavis [(reference)](https://scicloj.github.io/clay/)

^:kindly/hide-code
(def direct-deps
  (->
   (edn/read-string (slurp "deps.edn"))
   :deps
   keys
   ))
^:kindly/hide-code
(def all-deps-info
  (-> 
   (clojure.java.shell/sh  "clj" "-X:deps" "list" ":format" ":edn")
   :out
   (edn/read-string)))

^:kindly/hide-code
(def direct-deps-info 
  (select-keys
   all-deps-info
   direct-deps))

;; ## List of all direct noj dependencies 

^:kindly/hide-code
(kind/table
 (->
  (map
   (fn [[dep info]]
     (hash-map :lib (str dep)
               :version (:mvn/version info)
               :license (-> info :license :name))
     )
   direct-deps-info)
  (tc/dataset)
  (tc/select-columns [:lib :version :license])
  (tc/order-by [:lib])))
