^:kindly/hide-code
(ns index
  (:require [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [clojure.string :as str]
            [clojure.string :as string]))

^:kindly/hide-code
(def md
  (comp kindly/hide-code kind/md))

(md "
# Preface

Noj (scinojure) is an opinionated way to use the emerging Clojure data stack.

It collects a few of the main dependencies together with functions allowing to conveniently use them together.

**Source:** [![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)

**Artifact:** [![(Clojars coordinates)](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)

**Status:** Some parts of the underlying libraries are stable. Some part of Noj are still experimental, and the API will change. These details should be clarified soon.

## Existing chapters in this book:
")

^:kindly/hide-code
(defn chapter->title [chapter]
  (or (some->> chapter
               (format "notebooks/%s.clj")
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
            (format "\n- [%s](%s.html)\n"
                    (chapter->title chapter)
                    chapter)))
     (string/join "\n")
     md)
