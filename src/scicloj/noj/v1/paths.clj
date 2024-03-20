(ns scicloj.noj.v1.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn url? [path]
  (->> path
       (re-matches #"^http.*")))

(defn file-type [path]
  (-> path
      (string/split #"\.")
      last))

(defn exists? [path]
  (-> path
      io/file
      (.exists)))

(defn throw-if-not-exists! [path]
  (if (exists? path)
    path
    (throw (ex-info "File does not exist"
                    {:path path}))))
