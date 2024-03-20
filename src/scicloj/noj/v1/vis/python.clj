(ns scicloj.noj.v1.vis.python
  (:require [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.tempfiles.api :as tempfiles]))

;; inspiration: http://gigasquidsoftware.com/blog/2020/01/18/parens-for-pyplot/

(require-python 'matplotlib.pyplot
                'matplotlib.backends.backend_agg
                'numpy)

(defn html-in-reagent [html]
  (-> [:div
       {:dangerouslySetInnerHTML
        {:__html html}}]
      kind/reagent))

(defmacro with-pyplot
  "Takes forms with mathplotlib.pyplot and returns a showable (SVG) plot."
  [& body]
  `(let [_# (matplotlib.pyplot/clf)
         fig# (matplotlib.pyplot/figure)
         agg-canvas# (matplotlib.backends.backend_agg/FigureCanvasAgg fig#)
         path# (:path (tempfiles/tempfile! ".svg"))]
     ~(cons 'do body)
     (py. agg-canvas# "draw")
     (matplotlib.pyplot/savefig path#)
     (-> path#
         slurp
         html-in-reagent)))


(defn pyplot
  "Takes a function plotting using mathplotlib.pyplot, and returns a showable (SVG) plot"
  [plotting-function]
  (let [_ (matplotlib.pyplot/clf)
        fig (matplotlib.pyplot/figure)
        agg-canvas (matplotlib.backends.backend_agg/FigureCanvasAgg fig)
        path (:path (tempfiles/tempfile! ".svg"))]
    (plotting-function)
    (py. agg-canvas "draw")
    (matplotlib.pyplot/savefig path)
    (-> path
        slurp
        html-in-reagent)))
