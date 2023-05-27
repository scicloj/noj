(ns scicloj.noj.v1.vis.image
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.libs.buffered-image :as bimg]
   [tech.v3.tensor :as tensor]
   [tech.v3.datatype.functional :as fun])
  (:import
   javax.imageio.ImageIO
   java.awt.image.BufferedImage))

(defn tensor->image [tensor img-type]
  (let [shape (dtype/shape tensor)
        new-img (bimg/new-image (shape 0)
                                (shape 1)
                                img-type)]
    (dtype/copy! tensor
                 (tensor/ensure-tensor new-img))
    new-img))
