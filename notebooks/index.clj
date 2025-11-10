^:kindly/hide-code
(ns index
  (:require [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [clojure.string :as str])
  (:import (java.util.regex Pattern)))

^:kindly/hide-code
(def md
  (comp kindly/hide-code kind/md))

^:kindly/hide-code
(defn content->remove-from-marker-str
  ([content-str marker-str]
   (content->remove-from-marker-str content-str marker-str nil))
  ([content-str start-marker-str end-marker-str]
   (let [pattern (re-pattern (str "(?s)"
                                  (Pattern/quote start-marker-str)
                                  (cond-> ".*" end-marker-str (str "?"))
                                  (when end-marker-str
                                    (str "(?=" (Pattern/quote end-marker-str) ")"))))]
     (str/replace content-str pattern ""))))

^:kindly/hide-code
(-> (slurp "README.md")
    (content->remove-from-marker-str "## Getting Started with Noj" "## Learning Resources")
    (content->remove-from-marker-str "## License")
    (md))

;; ## Chapters of this book

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
                                 #_(prn [chapter (chapter->title chapter)])
                                 (format "\n  - [%s](noj_book.%s.html)\n"
                                         (chapter->title chapter)
                                         chapter)))))))
     (str/join "\n")
     md)
