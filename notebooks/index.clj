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

This book is its WIP documentation.



")

(->> ["datasets"
      "image"
      "python"
      "stats"
      "visualization"]
     (map (fn [k]
            (format "\n- [%s](%s.html)\n"
                    k k)))
     (string/join "\n")
     md)
