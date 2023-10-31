(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(clay/update-book!
 {:title "Noj Documentation"
  :toc true
  :base-source-path "notebooks"
  :base-target-path "clean-book"
  :chapter-source-paths ["index.md"
                         "intro/visualization.clj"
                         "intro/image.clj"
                         "intro/python.clj"
                         "intro/datasets.clj"]
  :page-config {:quarto {:format {:html {:theme :spacelab
                                         :monofont "Fira Code Medium"}}
                         :highlight-style :solarized}}})
