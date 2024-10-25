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

Noj gets you started with Clojure for data and science.

* You get a collection of good libraries out of the box
* .. and documentation that shows you how to use the different libraries together

The included libraries either use [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) directly as tabular data structure or provide high interoperability with it.

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/noj/](https://scicloj.github.io/noj/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)|
|Tests |![ci workflow](https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg)|
|License |[EPLv1.0](https://github.com/scicloj/noj/blob/main/LICENSE)|
|Status |Getting close to Beta stage.|
|Dev chat|[#noj-dev](https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev) at [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/)|
|User chat|[#data-science](https://clojurians.zulipchat.com/#narrow/stream/151924-data-science) at [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/)|

## Chapters of this book:
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
