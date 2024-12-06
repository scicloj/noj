;; # Data Visualization with Cytoscape - DRAFT

;; author: Vincent Choo


;; ## Setup
(ns noj-book.tokyo-railways
  [:require
   [clojure.data.json :as json]
   [scicloj.kindly.v4.kind :as kind]])

;; ## Quick Start
;; If you already have existing Cytoscape.js data from a previous project, i.e.
;; [Tokyo railways](https://js.cytoscape.org/demos/tokyo-railways/),
;; you can use it with Kindly's Cytoscape like so:\
;; (The [source data](https://github.com/cytoscape/cytoscape.js/tree/a4de13e0c1668436273c82f90613c6e5911f3f32/documentation/demos/tokyo-railways)
;; has been modified slightly to keep this example simple.)

(defn slurp-json [f]
  (-> (slurp f)
      (json/read-str {:key-fn keyword})))

;; TODO
;; - is there a way to parse CSS here?
;; - should I keep the original name & data, or use modified versions?
(kind/cytoscape {:elements (slurp-json "data/tokyo-railways/elements.json")
                 :style (slurp "data/tokyo-railways/cy_style.css")
                 :layout {:name "preset"}}
                {:style {:background-color "#000"}})

;; **Note:**\
;; While Kindly's Cytoscape does not require you to specify a `container` HTML
;; element, it *does* require you to convert JSON's string keys to Clojure
;; keywords before using options such as `:elements`, `:style`, or `:layout`.

;; [More options](#more-options) are also available.

;; ## Cytoscape Tutorial
;; If this is your first time using Cytoscape, make sure you are familiar with
;; [graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)) concepts
;; such as nodes and edges.
;; If you intend to customize your graph's appearance, you will also need to know
;; some [CSS](https://en.wikipedia.org/wiki/CSS).

;; You can use Cytoscape to create a simple interactive graph:

(def simple-elements
  {:nodes [{:data {:id "a"}
            :position {:x 0 :y 0}}
           {:data {:id "b"}
            :position {:x 100 :y 200}}]
   :edges [{:data {:id "ab" :source "a" :target "b"}}]})

(def simple-style
  [{:selector "node"
    :css {:content "data(id)"
          :text-valign "center"
          :text-halign "center"}}
   {:selector "parent"
    :css {:text-valign "top"
          :text-halign "center"}}
   {:selector "edge"
    :css {:curve-style "bezier"
          :target-arrow-shape "triangle"}}])

(def simple-layout
  {:name "preset" :padding 5})

(kind/cytoscape {:elements simple-elements
                 :style simple-style
                 :layout simple-layout})

;; When you generate a Cytoscape graph, you usually need to set the `:elements`,
;; `:style` and `:layout` options.

;; In this example, the `:elements` option, `simple-elements`, specifies that
;; there are two nodes, **a** and **b**, and one edge, **ab**.
;; For now, don't worry about what the `:style` and `:layout` options do.
;; Cytoscape just needs them to be set to something, and their details are
;; discussed in later sections.

;; ### Elements
;; In Cytoscape, an **element** is either a node or an edge.
;; So, when specifying `:elements`, you can either choose to mix nodes and edges:

;; TODO
;; - how should I format two equivalent definitions?
;;   Ideally, I'd like to place them inside a kind/cytoscape without running it
;; - can infer an edge because source & target options are present

(def mixed-elements
  [{:group "nodes"
    :data {:id "a"}
    :position {:x 0 :y 0}}
   {:group "nodes"
    :data {:id "b"}
    :position {:x 100 :y 200}}
   {:group "edges"
    :data {:id "ab" :source "a" :target "b"}}])

;; Or you can choose to keep them separate, keying them by group:

(def separate-elements
  {:nodes [{:data {:id "a"}
            :position {:x 0 :y 0}}
           {:data {:id "b"}
            :position {:x 100 :y 200}}]
   :edges [{:data {:id "ab" :source "a" :target "b"}}]})

;; **Note:**\
;; All elements must specify a string `:id` under `:data`.

;; TODO not using `:position`

;; ### Style
;; Cytoscape styles follow [CSS](https://en.wikipedia.org/wiki/CSS) conventions
;; as closely as possible.

;; TODO do example where we specify style with a function

;; ### Layout

;; ### More Options
;; A complete list of Cytoscape options is here:
;; - TODO https://js.cytoscape.org/#core/initialisation are all options
;;   available? If so, do other options use camelCase or kebab-case?
