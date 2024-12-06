;; # Intro to Linear Algebra - DRAFT 🛠

;; In this tutorial, we introduce basic linear algebra concepts
;; and the way they can be computed using [Fastmath](https://github.com/generateme/fastmath).

;; ## What is it about?

;; Linear algebra focuses on *vectors* and certain kinds of transformations of them, called *linear transformations*.

;; In our context here, our vectors are *ordered collections of floating-point numbers*.

;; This is a concrete special case of a more absract and more general notion of elements of [vector spaces](https://en.wikipedia.org/wiki/Vector_space). As usual, abstraction can be useful for our reasoning. We recommend learning abouthe more general ideas of linear algebra. Probably, for Clojurians who appreciate simplicity and functional composition, those ideas can be attractive.

;; Linear transformations are transformations which are simple, in a certain sense which can be made precise. They are often useful when mixed and composed with nonlinear transformations, and of course, the Fastmath API offers both kinds, as we will see below.

;; Implementation-wise, vectors can be represented in many ways. Clojure's persistent vectors are one way (assuming they contain only numbers). It is a bit heavy in time and space, since all numbers are [boxed](https://en.wikipedia.org/wiki/Boxing_(computer_programming)) in objects. Java arrays are a more lightweight representatin.

;; ## Recommended reading

;; ### Applied

;; * [Introduction to Applied Linear Algebra – Vectors, Matrices, and Least Squares](https://web.stanford.edu/~boyd/vmls/)
;; by Stephen Boyd & Lieven Vandenberghe, Cambridge University Press, UK, 2018.

;; * [Numerical Linear Algebra for Programmers](https://aiprobook.com/numerical-linear-algebra-for-programmers/) - An Interactive Tutorial with GPU, CUDA, OpenCL, MKL, Java, and Clojure
;; by Dragan Djurich, 2019.

;; * [Mathematics for Machine Learning](https://mml-book.github.io/)
;; by Marc Peter Deisenroth, A. Aldo Faisal, and Cheng Soon, Cambridge University Press, 2020.

;; ### Abstract
;; * [Linear Algebra Done Right](https://linear.axler.net/) 4th  by Sheldon Axler, Springer, 2024.


;; ## Setup

(ns noj-book.linear-algebra-intro
  (:require
   [clojure.math :as math]
   [fastmath.vector :as vec]
   [scicloj.kindly.v4.api :as kindly]))

;; util vars
(def java-double-array (double-array 5))


;; ## What is a linear transformation?
;; (coming soon)


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

;; Returns smallest number element from vector
(vec/mn [5 10 15])

;; Returns largest number element from vector
(vec/mx [5 10 15])

;; Removes negative sign from vector elements and return positive element
(vec/abs [-5 -10 -15])

;; Does vector division by int or float.
(vec/div [5 10 15] 2)

;; Compares vector values x[i] ? y[i] and returns vector with min values.
(vec/emn [1 10 15] [2.5 5 20])

;; Compares vector values x[i] ? y[i] and returns vector with max values.
(vec/emx [1 10 15] [2.5 5 20])

;; TODO: Euclidean distance probably calculated with Pythagorean theorem
;; We can also use more dimensional vectors with it.. 
; (3, 4) *       <-- Point A
;        |\
;        | \
;        |  \       d(A, B) = 5
;        |   \
;        |    \
;        |     \
;        *------* (7, 1) <-- Point B
(vec/dist [3 4] [7 1])

;; Iterates over vectors and does element vise division.
(vec/ediv [5 10 15] [2 2 2])

;; Map over vector and apply function to each element and returns a same type..
;; In case that we do calculus on elements with float point number we get
;; vector with float poing elements eg: 0.5  
(vec/fmap [5 10 15] #(/ 2.0 %))

;; In case that we do calculus on elements with whole int we get
;; vector with division representation elements eg: 1/2  
;; TODO: Check if this is intended!
(vec/fmap (vec/vec->RealVector [5 10 15]) #(/ 2 %))
;; Calculates difference between same positioned elements and multiples
;; that difference by `t` in this example 5.
(vec/lerp [1 0 1] [1 1 1] 5)

(vec/lerp (vec/vec->RealVector [1 0 1]) (vec/vec->RealVector [1 1 1]) 5)
;; Multiples all elements in the vector.
(vec/prod [5 10 15])


; (vec/clamp)

















