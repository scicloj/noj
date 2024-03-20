(ns scicloj.noj.v1.vis.hanami.templates
  (:require [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]))

(hc/update-defaults
 {:X2 hc/RMV
  :Y2 hc/RMV
  :X2TYPE (fn [ctx]
            (if (-> ctx :X2 (not= hc/RMV))
              (:XTYPE ctx)
              hc/RMV))
  :Y2TYPE (fn [ctx]
            (if (-> ctx :Y2 (not= hc/RMV))
              (:YTYPE ctx)
              hc/RMV))
  :X2ENCODING (fn [ctx]
                (if (-> ctx :X2 (not= hc/RMV))
                  (-> ht/xy-encoding
                      :x
                      (assoc :field :X2
                             :type :X2TYPE))
                  hc/RMV))
  :Y2ENCODING (fn [ctx]
                (if (-> ctx :Y2 (not= hc/RMV))
                  (-> ht/xy-encoding
                      :y
                      (assoc :field :Y2
                             :type :Y2TYPE))
                  hc/RMV))
  :ENCODING (merge
             ht/xy-encoding
             {:x2 :X2ENCODING
              :y2 :Y2ENCODING})})

(def boxplot-chart
  (assoc ht/view-base
         :mark (merge ht/mark-base {:type "boxplot"})))

(def rule-chart
  (assoc ht/view-base
         :mark "rule"))

(def rect-chart
  (assoc ht/view-base
         :mark "rect"))

(def boxplot-layer
  {:mark (assoc ht/mark-base :type "boxplot")
   :selection :SELECTION
   :transform :TRANSFORM
   :encoding :ENCODING})
