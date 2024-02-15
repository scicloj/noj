(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(clay/make! {:format [:quarto :html]
             :base-source-path "notebooks"
             :source-path ["index.clj"
                           "image.clj"
                           "python.clj"
                           "stats.clj"
                           "visualization.clj"
                           "prepare_for_ml.clj"]
             :base-target-path "docs"
             :book {:title "Noj Documentation"}
             :clean-up-target-dir true})

6
