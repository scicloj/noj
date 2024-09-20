(ns plotnine)

(require '[libpython-clj2.python :as py]
         '[libpython-clj2.require :refer [require-python]])

(require-python '[plotnine :as])
(require-python '[numpy :as np])

(py/from-import plotnine ggplot, geom_point, aes, stat_smooth, facet_wrap)
(py/from-import plotnine.data  mtcars)

(ggplot mtcars (aes "wt" "mpg" :color "factor(gear)"))
(ggplot mtcars (aes "wt" "mpg" :color "factor(gear)"))
