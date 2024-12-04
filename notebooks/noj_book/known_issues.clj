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

;; See [deps issue report](https://clojurians-log.clojureverse.org/tools-deps/2021-09-16).

