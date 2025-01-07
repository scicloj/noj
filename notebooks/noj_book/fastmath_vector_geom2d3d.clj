;; # 2d and 3d geometry with fastmath.vector - DRAFT 🛠

;; authors: Nedeljko Radovanovic, Epidiah Ravachol, Daniel Slutsky

;; ## Setup
(ns noj-book.fastmath-vector-geom2d3d
  (:require [fastmath.vector :as vec]
            [clojure.math :as math]
            [emmy.mafs :as mafs]
            [scicloj.kindly.v4.kind :as kind]))

;; ## 2d and 3d examples with visualization using fastmath

;; ### rounding
;; Rounding a vector to a specified number of decimal places (5 decimals here).
;; This is useful when you need to display vectors in a human-readable form with controlled precision.
(-> (vec/vec2 1/3 2/3)
    (vec/approx 5))

;; ### addition
;; Adding two vectors together. This is the basic operation in vector math, often used in physics, 
;; for example, to add forces or velocities.
(vec/add (vec/vec2 -2 -1)
         (vec/vec2 1 4))

;; ## Visualize 2D Addition
(mafs/mafs
 {:viewBox {:x [-5 5]
            :y [-5 5]}}
 (mafs/cartesian)
 (mafs/vector [-2 -1] {:color :blue})
 (mafs/vector [1 4] {:color :blue})
 (mafs/vector (vec
               (vec/add (vec/vec2 -2 -1)
                        (vec/vec2 1 4)))
              {:color :red}))

;; ### rotation
;; Rotating a 2D vector by an angle, specified in radians.
;; Pi radians = 180 degrees, so rotating (1, 0) by Pi radians (or 180 degrees) results in (-1, 0).
(-> (vec/vec2 1 0)
    (vec/rotate math/PI))

;; Approximation of the rotated vector, to match the precision required.
(-> (vec/vec2 1 0)
    (vec/rotate math/PI)
    (vec/approx 5))

;; ## Visualize 2D Rotation
(mafs/mafs
 {:viewBox {:x [-5 5]
            :y [-5 5]}}
 (mafs/cartesian)
 (mafs/vector (vec (vec/vec2 -2 -1))
              {:color :blue})
 (mafs/vector (vec (vec/rotate
                    (vec/vec2 -2 -1)
                    (/ math/PI 2)))
              {:color :red}))

;; ## Real World Use Cases:
;; - In physics, vector addition is used to calculate the net force on an object.
;; - In computer graphics, rotation is used to manipulate objects or camera views.

;; ## visualizing 3d rotation
;; Visualizing 3D vector rotations 
;; This is useful in graphics engines, physics simulations, or robotics.

(defn vec3d->plotly-coords [v]
  {:x [0 (v 0)]
   :y [0 (v 1)]
   :z [0 (v 2)]
   :type :scatter3d
   :mode :lines+markers
   :line {:width 10}
   :marker {:size 4}})

;; Visualize a 3D vector rotation around the Y-axis by Pi/10 radians.
(let [orig-v (vec/vec3 1 0 0)]
  (kind/plotly
   {:data [(vec3d->plotly-coords orig-v)
           (vec3d->plotly-coords (vec/rotate orig-v
                                             0
                                             (/ math/PI 10)
                                             0))]}))

;; ## Real World Use Cases:
;; - In robotics, 3D rotations are used to simulate the movement of arms or tools.
;; - In gaming, rotations are essential for character and camera movements in 3D space.

(def random-shape
  (repeatedly 6
              (fn []
                (vec/vec3 (+ 1 (rand))  ;; Random x-coordinates
                          (+ 2 (rand))  ;; Random y-coordinates
                          0))))  ;; Fixed z-coordinate

