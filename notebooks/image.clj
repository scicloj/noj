(ns image
  (:require [tech.v3.tensor :as tensor]
            [tech.v3.datatype.functional :as fun]
            [scicloj.noj.v1.vis.image :as vis.image]))

;; ## Turning tensors into images

;; The `vis.image/tensor->image` function can help turning [dtype-next](https://github.com/cnuernber/dtype-next/issues/92)'s [tensors](https://cnuernber.github.io/dtype-next/dimensions-bytecode-gen.html) into Java [BufferedImage](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html) objects.
;;
;; Eventually, this function may find itself in dtype-next itself, see [Issue #92](https://github.com/cnuernber/dtype-next/issues/92) there.
;;
;; You may see this function in action in the [Clay & Noj demo: image processing](https://scicloj.github.io/clojure-data-scrapbook/projects/visual-tools/clay-cider-demo-20231217/index.html) at the [Clojure Data Scrapbook](https://scicloj.github.io/clojure-data-scrapbook/) and in [the related video](https://www.youtube.com/watch?v=fd4kjlws6Ts).

(-> (for [i (range 100)]
      (range 100))
    tensor/ensure-tensor
    (fun/* 400)
    (vis.image/tensor->image :ushort-gray))
