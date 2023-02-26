(ns user
  (:require [scicloj.kindly-default.v1.api :as kindly-default]))

;; Initialize Kindly's [default](https://github.com/scicloj/kindly-default).
(kindly-default/setup!)

;; ## Useful commands

(comment
  (require '[nextjournal.clerk :as clerk])
  ;; Start Clerk.
  (clerk/serve! {:browse? true}))

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
