^{:kindly/hide-code true
  :kindly/kind :kind/html}
(slurp "notebooks/Noj.svg")

;; # Preface

^:kindly/hide-code
(ns index
  (:require [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [clojure.string :as str]
            [clojure.string :as string]
            [scicloj.clay.v2.api :as clay]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]))

^:kindly/hide-code
(def md
  (comp kindly/hide-code kind/md))

(md "

Scinojure is an entry point to the Clojure stack for data & science.

It collects a few of the main libraries and documents common ways to use them together.

**Source:** [![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)

**Artifact:** [2-alpha1-SNAPSHOT](https://clojars.org/org.scicloj/noj/versions/2-alpha1-SNAPSHOT)

Note we are using `SNAPSHOT` version for now, since a few of the current dependencies are at a snapshot stage for an upcoming release.

**Status:** Most of the [underlying libraries](./noj_book.underlying_libraries.html) are stable. The experimental parts are marked as such. For some of the libraries, we use a branch for an upcoming release.

## Getting started
coming soon

## Existing chapters in this book:
")

^:kindly/hide-code
(defn chapter->title [chapter]
  (or (some->> chapter
               (format "notebooks/noj_book/%s.clj")
               slurp
               str/split-lines
               (filter #(re-matches #"^;; # .*" %))
               first
               (#(str/replace % #"^;; # " "")))
      chapter))

(->> "notebooks/chapters.edn"
     slurp
     clojure.edn/read-string
     (mapcat (fn [{:keys [part chapters]}]
               (cons (format "- %s" part)
                     (->> chapters
                          (map (fn [chapter]
                                 (prn [chapter (chapter->title chapter)])
                                 (format "\n  - [%s](noj_book.%s.html)\n"
                                         (chapter->title chapter)
                                         chapter)))))))
     (string/join "\n")
     md)
