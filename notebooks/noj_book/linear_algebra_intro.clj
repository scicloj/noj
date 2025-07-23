;; # Intro to Linear Algebra - DRAFT 🛠

;; In this tutorial, we introduce basic linear algebra concepts
;; and the way they can be computed using [Fastmath](https://github.com/generateme/fastmath).

;; ## What is it about?

;; Linear algebra focuses on *vectors* and certain kinds of transformations of them, called *linear transformations*.

;; In our context here, our vectors are *ordered collections of floating-point numbers*.

;; This is a concrete special case of a more absract and more general notion of elements of [vector spaces](https://en.wikipedia.org/wiki/Vector_space). As usual, abstraction can be useful for our reasoning. We recommend learning abouthe more general ideas of linear algebra. Probably, for Clojurians who appreciate simplicity and functional composition, those ideas can be attractive.

;; Linear transformations are transformations which are simple, in a certain sense which can be made precise. They are often useful when mixed and composed with nonlinear transformations, and of course, the Fastmath API offers both kinds, as we will see below.

;; Implementation-wise, vectors can be represented in many ways. Clojure's persistent vectors are one way (assuming they contain only numbers). It is a bit heavy in time and space, since all numbers are [boxed](https://en.wikipedia.org/wiki/Boxing_(computer_programming)) in objects. Java arrays are a more lightweight representation.

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

;; Define a utility variable for demonstration purposes: a Java double array of length 5.
(def java-double-array (double-array 5))

;; ## Linear Algebra Concepts with `fastmath.vector`
;; Below are examples and explanations of common operations using the `fastmath.vector` library.

;; ### What is a Linear Transformation?
;; A linear transformation is a mathematical function that maps vectors from one 
;; vector space to another (or to itself) while preserving the operations of vector
;; addition and scalar multiplication. In simpler terms, it is a transformation that maintains 
;; the structure of a vector space.

;; ### Vector Addition
;; Adding two vectors element-wise. This operation combines corresponding elements of two vectors.
(vec/add [1 9]
         [0 -3])

;; ### Scalar Multiplication
;; Multiplying each element of a vector by a scalar value. This operation scales the vector proportionally.
(vec/mult [1 9]
          1000)

;; ### Vector Subtraction
;; #### Case 1: Single vector passed to `vec/sub`
;; Negates the vector by multiplying each element by `-1.0`.
(vec/sub [10 5])

;; #### Case 2: Two vectors passed to `vec/sub`
;; Performs element-wise subtraction between the first and second vectors.
(vec/sub [10 5]
         [8 4])

;; ### Dot Product
;; The dot product of two vectors is a scalar that measures the similarity of their directions.
;; It is calculated as: `a · b = a₁b₁ + a₂b₂ + ... + aₙbₙ`.
(vec/dot [10 5]
         [8 4])

;; ### Converters
;; The following examples demonstrate various conversions between vector types and formats.

;; #### Convert a vector to a Java array of doubles `[D`.
(vec/vec->array [10 5])
(type (vec/vec->array [10 5]))

;; #### Convert a Java double array back to a Clojure sequence.
(identity java-double-array)
(type (vec/vec->seq java-double-array))

;; #### Convert a vector or Java array to an Apache Commons Math RealVector.
(type (vec/vec->RealVector [10 5]))
(identity java-double-array)
(type (vec/vec->RealVector java-double-array))

;; #### Convert a Clojure vector or Java array to a primitive vector `Vec`.
(vec/vec->Vec [10 5])
(type (vec/vec->Vec [10 5]))
(identity java-double-array)
(type (vec/vec->Vec java-double-array))

;; ### Specialized Operations
;; #### Transform elements from one vector to match the count of another.
;; This operation extracts the same number of elements from the second vector as there are in the first.
(vec/as-vec [10 2] [5 10 15])

;; #### Create a zero vector matching the size of the input vector.
(vec/as-vec [5 10 15])

;; ### Vector Magnitude
;; Calculates the magnitude (length) of a vector using the Pythagorean theorem.
(vec/mag [3 4])

;; ### Approximation
;; Rounds each value in the vector to the specified number of decimal places.
(vec/approx [math/PI])
(kindly/check = [3.14])
(vec/approx [math/PI math/PI math/PI] 5)

;; ### Equality Tolerance
;; #### Element-wise Equality with Tolerance
;; Checks if elements of two vectors are equal within a given absolute (and/or relative) tolerance.
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4))
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4) 0.001)
(vec/edelta-eq [math/PI] (vec/approx [math/PI] 4) 0.000001)

