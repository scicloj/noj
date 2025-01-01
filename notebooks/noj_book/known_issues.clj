;; # Known issues ❗

^:kindly/hide-code
(ns noj-book.known-issues
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]))

;; ## Fetching dependencies

;; If you encounter:
;; _Error building classpath. Could not acquire write lock for 'artifact:org.bytedeco:mkl'_

;; Try:
;; ```bash
;; clj -P -Sthreads 1
;; ```
;; from your terminal.

;; See [Ask Clojure](https://ask.clojure.org/index.php/12730/error-could-acquire-write-lock-artifact-org-bytedeco-opencv).

;; ## Native Dependencies
;; Several of the machine learning models included in Noj are known to have native, operating system level,
;; dependencies. For most OS these get "autoinstalled" via depedencies to jars.
;; 
;; For some OS this is not working or needs manual instalations or configuration.
;;
;; We maintain a complete integration test suite, which exercises all models, and we have a known-to-work
;; [devcontainer setup](https://github.com/scicloj/noj/tree/main/.devcontainer) in which all models do work.
;;
;; This can be used for reference or as a starting point to use Noj, if one desires.
;; ### scicloj.ml.smile
;; Noj is often used together with `scicloj.ml.smile`, and
;; some of the Smile based models require native dependencies. This is documented in [Smile](https://github.com/haifengl/smile)


