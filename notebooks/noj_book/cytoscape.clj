;; # Data Visualization with Cytoscape - DRAFT

;; author: Vincent Choo

(ns noj-book.cytoscape
  [:require
   [clojure.data.json :as json]
   [scicloj.kindly.v4.kind :as kind]])

;; [Cytoscape](https://cytoscape.org/) is a...
;; [Cytoscape.js](https://js.cytoscape.org/) is a...

;; Under the hood, Clay uses Cytoscape.js, so all documentation applies.

;; ## Quick Start
;; Suppose you already have existing Cytoscape.js
;; [data](https://github.com/cytoscape/cytoscape.js/tree/a4de13e0c1668436273c82f90613c6e5911f3f32/documentation/demos/tokyo-railways)
;; from a previous project, i.e.
;; [Tokyo railways](https://js.cytoscape.org/demos/tokyo-railways/).

;; Once you have prepared the data...

(def tokyo-railways-elements
  (-> (slurp "data/cytoscape/tokyo-railways.json") ; read data into memory as a string
      (json/read-str) ; convert data to JSON
      (get "elements")) ; get only the value keyed to "elements"
  )

(def tokyo-railways-style
  (slurp "data/cytoscape/tokyo-railways.cycss") ; Cytoscape can accept its style as a string
  )

(def tokyo-railways-layout
  {:name "preset"} ; keys can either be strings or Clojure keywords
  )

(def tokyo-railways-kindly-options
  {:style {:background-color "#000"}} ; Kindly does not accept its style option as a string
  )

;; ...you can annotate it with
;; [Kindly](https://scicloj.github.io/kindly-noted/kindly.html)
;; and be visualized with a tool like
;; [Clay](https://scicloj.github.io/clay).

(kind/cytoscape {:elements tokyo-railways-elements
                 :style tokyo-railways-style
                 :layout tokyo-railways-layout}
                tokyo-railways-kindly-options)

;; ;; ℹ️ **Note:**\
;; Unlike Cytoscape.js, Clay will automatically set the
;; [`container`](https://js.cytoscape.org/#getting-started/initialisation)
;; HTML element for you.


;; ## Cytoscape Tutorial
;; If this is your first time using Cytoscape, make sure you are familiar with
;; [graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
;; concepts such as nodes and edges.

;; ### Basic Graphs
;; When generating Cytoscape graphs, you will usually need to set the
;; `:elements`, `:style`, and `:layout` options:

(def simple-elements
  {:nodes [{:data {:id "a"}
            :position {:x 0 :y 0}}
           {:data {:id "b"}
            :position {:x 100 :y 200}}]
   :edges [{:data {:id "ab" :source "a" :target "b"}}]})

(def simple-style
  [{:selector "node"
    :css {:label "data(id)"}}])

(def simple-layout
  {:name "preset"})

(kind/cytoscape {:elements simple-elements
                 :style simple-style
                 :layout simple-layout})

;; In this example, the `:elements` option, `simple-elements`, specifies that
;; there are two nodes, **a** and **b**, and one edge, **ab**.
;; (For now, don't worry about what the `:style` and `:layout` options do.
;; Cytoscape just needs them to be set to something, and their details are
;; discussed in later sections.)

;; ℹ️ **Note:**\
;; All elements must specify a string `:id` in its `:data`.
;; Edges must have a `:source` and `:target`, and both must be a node's `:id`.

;; ### Directed Graphs
;; Although `simple-elements` has directed edges, they are displayed as
;; undirected. Modifying the style allows them to be displayed as directed.

(def directed-edges-style
  [{:selector "node"
    :css {:content "data(id)"
          :text-valign "center"
          :text-halign "center"}}
   {:selector "edge"
    :css {:target-arrow-shape "triangle"}}])

(kind/cytoscape {:elements simple-elements
                 :style directed-edges-style
                 :layout simple-layout})

;; And by applying styles to individual elements, it is possible to create mixed
;; graphs:

;; In general, by adjusting the nodes, edges, and styles, Cytoscape can support
;; a variety of other use-cases as well, such as loops, multigraphs, and
;; disconnected graphs.

;; ### Compound Graphs
;; It is also possible to generate compound graphs:

;; ### Styling
;; Cytoscape styles follow
;; [CSS](https://en.wikipedia.org/wiki/CSS)
;; conventions as closely as possible.

;; ### Layout
; TODO
; - some layouts let you ignore using `:position`


;; ### More Options
;; A complete list of Cytoscape initialization options is here:
; - TODO https://js.cytoscape.org/#core/initialisation are all options
;   available? If so, do other options use camelCase or kebab-case?
;; This is different than the other options available.

; TODO examples
; - other
;   - calling JS?
;     - cy.png
;     - clustering
;     - centrality
;     - camelCase

;; ### Elements
;; #### Alternate Format
;; In Cytoscape, an **element** can be either a node or an edge.
;; So while `:elements` can be a map with keyed by group, as with
;; `simple-elements`, `:elements` can also be a list of nodes and edges:

(def mixed-elements
  [{:group "nodes" ; the element is a node when :group is "nodes"
    :data {:id "a"}
    :position {:x 0 :y 0}}
   {:group "nodes"
    :data {:id "b"}
    :position {:x 100 :y 200}}
   {:group "edges" ; but the element is an edge when :group is "edges"
    :data {:id "ab" :source "a" :target "b"}}])

(comment ; commenting the expression out to avoid rendering it
  (kind/cytoscape {:elements mixed-elements
                   :style simple-style
                   :layout simple-layout}))
