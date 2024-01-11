(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(clay/make! {:format [:quarto :html]
             :base-source-path "notebooks"
             :source-path ["index.clj"
                           "datasets.clj"
                           "image.clj"
                           "python.clj"
                           "stats.clj"
                           "visualization.clj"]
             :base-target-path "docs"
             :book {:title "Noj Documentation"}})