;; #### Vector Equality with Tolerance
;; Similar to `edelta-eq`, but compares entire vectors for equality with tolerance.
(vec/delta-eq [math/PI] (vec/approx [math/PI] 4))
(vec/delta-eq [math/PI] (vec/approx [math/PI] 4) 0.000001)

;; ### Normalization
;; Scales the vector to have a magnitude of 1, keeping its direction. 
;; This is often used to create unit vectors.
(vec/normalize [3 4])

;; ### Vector Projection
;; Projects one vector onto another. 
;; The result is the component of the first vector in the direction of the second vector.
(def first-real-vec
  (vec/vec->RealVector [3 4]))

(def second-real-vec
  (vec/vec->RealVector [1 0]))

(vec/project [3 4] [1 0])

(vec/project first-real-vec second-real-vec)

;; ### Vector Rotation
;; Rotates a 2D or 3D vector by a given angle/angles (in radians). Useful for geometric transformations.
(vec/rotate (vec/vec2 [1 2]) (/ math/PI 2))
(vec/rotate (vec/vec3 [1 2 3]) 0.5 0.5 0.5)

;; ### Linear Interpolation
;; Performs linear interpolation between two vectors based on a parameter `t` (0 ≤ t ≤ 1).
;; Great for smooth transitions between points.
(vec/lerp (vec/vec2 [1 1]) (vec/vec2 [4 4]) 0.5)

;; ### Cross Product
;; Computes the cross product of two vectors. 
;; The result is a vector perpendicular to both input vectors.
(vec/cross (vec/vec3 [1 0 0]) (vec/vec3 [0 1 0]))
(vec/cross (vec/vec2 [1 0]) (vec/vec2 [0 1]))

;; ### Angle Between
;; Calculates the angle (in radians) between two vectors. 
;; Useful for determining directional differences.
(vec/angle-between (vec/vec2 [1 0]) (vec/vec2 [0 1]))

;; ### Vector Distance
;; Computes the Euclidean distance between two vectors (points in space).
(vec/distances (vec/vec2 [1 1]) (vec/vec2 [4 5]))

;; ### Clamp
;; Restricts each component of a vector to lie within a specified range.
(vec/clamp (vec/vec3 [-10 3 7]) 0 5)

;; ### Absolute value
;; Computes the absolute value of each component of the vector.
(vec/abs [-10 -5 3])

;; ### Maximum value
;; Returns largest element value from vector.
(vec/mx (vec/vec3 [-10 3 7]))

;; ### Maximum value index
;; Returns largest element index from vector.
(vec/maxdim (vec/vec3 [-10 3 7]))

;; ### Minimum value
;; Returns smallest element value from vector.
(vec/mn (vec/vec3 [-10 3 7]))

;; ### Minimum value index
;; Returns smallest element index from vector.
(vec/mindim (vec/vec3 [-10 3 7]))

;; ### Vector Set Magnitude
;; Rescales a vector to have a specific magnitude without requiring 
;; normalization first.
(vec/set-mag (vec/vec2 [10 3]) 4)

;; ### Vector Limit Magnitude
;; Restricts the magnitude of a vector to a specified maximum value.
(vec/limit (vec/vec2 [3 4]) 2)

;; ### Distance Squared
;; Calculates the squared distance between two vectors
(vec/dist-sq [1 1] [4 5])
