;; # Linear transformations with `fastmath.matrix` - DRAFT 🛠

;; In this tutorial we discuss the notion of linear transformations
;; and show how they can be represented as matrices.

;; Specifically, we will use `fastmath.vector` for vectors
;; and `fastmath.matrix` for matrices.

;; ## Setup

(ns noj-book.fastmath-matrix-intro
  (:require [fastmath.vector :as vec]
            [fastmath.matrix :as mat]
            [fastmath.core :as math]))

;; ## Vectors

;; Recall that vectors are abstract entities that live in
;; [*vector spaces*](https://en.wikipedia.org/wiki/Vector_space)
;; (or *linear spaces*).
;;
;; We often represent vectors them as arrays of numbers. 
;; The `fastmath.vector` API supports a few datatypes of this kind.

(vec/->Vec2 3 4)

(vec/->Vec3 3 4 -1)

(vec/vec->RealVector
 [1 3 9 -3])

;; What we care about in linear algebra is mostly the structure of vector spaces,
;; which is defined by the operations of addition and multiplication by a "scalar"
;; (that is, a single number).
;; These operations are just functions which are assumed to satisfy certain axioms.

;; When we represent vectors as arrays of numbers, there is a standard way
;; to define such operations: element-by-element.

(vec/add (vec/->Vec2 3 1)
         (vec/->Vec2 2 -2))

(vec/mult (vec/->Vec2 3 1)
          5)

;; Sometimes, vector spaces are endowed by additional structure,
;; which is defined as additional functions over these spaces.

;; E.g., we may add a notion of magnitude of vectors, so called *norm*.
;; When we represent vectors as arrays of numbers, one standard way to define 
;; a norm is to compute the corresponding points from zero.

(vec/mag
 (vec/->Vec2 3 4))

;; (You may veriffy that 5 is the distance
;; between `(0,0)` to `(3,4)` in the plane
;; using Pythagoras theorem.)

;; Another additional operation that is a *scalar product* operation between vectors.
;; This is a function that takes two vectors and returns a scalar (a number).
;; When we represent vectors as arrays of numbers, one standard way to define it
;; is by multiplying element by element and summing up.
;; This is the so-called *dot-product*.

(vec/dot
 (vec/->Vec3 1 10 100)
 (vec/->Vec3 1 2 3))

;; ## Matrices as transformations

;; Matrices are arrays of numbers of rectangular shape: 

(mat/->Mat2x2 0 1 2 0)

(mat/rows->RealMatrix
 [[1 3 9]
  [8 -9 7]])

;; Conceptually, we think about matrices as functions,
;; also called *mappings* or *transformations* or *operators*,
;; between vector spaces.
;; As we'll see below, the act as a specific kind of functions,
;; which are *linear*.

;; The way to apply the matrix as a function to a vector
;; is called "multiplying the matrix by the vector".

;; The multiplication of a $k \times l$ matrix $M$ with 
;; an $l$-dimensional vector $v$ is an $k$-dimensional vector $Mv$.
;; Each element of $Mv$ a dot product (as defined above)
;; of the corresponding row of $M$ with $v$.

;; For example:

(mat/mulv (mat/rows->RealMatrix
           [[1 1 1]
            [1 0 -1]])
          (vec/vec->RealVector
           [10 20 30]))

;; You see, multiplying this 2x3 matrix by a vector of dimension 3
;; resulted in a vector of dimension 2. This matrix acts as follows:

;; * The first new element is the sum of three old elements.
;; * The second new element is the difference of the first and last old elements.

;; A square-shaped matrix can be seen as a transformation
;; from a vector space to itself.
;; For example, a 2x2 matrix takes vectors of dimension 2 to vectors of dimension 2.

(mat/mulv (mat/->Mat2x2
           1 1
           1 0)
          (vec/->Vec2
           10 20))

;; We still need to explain what linear transformations are, 
;; and how they can be implemented with matrices.

;; ## Special cases

;; ### Identity matrix
;; This matrix has ones on the main diagonal
;; and zeros elsewhere.
(mat/->Mat2x2 1 0 0 1)

;; Multiplying by this matrix does nothing
;; (as the identity function in Clojure!).

(mat/mulv
 (mat/->Mat2x2 1 0 0 1)
 (vec/->Vec2 3 4))

(identity
 (vec/->Vec2 3 4))

;; ### Permutation matrix
;; This is similar to the identity,
;; but the order of rows is changes.
(mat/->Mat2x2 0 1 1 0)

;; It acts by changing the order of 
;; coordinates.
(mat/mulv
 (mat/->Mat2x2 0 1 1 0)
 (vec/->Vec2 3 4))

;; ## Operations on matrices

;; `fastmath.vector` offers a rich collection of operations
;; that act on matrices themselves.

;; For example, transposition reverses the roles of columns and rows:

(mat/->Mat2x2 0 1 2 0)

(mat/transpose
 (mat/->Mat2x2 0 1 2 0))

;; ## Matrix multiplication

;; [Matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication) is one important operation:
(mat/mulm
 (mat/->Mat2x2 0 1 1 0)
 (mat/->Mat2x2 2 0 3 0))

;; The multiplication $MN$ of a $k \times l$ matrix $M$ with an $l \times m$ matrix $N$ is defined as
;; a $k \times m$ matrix. Each of its columns is the matrix-vector multiplication 
;; of $M$ by the corresponding column of $N$, seen as a vector.

;; Importantly, if we see matrices as transformations as suggested above,
;; then multiplication is just composition of functions.
;; At the moment, we will not try to explain why this is true.

;; ## What are linear transformations?

;; Given a matrix $M$, the function $v \mapsto M v$,
;; or in Clojure, `(fn [v] (mat/mul m v))`,
;; is of a special kind. It is $linear$.

;; Let us discuss what this means.

;; Given a set of vectors, their linear combinations are all the other vectors
;; one may get from them using addition and multiplication by scalar.

;; For example, `(3,4)` is a linear combination of `(1,0)` and `(0,1)`:
(vec/add (vec/mult (vec/->Vec2 1 0) 3)
         (vec/mult (vec/->Vec2 0 1) 4))

;; Linear transformations are
;; functions vectors->vectors
;; which respect linear combinations.

;; Example: 
(defn T [v]
  [(+ (* -1 (v 0))
      (* 9 (v 1)))
   (+ (* 2 (v 0))
      (* -5 (v 1)))])

(T [1 0])

(T [0 1])

(T
 (vec/add (vec/mult (vec/->Vec2 1 0) 3)
          (vec/mult (vec/->Vec2 0 1) 4)))

(vec/add (vec/mult (T (vec/->Vec2 1 0)) 3)
         (vec/mult (T (vec/->Vec2 0 1)) 4))


;; You see, it does not matter whether we apply T before or after taking a linear combination.
;; In other words, it respects the linear structure of our vector space.

;; Another example - rotating a vector by 90 degrees (or Pi/2 radians):
(defn R [v]
  (vec/rotate v (/ math/PI 2)))

(R
 (vec/add (vec/mult (vec/->Vec2 1 0) 3)
          (vec/mult (vec/->Vec2 0 1) 4)))

(vec/add (vec/mult (R (vec/->Vec2 1 0)) 3)
         (vec/mult (R (vec/->Vec2 0 1)) 4))

;; Another example - multiplying by a matrix:

(defn M [v]
  (mat/mulv
   (mat/->Mat2x2 -1 9 2 -5)
   v))

(M
 (vec/add (vec/mult (vec/->Vec2 1 0) 3)
          (vec/mult (vec/->Vec2 0 1) 4)))

(vec/add (vec/mult (M (vec/->Vec2 1 0)) 3)
         (vec/mult (M (vec/->Vec2 0 1)) 4))

;; Actually, note that by the definition of 
;; multiplication between matrices and vectors,
;; `T` and `M` are actually the same function.

;; ## The standard basis

;; The standard basis of the vector space of $n$-dimensional arrays of numbers
;; is the set of vectors which are all 0 except for one 1.

;; In dimension $n=3$, for example:
#{(vec/->Vec3 1 0 0)
  (vec/->Vec3 0 1 0)
  (vec/->Vec3 0 0 1)}

;; Every vector in these spaces can be expressed
;; as a linear combination of standard basis elements.

;; For example:
(vec/->Vec3 2 3 4)

(-> (vec/mult (vec/->Vec3 1 0 0) 2)
    (vec/add (vec/mult (vec/->Vec3 0 1 0) 3))
    (vec/add (vec/mult (vec/->Vec3 0 0 1) 4)))

;; The standard basis vectors
;; are linearly independent:
;; Each of them cannot be expressed
;; as a linear combination of the
;; others.

;; So:
;; Every vector can be expressed
;; (in a unique way!)
;; as a linear combination
;; of standard basis elements,
;; but we need all of them for that.

;; E.g., in dimension 3, we need all 3.

;; ## Representing linear transformations as matrices

;; We have already claimed that matrices act as linear transformations.

;; Actually, if we represent our vectors as arrays of numbers,
;; any linear transformation $T$ can be represented as a matrix.

;; The reason is that, given any vector, we can represent it
;; as a linear combination of standard basis elements,
;; so we can infer the way it is transformed by $T$,
;; if we look into what $T$ does to the standard basis elements.
;; But this information can be encoded in a matrix,
;; which acts the same way as $T$.



