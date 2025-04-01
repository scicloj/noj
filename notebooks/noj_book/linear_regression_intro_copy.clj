;; # Introduction to Linear Regression   

;; **last update:** 2024-12-29

;; In this tutorial, we introduce the fundamentals of [linear regression](https://en.wikipedia.org/wiki/Linear_regression),
;; guided by the
;; [In Depth: Linear Regression](https://jakevdp.github.io/PythonDataScienceHandbook/05.06-linear-regression.html)
;; chapter of the
;; [Python Data Science Handbook](https://jakevdp.github.io/PythonDataScienceHandbook/)
;; by Jake VanderPlas.

;; ## Setup

(ns noj-book.linear-regression-intro-copy
  (:require
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [tablecloth.column.api :as tcc]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.dataset.modelling :as ds-mod]
   [fastmath.ml.regression :as reg]
   [scicloj.kindly.v4.kind :as kind]
   [fastmath.random :as rand]
   [scicloj.tableplot.v1.plotly :as plotly]))

;; ## Simple Linear Regression

;; We begin with the classic straight-line model: for data points $(x, y)$,
;; we assume there is a linear relationship allowing us to predict $y$ as
;; $$y = ax + b.$$
;; In this formulation, $a$ is the slope and $b$ is the intercept,
;; the point where our line would cross the $y$ axis.
