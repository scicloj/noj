(ns gen-tests
  (:require
   [scicloj.clay.v2.api :as clay]
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn do-generate-tests []
  (->>
   (file-seq (io/file "notebooks/noj_book"))
   (filter #(str/ends-with? (.toPath %) ".clj"))
   (run!
    #(let [p (.getAbsolutePath %)]
       (println :generate-tests p)
       (clay/make! {:source-path p
                    :show false})))))

