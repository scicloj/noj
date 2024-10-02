;; # Intro to Linear Algebra - DRAFT 🛠

;; In this tutorial, we introduce basic linear algebra concepts
;; and the way they can be computed using [Fastmath](https://github.com/generateme/fastmath).

;; For additional backgrounds, see
;; [Introduction to Applied Linear Algebra – Vectors, Matrices, and Least Squares](https://web.stanford.edu/~boyd/vmls/)
;; by Stephen Boyd & Lieven Vandenberghe, Cambridge University Press, UK, 2018.

(ns noj-book.linear-algebra-intro
  (:require [fastmath.vector :as vec]))

;; ## Addition

(vec/add [1 9]
         [0 -3])

;; ## Scalar multiplication

(vec/mult [1 9]
          1000)
