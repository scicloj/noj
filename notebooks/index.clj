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

**Artifact:** [![(Clojars coordinates)](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)

**Status:** Mos of the underlying libraries are stable. The experimental parts are marked as such.

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
     (map (fn [chapter]
            (prn [chapter (chapter->title chapter)])
            (format "\n- [%s](noj_book.%s.html)\n"
                    (chapter->title chapter)
                    chapter)))
     (string/join "\n")
     md)
