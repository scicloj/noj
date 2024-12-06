(ns noj-book.tokyo-railways
  [:require
   [clojure.data.json :as json]
   [scicloj.kindly.v4.kind :as kind]])

(def raw-tokyo-railways
  (-> (slurp "data/tokyo-railways/tokyo-railways.json")
      (json/read-str {:key-fn keyword})
      (get :elements)))

(def default-style
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

(def default-layout
  {:name "preset" :padding 5})

;; see description at https://github.com/scicloj/kindly/blob/main/src/scicloj/kindly/v4/kind.cljc
(kind/cytoscape {:elements raw-tokyo-railways
                 :style default-style
                 :layout default-layout})
