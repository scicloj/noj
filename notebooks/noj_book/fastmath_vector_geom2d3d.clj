;; # 2d and 3d geometry with `fastmath.vector` - DRAFT 🛠

;; authors: Nedeljko Radovanovic, Epidiah Ravachol, Daniel Slutsky

(ns noj-book.fastmath-vector-geom2d3d
  (:require [fastmath.vector :as vec]
            [clojure.math :as math]
            [emmy.mafs :as mafs]
            [scicloj.kindly.v4.kind :as kind]))

;; ## a few operations

;; ### rounding

(-> (vec/vec2 1/3 2/3)
    (vec/approx 5))

;; ### addition

(vec/add (vec/vec2 -2 -1)
         (vec/vec2 1 4))

;; ### rotation

;; Pi radians = 180 degress
(-> (vec/vec2 1 0)
    (vec/rotate math/PI))

(-> (vec/vec2 1 0)
    (vec/rotate math/PI)
    (vec/approx 5))

;; rotating by Pi ratians (90 degrees)
;; around the z axis:
(-> (vec/vec3 1 0 1)
    (vec/rotate 0 0 (/ math/PI 2))
    (vec/approx 5))

;; ## visualizing 2d addition
;; (the parallelogram rule)

(mafs/mafs
 {:viewBox {:x [-5 5 ]
            :y [-5 5]}}
 (mafs/cartesian)
 (mafs/vector [-2 -1] {:color :blue})
 (mafs/vector [1 4] {:color :blue})
 (mafs/vector (vec
               (vec/add (vec/vec2 -2 -1)
                        (vec/vec2 1 4)))
              {:color :red}))


;; ## visualizing 2d rotation

(mafs/mafs
 {:viewBox {:x [-5 5 ]
            :y [-5 5]}}
 (mafs/cartesian)
 (mafs/vector (vec (vec/vec2 -2 -1))
              {:color :blue})
 (mafs/vector (vec (vec/rotate
                    (vec/vec2 -2 -1)
                    (/ math/PI 2)))
              {:color :red}))

;; ## visualizing 3d rotation

(defn vec3d->plotly-coords [v]
  {:x [0 (v 0)]
   :y [0 (v 1)]
   :z [0 (v 2)]
   :type :scatter3d
   :mode :lines+markers
   :line {:width 10}
   :marker {:size 4}})


(let [orig-v (vec/vec3 1 0 0)]
  (kind/plotly
   {:data [(vec3d->plotly-coords orig-v)
           (vec3d->plotly-coords (vec/rotate orig-v
                                             0
                                             (/ math/PI 10)
                                             0))]}))


(def random-shape
  (repeatedly 6
              (fn []
                (vec/vec3 (+ 1 (rand))
                          (+ 2 (rand))
                          0))))

(defn shape->plotly-coords [shape]
  {:x (mapv #(% 0) shape)
   :y (mapv #(% 1) shape)
   :z (mapv #(% 2) shape)
   :type :scatter3d
   :mode :markers+lines
   :line {:width 10}
   :marker {:size 4}})


(kind/plotly
 {:data [(shape->plotly-coords random-shape)
         (shape->plotly-coords
          (map #(vec/rotate %
                            0
                            0
                            Math/PI)
               random-shape))
         (shape->plotly-coords [[0 0 0]])]})
