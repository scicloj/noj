^:kindly/hide-code
(ns index
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]))


^:kindly/hide-code
(kind/hiccup
 [:div
  {:class ["text-center"]}
  #_[:h1
   {:class ["display-4" "fw-bold" "align-items-center" "gap-3"]}
   [:span {:class "home-hero__title"} "Noj"]
   [:small " is..."]
   #_[:sup
    [:img {:src "Noj.png"
           :style {:height "1.2em"
                   :width "auto"
                   :margin-left "0.15em"}}]]]
  [:h1 {:class ["text-center"]} "A Clojure data science toolkit"]
  [:p "A tested and integrated collection of libraries that are known to work seamlessly together from day one."]
  [:button {:type "button" :class ["btn" "btn-primary"]} "Get started"]
  [:button {:type "button" :class ["btn" "btn-primary"]} "Learn Noj"]])

^:kindly/hide-code
(defn feature-card [{:keys [heading text image]}]
  [:div {:class ["flex-fill" "w-100" "overflow-hidden" "p-2"]}
   [:div {:style {:display "flex" :align-items "center" :gap "12px"}}
    [:h4 heading]]
    [:p text]])

^:kindly/hide-code
(def features-data-row1
  [{:heading "Simple"
    :text "With Clojure, you can separate concerns."}
   {:heading "Easy deployment"
    :text "Use the same code in development and production."}
   {:heading "Fast"
    :text "The speed of Java and tech.ml.dataset."}])

^:kindly/hide-code
(def features-data-row2
  [{:heading "Maintainable"
    :text "With the backward compatibility of Clojure."}
   {:heading "REPL"
    :text "An unparalleled interactivity and in a quick feedback loop."}
   {:heading "Syntax uniformity"
    :text ""}])

^:kindly/hide-code
(kind/hiccup [:br])

^:kindly/hide-code
(kind/hiccup
 [:div {:class "full-width-bg"}
  [:div
   {:class "text-center"}
   [:h1 "Why Noj?"]]
  (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
        (map feature-card features-data-row1))
  [:br]
  (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
       (map feature-card features-data-row2))])

^:kindly/hide-code
(def library-data
  [{:heading "Exploring and Publishing"
    :short-heading "Publish"
    :text "Publish data analysis notebooks in multiple formats."
    :image "Clay.png"
    :link "publishing"}
   {:heading "Data Visualization"
    :short-heading "Visualize"
    :text "Generate clear and compelling data visualizations."
    :image "Plotje.png"
    :link "visualization"}
   {:heading "High Performance Computing "
    :short-heading "HPC"
    :text "Efficiently transform and prepare data for analysis."
    :image "TMD.png"
    :link "hpc"}
   {:heading "Data Processing"
    :short-heading "Process"
    :text "Efficiently transform and prepare data for analysis."
    :image "TMD.png"
    :link "datasets"}
   {:heading "Mathematics and Statistics"
    :short-heading "Stats"
    :text "Perform comprehensive mathematical and statistical operations."
    :image "Fastmath.png"
    :link "math"}
   {:heading "Machine Learning"
    :short-heading "ML"
    :text "Access powerful machine learning tools and models."
    :image "Metamorph.ml.png"
    :link "ml"}
   {:heading "Bridges and Interop"
    :short-heading "Interop"
    :text "Connect and interact with other language systems and libraries"
    :image "Libpython_clj.png"
    :link "interop"}])

^:kindly/hide-code
(defn library-active-tab [{:keys [short-heading image link]}]
  [:li {:class "nav-item" :role "presentation"}
   [:a {:class "nav-link active" :data-bs-toggle "tab" :href (str "#" link) :aria-selected "true" :role "tab"} [:img {:src image :style {:width "1em" :height "auto"}}] " " [:b short-heading]]])

^:kindly/hide-code
(defn library-tab [{:keys [short-heading image link]}]
  [:li {:class "nav-item" :role "presentation"}
   [:a {:class "nav-link" :data-bs-toggle "tab" :href (str "#" link) :aria-selected "true" :role "tab"} [:img {:src image :style {:width "1em" :height "auto"}}] " " [:b short-heading]]])

^:kindly/hide-code
(defn library-tab-active-content [{:keys [heading text link]}]
  [:div {:class "tab-pane fade active show" :id link :role "tabpanel"}
   [:h4 heading]
   [:p text]])

^:kindly/hide-code
(defn library-tab-content [{:keys [heading text link]}]
  [:div {:class "tab-pane fade" :id link :role "tabpanel"}
   [:h4 heading]
   [:p text]])

^:kindly/hide-code
(kind/hiccup
 [:div
  [:h1 {:class "text-center"} "Libraries"]
  [:br]
  (into 
   [:ul {:class "nav nav-tabs" :role "tablist"}]
   (cons (library-active-tab (first library-data))
         (map library-tab (rest library-data))))
  (into
   [:div {:id "myTabContent" :class "tab-content"}]
   (cons (library-tab-active-content (first library-data))
         (map library-tab-content (rest library-data))))])

^:kindly/hide-code
(kind/hiccup
 [:div {:class "full-width-bg"}
  [:div
   {:class "text-center"}
   [:h1 "Community"]]])
