(ns intro.image
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype.functional :as fun]
            [scicloj.noj.v1.vis.image :as vis.image]))

;; ## Adapt Clerk to Kindly
(kind-clerk/setup!)

(-> (for [i (range 100)]
      (range 100))
    tensor/ensure-tensor
    (fun/* 400)
    (vis.image/tensor->image :ushort-gray))
