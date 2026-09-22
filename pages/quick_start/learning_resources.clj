^:kindly/hide-code
(ns quick-start.learning_resources
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
    (content->remove-from-marker-str "# Noj - a data science toolkit" "## Learning Resources")
    (content->remove-from-marker-str "## License")
    (md))
