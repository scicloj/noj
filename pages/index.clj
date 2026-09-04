^:kindly/hide-code
(ns index
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]))

^:kindly/hide-code
(kind/hiccup
 [:div
  {:class ["text-center"]}
  [:h1
   {:class ["display-4" "fw-bold" "align-items-center" "gap-3" "text-center"]}
   [:span "Noj"]
   [:sup
    [:img {:src "Noj.png"
           :style {:height "1.2em"
                   :width "auto"
                   :margin-left "0.15em"}}]]]
  [:h1 [:i "The reliable data analysis stack in Clojure"]]])

^:kindly/hide-code
(def card-data
  [{:heading "Simple"
    :text "Think clearly about the problem by disentangling concerns, Clojure-style."
    :image "Simple.jpeg"}
   {:heading "Robust"
    :text "Rely on performant and battle-tested Java libraries."
    :image "Java.png"}
   {:heading "Versatile"
    :text "Seamlessly transition from development to deployment."
    :image "Versatile.jpeg"}])

^:kindly/hide-code
(defn feature-card [{:keys [heading text image]}]
  [:div {:class ["flex-fill" "w-100" "border" "rounded" "overflow-hidden" "p-2"]}
     [:h3 heading]
     [:p text]
     [:img {:src image
            :style {:width "100%" :height "auto" :display "block"}}]])
        
^:kindly/hide-code
(kind/hiccup
 (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
       (map feature-card card-data)))
 
;; Noj is an out-of-the-box Clojure library designed to streamline data science workflows for both newcomers and experienced users. Noj provides a tested and integrated collection of libraries that are known to work seamlessly together from day one, rather than requiring users to find, configure, and integrate multiple libraries separately. 
