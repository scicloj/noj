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

