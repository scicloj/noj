;; # linear transformations with `fastmath.matrix` - DRAFT 🛠

(ns noj-book.fastmath-matrix-intro
  (:require [fastmath.vector :as vec]
            [fastmath.matrix :as mat]
            [clojure.math :as math]))

(vec/mag [3 4])

(vec/mag
 (vec/->Vec2 3 4))
(mat/->Mat2x2 0 1 2 0)

(mat/transpose
 (mat/->Mat2x2 0 1 2 0))

;; Matrices are operators.
;; They operate on vectors
;; by matrix multiplication.



;; Identity matrix
(mat/->Mat2x2 1 0 0 1)

;; does nothing
;; (as the identity function in Clojure!)
(mat/mulv
 (mat/->Mat2x2 1 0 0 1)
 (vec/->Vec2 3 4))

(identity
 (vec/->Vec2 3 4))


;; Permutation matrix
;; (similar to the identity,
;; just switching the order of rows)
(mat/->Mat2x2 0 1 1 0)

;; acts by changing the order of 
;; coordinates
(mat/mulv
 (mat/->Mat2x2 0 1 1 0)
 (vec/->Vec2 3 4))

;; Every matrix `m`
;; defines a function
;; vector->vector:
;; (fn [v]
;;  (mat/mulv m v))


;; The standard basis.
(vec/->Vec2 1 0)
(vec/->Vec2 0 1)

(vec/->Vec3 1 0 0)
(vec/->Vec3 0 1 0)
(vec/->Vec3 0 0 1)

;; What is linear combination?
;; Everything we can do
;; by adding vectors
;; and multiplying them by numers.

(vec/mult (vec/->Vec3 1 0 0)
          4)

(vec/add
 (vec/->Vec3 1 0 0)
 (vec/->Vec3 0 1 0))

(vec/add
 (vec/mult (vec/->Vec3 1 0 0)
           4)
 (vec/->Vec3 0 1 0))

;; Every vector can be expressed
;; as a linear combination
;; of standard basis elements.

;; For example:
(vec/->Vec3 2 3 4)

(-> (vec/mult [1 0 0] 2)
    (vec/add (vec/mult [0 1 0] 3))
    (vec/add (vec/mult [0 0 1] 4)))

;; The standard basis vectors
;; are linearly independent.
;; Each of them cannot be expressed
;; as a linear combination of the
;; others.

;; So:
;; Every vector can be expressed
;; as a linear combination
;; of standard basis elements,
;; but we need all of them for that.

;; In dimension 3,
;; we need all 3 of them:
[1 0 0]
[0 1 0]
[0 0 1]


;; What is linear transformation?

;; Linear transformations are
;; functions vectors->vectors
;; which respect linear combinations.

;; Example: 
(defn T [v]
  [(* 9 (v 1))
   (* -2 (v 0))])

(T [1 0])
(T [0 1])


(T [3 4])

(T
 (vec/add (vec/mult [1 0] 3)
          (vec/mult [0 1] 4)))

(vec/add (vec/mult (T [1 0]) 3)
         (vec/mult (T [0 1]) 4))

;; T respects linear combinations.


(defn R [v]
  (vec/rotate (vec/->Vec2 (v 0) (v 1)) (/ math/PI 2)))

(R [3 4])

(R
 (vec/add (vec/mult [1 0] 3)
          (vec/mult [0 1] 4)))

(vec/add (vec/mult (R [1 0]) 3)
         (vec/mult (R [0 1]) 4))



(defn M [v]
  (mat/mulv
   (mat/->Mat2x2 0 1 1 0)
   (vec/->Vec2 (v 0) (v 1))))

(M [3 4])

(M
 (vec/add (vec/mult [1 0] 3)
          (vec/mult [0 1] 4)))

(vec/add (vec/mult (M [1 0]) 3)
         (vec/mult (M [0 1]) 4))


;; Multiplying vectors by matrices is a linear transformation.

;; All linear transformations can be defined this way.

;; The reason is that linear transformations are defined
;; by what they do to the standard basis.