(defn shape->plotly-coords [shape]
  {:x (mapv #(% 0) shape)
   :y (mapv #(% 1) shape)
   :z (mapv #(% 2) shape)
   :type :scatter3d
   :mode :markers+lines
   :line {:width 10}
   :marker {:size 4}})

;; Visualizing random 3D shapes and their rotations.
(kind/plotly
 {:data [(shape->plotly-coords random-shape)  ;; Original random shape
         (shape->plotly-coords
          (map #(vec/rotate %
                            0
                            0
                            Math/PI)
               random-shape))
         (shape->plotly-coords [[0 0 0]])]})

;; ## Real World Use Cases:
;; - In architecture, 3D models of buildings or structures are manipulated using rotations.
;; - In data science, transformations like rotations are used to adjust feature spaces in machine learning.
;; - In simulations, random shapes might represent particles or objects in a system that undergo transformations.

;; Returning the random shape for reference
random-shape

;; ## More Operations

;; ### Scaling
;; Scaling a vector by a scalar value. This is often used in computer graphics and physics to 
;; resize vectors, for example, to scale the size of objects or control the speed of moving objects.
(def scaling-original (vec/vec2 1 2))
(def scaling-result (vec/mult scaling-original 3))

;; Visualize scaling operation
(defn vec2d->plotly-coords [v]
  {:x [0 (v 0)]
   :y [0 (v 1)]
   :type :scatter
   :mode :lines+markers
   :line {:width 5}
   :marker {:size 10}})

(kind/plotly
 {:data [(vec2d->plotly-coords scaling-original)
         (vec2d->plotly-coords scaling-result)]})

;; ### Dot Product
;; Visualizing the dot product as the projection of one vector onto another.
;; In real-world physics, this can represent the amount of work done by a force (force * displacement).
(def dot-product-a (vec/vec2 1 2))
(def dot-product-b (vec/vec2 4 5))
(def dot-product-projection (vec/project dot-product-a dot-product-b))

;; Visualize dot product projection
(kind/plotly
 {:data [(vec2d->plotly-coords dot-product-a)
         (vec2d->plotly-coords dot-product-b)
         (vec2d->plotly-coords dot-product-projection)]})

;; ### Cross Product
;; Visualizing the cross product in 3D space. The cross product results in a vector that is perpendicular to both input vectors.
;; ## Real World Use Cases:
;; - In physics, the cross product is used to calculate torque and angular momentum.
;; - In graphics, it is used to compute surface normals for lighting calculations.
(kind/plotly
 {:data [(vec3d->plotly-coords (vec/vec3 1 2 3))
         (vec3d->plotly-coords (vec/vec3 4 5 6))
         (vec3d->plotly-coords (vec/cross (vec/vec3 1 2 3) (vec/vec3 4 5 6)))]})

;; ### Projection
;; Visualizing the projection of vector a onto vector b. This is common in computer graphics and physics simulations.
;; ## Real World Use Cases:
;; - Projections are used in physics to resolve forces into components.
;; - In computer graphics, projections are used for perspective transformations and shadow computations.
(kind/plotly
 {:data [(vec3d->plotly-coords (vec/vec3 1 2 3))
         (vec3d->plotly-coords (vec/vec3 4 5 6))
         (vec3d->plotly-coords (vec/project (vec/vec3 1 2 3) (vec/vec3 4 5 6)))]})

;; ### Distance Between Two Vectors
;; Visualizing the distance between two vectors as the length of the difference vector between them.
;; ## Real World Use Cases:
;; - Distance calculations are critical in collision detection in gaming and simulations.
;; - They are also used in clustering algorithms in data science and machine learning.
(def dist-a (vec/vec3 1 2 3))
(def dist-b (vec/vec3 4 5 6))
(def dist-result (vec/dist dist-a dist-b))

;; Visualize the distance between two vectors
(kind/plotly
 {:data [(vec3d->plotly-coords dist-a)
         (vec3d->plotly-coords dist-b)]
  :layout {:title (str "Distance: " dist-result)}})

;; ### Normalization
;; Normalizing a vector to a unit vector. This is useful when you need a direction but not the magnitude.

;; Visualizing vector normalization
;; Shows the original vector and its normalized version.
;; ## Real World Use Cases:
;; - Normalization is used in graphics for shading and lighting calculations.
;; - In simulations, unit vectors are often used to indicate directions without affecting magnitude.
(kind/plotly
 {:data [(vec2d->plotly-coords (vec/vec2 3 4))
         (vec2d->plotly-coords (vec/normalize (vec/vec2 3 4)))]})

;; ### Linear Interpolation (Lerp)
;; Interpolating between two vectors. This can be used for animations or blending transformations.
;; ## Real World Use Cases:
;; - Used in animations, physics simulations (e.g., easing between positions), and graphics (e.g., color interpolation).
(def lerp-start (vec/vec2 0 0))
(def lerp-end (vec/vec2 10 10))
(def lerp-result (vec/lerp lerp-start lerp-end 0.5))  ;; Halfway interpolation
(kind/plotly
 {:data [(vec2d->plotly-coords lerp-start)
         (vec2d->plotly-coords lerp-end)
         (vec2d->plotly-coords lerp-result)]})

;; ### Vector Composition (Scaling + Rotation + Translation) on 3D vector
;; Apply a sequence of transformations to a 3D vector: scale, rotate, then translate.
;; ## Real World Use Cases:
;; - Composition is used in 3D modeling and animation pipelines for transformations.
;; - In simulations, such as flight dynamics, transformations simulate real-world movement.

(def original-vector (vec/vec3 1 1 1))

;; Scaling the vector by a factor of 2
(def scaled-vector (vec/mult original-vector 2))

;; Rotating the scaled vector by Pi/4 radians (45 degrees) around the Z-axis
(def rotated-vector (vec/rotate scaled-vector 0 0 math/PI))

;; Translating the rotated vector by adding (3, 3, 3)
(def translated-vector (vec/add rotated-vector (vec/vec3 3 3 3)))

;; Visualizing the sequence of transformations: scaling, rotation, and translation
(kind/plotly
 {:data [(vec3d->plotly-coords original-vector)
         (vec3d->plotly-coords scaled-vector)
         (vec3d->plotly-coords rotated-vector)
         (vec3d->plotly-coords translated-vector)
         (vec3d->plotly-coords translated-vector)]})
