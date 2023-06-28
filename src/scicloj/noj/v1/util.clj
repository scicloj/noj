(ns scicloj.noj.v1.util
  (:require [clojure.string :as string]))

(defn concat-keywords [& kws]
  (->> kws
       (map name)
       (string/join "-")
       keyword))
