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

Scinojure (\"Noj\") is an entry point to the Clojure stack for data & science.

It combines a few of the relevant Clojure libraries for data & science and documents common ways of using them together. The included libraries either use [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) directly as tabular data structure or provide high interoperability with it.

**Source:** [![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)

**Deps:**

```clj
org.scicloj/noj {:git/url \"https://github.com/scicloj/noj.git\"
                 :git/tag \"2-alpha7\"
                 :git/sha \"ef8e323\"}
```

Note we are using git coordinates at the moment, in order to expose a few relevant features of the current underlying libraries, which are unreleased yet.

**Status:** Most of the [underlying libraries](./noj_book.underlying_libraries.html) are stable. The experimental parts are marked as such. For some of the libraries, we use a branch for an upcoming release.
The main current goal is to provide a clear picture of the direction the stack is going towards, expecting most of it to stabilize soon.

**Near term plan - till the end of October 2024**

* Work on stabilizing the upcoming releases of the underlying libraries.

* Keep documenting core ideas of the underlying librares and ways to combine them in typical workflows.

* Keep making the docs generate automatic tests using [kindly/check](https://scicloj.github.io/clay/#test-generation).

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
