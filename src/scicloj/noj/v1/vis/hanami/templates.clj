(ns scicloj.noj.v1.vis.hanami.templates
  (:require [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]))

(swap! hc/_defaults
       assoc
       :X2 com.rpl.specter/NONE
       :Y2 com.rpl.specter/NONE
       :X2TYPE (fn [ctx]
                 (if (-> ctx :X2 (not= com.rpl.specter/NONE))
                   (:XTYPE ctx)
                   com.rpl.specter/NONE))
       :Y2TYPE (fn [ctx]
                 (if (-> ctx :Y2 (not= com.rpl.specter/NONE))
                   (:YTYPE ctx)
                   com.rpl.specter/NONE))
       :X2ENCODING (fn [ctx]
                     (if (-> ctx :X2 (not= com.rpl.specter/NONE))
                       (-> ht/xy-encoding
                           :x
                           (assoc :field :X2
                                  :type :X2TYPE))
                       com.rpl.specter/NONE))
       :Y2ENCODING (fn [ctx]
                     (if (-> ctx :Y2 (not= com.rpl.specter/NONE))
                       (-> ht/xy-encoding
                           :y
                           (assoc :field :Y2
                                  :type :Y2TYPE))
                       com.rpl.specter/NONE))
       :ENCODING (merge
                  ht/xy-encoding
                  {:x2 :X2ENCODING
                   :y2 :Y2ENCODING}))

(def boxplot-chart
  (assoc ht/view-base
         :mark (merge ht/mark-base {:type "boxplot"})))

(def rule-chart
  (assoc ht/view-base
         :mark "rule"))

(def rect-chart
  (assoc ht/view-base
         :mark "rect"))
