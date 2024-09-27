;; # Underlying libraries

^:kindly/hide-code
(ns noj-book.underlying-libraries)

;; Noj consists of the following libraries:

;; * [Tablecloth](https://scicloj.github.io/tablecloth/) - dataset processing on top of TMD
;; * [tcutils](https://scicloj.github.io/tcutils/) - utility functions for Tablecloth datasets - 🛠 early stage
;; * [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) (TMD) - high-perfrormance table processing
;; * [tmd-parquet](https://github.com/techascent/tech.parquet) - TMD bindings for [Parquet](https://parquet.apache.org/) format
;; * [dtype-next](https://github.com/cnuernber/dtype-next) - high-performance array-programming
;; * [Kindly](https://scicloj.github.io/kindly-noted/) - datavis standard
;; * [Fastmath](https://github.com/generateme/fastmath) - math & stats - alpha stage of version 3
;; * [Hanamicloth](https://scicloj.github.io/hanamicloth/) - easy layered graphics - 🛠 alpha version - should stabilize soon
;; * [Hanami](https://github.com/jsa-aerial/hanami) - interactive datavis
;; * [metamorph.ml](https://github.com/scicloj/metamorph.ml) - machine learning platform
;; * [scicloj.ml.tribuo](https://github.com/scicloj/scicloj.ml.tribuo) - [Tribuo](https://tribuo.org/) machine learning models
;; * [scicloj.ml.smile](https://github.com/scicloj/scicloj.ml.smile) - [Smile](https://haifengl.github.io/) (v 2.6) machine learning models
;; * [sklearn-clj](https://github.com/scicloj/sklearn-clj) -
;; * some Tribuo modules added by default: general-linear and tree ensembles for regression/classification
;; * [libpython-clj](https://github.com/clj-python/libpython-clj) - Python bindings
;; * [kind-pyplot](https://scicloj.github.io/kind-pyplot/) - Python plotting
;; * [ClojisR](https://scicloj.github.io/clojisr/) - R bindings
;; * [same-ish](https://github.com/microsoft/same-ish) - approximate comparisons - useful for notebook testability
