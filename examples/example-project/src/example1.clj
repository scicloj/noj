(ns example1
  (:require [scicloj.clay.v1.api :as clay]
            [scicloj.clay.v1.tools :as tools]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kind :as kind]
            [scicloj.kindly.v2.kindness :as kindness]
            [scicloj.noj.v1.view.dataset]
            [scicloj.viz.api :as viz]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [tablecloth.api :as tc]
            [clojure.string :as string])
  (:import javax.imageio.ImageIO
           java.net.URL))


(clay/start! {:tools [tools/clerk
                      #_tools/portal]})

(+ 1 2)

(defonce clay-image
  (-> "https://upload.wikimedia.org/wikipedia/commons/2/2c/Clay-ss-2005.jpg"
      (URL.)
      (ImageIO/read)))

clay-image

(delay
  (Thread/sleep 500)
  (+ 1 2))

(-> 2
    (+ 3)
    (clay/check = 4))

(-> 2
    (+ 3)
    (clay/check = 5))

(-> [:small "hello"]
    (kindly/consider kind/hiccup))

(def vega-lite-spec
  (memoize
   (fn [n]
     (-> {:data {:values
                 (->> (repeatedly n #(- (rand) 0.5))
                      (reductions +)
                      (map-indexed (fn [x y]
                                     {:w (rand-int 9)
                                      :z (rand-int 9)
                                      :x x
                                      :y y})))},
          :mark "point"
          :encoding
          {:size {:field "w" :type "quantitative"}
           :x {:field "x", :type "quantitative"},
           :y {:field "y", :type "quantitative"},
           :fill {:field "z", :type "nominal"}}}
         (kindly/consider kind/vega)))))


(-> (->> [10 100 1000]
         (map (fn [n]
                [:div {:style {:width "400px"}}
                 [:h1 (str "n=" n)]
                 (vega-lite-spec n)]))
         (into [:div]))
    (kindly/consider kind/hiccup))


(-> {:x (range 99)
     :y (repeatedly 99 rand)}
    (tc/dataset))



(-> {:x (range 99)
     :y (repeatedly 99 rand)}
    tc/dataset
    viz/data
    (viz/viz {:viz/type ht/point-chart}
             {:MSIZE 200}))
