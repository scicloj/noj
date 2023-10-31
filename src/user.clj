(ns user)

;; ## Useful commands

(comment
  (require '[scicloj.clay.v2.api :as clay])
  ;; Start Clay.
  (clay/start!))

(comment
  ;; Show the whole document:
  (clay/show-doc! "notebooks/index.clj"))

(comment
  ;; Show the document with table-of-contents, and write it as html:
  (clay/show-doc-and-write-html! "notebooks/index.clj"
                                 {:toc? true}))

(comment
  ;; Browse the Clay view (in case you closed the browser tab opened by `clay/start!`)
  (clay/browse!))



(comment
  ;; Update book
  (require '[scicloj.clay.v2.api :as clay])


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
                           :highlight-style :solarized}}}))
