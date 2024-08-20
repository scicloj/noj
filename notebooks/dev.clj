(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(clay/make! {:format [:quarto :html]
             :base-source-path "notebooks"
             :source-path (->> "notebooks/chapters.edn"
                               slurp
                               clojure.edn/read-string
                               (map (fn [part]
                                      (-> part
                                          (update
                                           :chapters
                                           (partial map #(format "noj_book/%s.clj" %))))))
                               (cons "index.clj"))
             :base-target-path "docs"
             :book {:title "Scinojure Documentation"}
             :clean-up-target-dir true})
