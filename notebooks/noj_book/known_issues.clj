;; # Known Issues ❗
^:kindly/hide-code
(ns known-issues)

;; ## `clj` fails with "mkl" error

;; If you encounter:
;; _Error building classpath. Could not acquire write lock for 'artifact:org.bytedeco:mkl'_

;; Try: `clj -P -Sthreads 1`

;; Explanation: see [deps issue report](https://clojurians-log.clojureverse.org/tools-deps/2021-09-16).
