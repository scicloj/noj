;; # Underlying libraries

^:kindly/hide-code
(ns noj-book.underlying-libraries)

;; Noj consists of the following libraries:

;; (See also the list of [other recommended libraries](./noj_book.recommended_libraries.html),
;; which are not included in Noj.)

;; ## High-performance computing
;; * [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) (TMD) - high-perfrormance table processing [(reference)](https://techascent.github.io/tech.ml.dataset/)
;; * [dtype-next](https://github.com/cnuernber/dtype-next) - high-performance array-programming [(reference)](https://cnuernber.github.io/dtype-next/)

;; ## Data processing
;; * [Tablecloth](https://github.com/scicloj/tablecloth) - dataset processing on top of TMD [(reference)](https://scicloj.github.io/tablecloth/)
;; * [tcutils](https://github.com/scicloj/tcutils) - utility functions for Tablecloth datasets - 🛠 early stage [(reference)](https://scicloj.github.io/tcutils/)
;; * [tmd-parquet](https://github.com/techascent/tech.parquet) - TMD bindings for [Parquet](https://parquet.apache.org/) format
;; * [clojure.java-time](https://github.com/dm3/clojure.java-time) - Java 8 Date-Time API for Clojure [(reference)](https://dm3.github.io/clojure.java-time/)

;; ## Math and statistics
;; * [Fastmath](https://github.com/generateme/fastmath) - math & stats - alpha stage of version 3 [(reference)](https://generateme.github.io/fastmath/clay/)
;; * [same-ish](https://github.com/microsoft/same-ish) - approximate comparisons - useful for notebook testability

;; ## Machine learning
;; * [metamorph.ml](https://github.com/scicloj/metamorph.ml) - machine learning platform [(reference)](https://cljdoc.org/badge/scicloj/metamorph.ml)
;; * [scicloj.ml.tribuo](https://github.com/scicloj/scicloj.ml.tribuo) - [Tribuo](https://tribuo.org/) machine learning models
;; * [scicloj.ml.smile](https://github.com/scicloj/scicloj.ml.smile) - [Smile](https://haifengl.github.io/) (v 2.6) machine learning models
;; * [sklearn-clj](https://github.com/scicloj/sklearn-clj) - Plugin to use [sklearn](https://scikit-learn.org/) models in metamorph.ml

;; ## Visualization
;; * [Tableplot](https://github.com/scicloj/tableplot) - easy layered graphics - 🛠 alpha version - should stabilize soon [(reference)](https://scicloj.github.io/tableplot/)
;; * [Hanami](https://github.com/jsa-aerial/hanami) - interactive datavis
;; * [Emmy-viewers](https://github.com/mentat-collective/emmy-viewers?tab=readme-ov-file) - math visualization
;; * [Kindly](https://github.com/scicloj/kindly-noted) - datavis standard [(reference)](https://scicloj.github.io/kindly-noted/kindly)

;; ## Bridges to other languages
;; * [libpython-clj](https://github.com/clj-python/libpython-clj) - Python bindings [(reference)](https://clj-python.github.io/libpython-clj/)
;; * [kind-pyplot](https://github.com/scicloj/kind-pyplot) - Python plotting [(reference)](https://scicloj.github.io/kind-pyplot/)
;; * [ClojisR](https://github.com/scicloj/clojisr) - R bindings [(reference)](https://clj-python.github.io/libpython-clj/)
