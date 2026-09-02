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
(kind/hiccup
   [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}
    [:div {:class ["flex-fill" "w-100" "border" "rounded" "overflow-hidden" "p-3"]}
     [:h3 "Simple"]
     [:p "Think clearly about the problem by disentangling concerns, Clojure-style."]
     [:img {:src "Simple.jpeg"
            :style {:width "100%" :height "auto" :display "block"}}]]
    [:div {:class ["flex-fill" "w-100" "border" "rounded" "overflow-hidden" "p-3"]}
     [:h3 "Robust"]
     [:p "Rely on performant and battle-tested Java libraries."]
     [:img {:src "Java.png"
            :style {:width "100%" :height "auto" :display "block"}}]]
    [:div {:class ["flex-fill" "w-100" "border" "rounded" "overflow-hidden" "p-3"]}
     [:h3 "Versatile"]
     [:p "Seamlessly transition from development to deployment."]
     [:img {:src "Versatile.jpeg"
            :style {:width "100%" :height "auto" :display "block"}}]]])

;; Noj is an out-of-the-box Clojure library designed to streamline data science workflows for both newcomers and experienced users. Noj provides a tested and integrated collection of libraries that are known to work seamlessly together from day one, rather than requiring users to find, configure, and integrate multiple libraries separately. 
