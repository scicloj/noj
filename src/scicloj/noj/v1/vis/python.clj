(ns scicloj.noj.v1.vis.python
  (:require [tech.v3.dataset :as tmd]
            [aerial.hanami.common :as hc]
            [scicloj.noj.v1.paths :as paths]
            [scicloj.tempfiles.api :as tempfiles]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [tech.v3.datatype :as dtype]
            [scicloj.kindly.v4.kind :as kind]))

;; inspiration: http://gigasquidsoftware.com/blog/2020/01/18/parens-for-pyplot/

(require-python 'matplotlib.pyplot
                'matplotlib.backends.backend_agg
                'numpy)

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
         kind/html)))


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
        kind/html)))
