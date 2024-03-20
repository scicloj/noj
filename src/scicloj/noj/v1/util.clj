(ns scicloj.noj.v1.util
  (:require [clojure.string :as str]))

(defn concat-keywords [& kws]
  (->> kws
       (map name)
       (str/join "-")
       keyword))
