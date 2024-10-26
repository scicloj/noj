;; # Intro to Linear Algebra - DRAFT 🛠

;; In this tutorial, we introduce basic linear algebra concepts
;; and the way they can be computed using [Fastmath](https://github.com/generateme/fastmath).

;; For additional backgrounds, see
;; [Introduction to Applied Linear Algebra – Vectors, Matrices, and Least Squares](https://web.stanford.edu/~boyd/vmls/)
;; by Stephen Boyd & Lieven Vandenberghe, Cambridge University Press, UK, 2018.
(ns noj-book.linear-algebra-intro
  (:require
   [clojure.math :as math]
   [fastmath.vector :as vec]
   [scicloj.kindly.v4.api :as kindly]))

;; util vars

(def java-double-array (double-array 5))

;; ## Addition

(vec/add [1 9]
         [0 -3])

;; ## Scalar multiplication

(vec/mult [1 9]
          1000)

;; ## Subtraction
;; When we pass single vector to fastmath/sub function it will be multiplied by `-1.0`.

(vec/sub [10 5])

;; When we pass two vectors to fastmath/sub function it will perform basic vector subtraction.

(vec/sub [10 5]
         [8 4])

;; ## Dot product
;; When we pass two vectors to fastmath/dot function it will perform dot product operation on them.
;; `a · b = a₁b₁ + a₂b₂ + … + anbn`
(vec/dot [10 5]
         [8 4])

;; ## Converters
;; ### Vector to Java array of doubles `[D`. 

(vec/vec->array [10 5])

(type (vec/vec->array [10 5]))

;; ### Java array to Clojure sequence. 
(identity java-double-array)

(type (vec/vec->seq java-double-array))

;; ### Vector or Java array to Apache Commons Math RealVector. 
(type (vec/vec->RealVector [10 5]))

(identity java-double-array)

(type (vec/vec->RealVector java-double-array))

;; ### Clojure vector or Java array to primitive vector `Vec`. 

(vec/vec->Vec [10 5])

(type (vec/vec->Vec [10 5]))

(identity java-double-array)

(type (vec/vec->Vec java-double-array))

;; ### WIP -- if two vectors are passed it takes count of elemets from first vec and
;; returns same count of elements from second vec??
(vec/as-vec [10 2] [5 10 15])

;; ### WIP -- if one vector is passed it takes count of elemets from vec and returns same 
;; count of elemets from new vector with each value being `0,0`. 
(vec/as-vec [5 10 15])

;; Magnitude of the vector calculated using Pythagoras(norm).  
(vec/mag [3 4])

;; Round the value from the vector based on second argument
(vec/approx [math/PI])
(kindly/check = [3.14])
(vec/approx [math/PI math/PI math/PI] 5)

;; Equality tolerance
;; "Element-wise equality with given absolute (and/or relative) tolerance."
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4))
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4) 0.001)
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4) 0.000001) 
;; Equality with given absolute (and/or relative) tolerance.
(vec/delta-eq [math/PI] (vec/approx [math/PI] 4))
(vec/delta-eq [math/PI] (vec/approx [math/PI] 4) 0.000001)

