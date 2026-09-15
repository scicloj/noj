^:kindly/hide-code
(ns index
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]))


^:kindly/hide-code
(kind/hiccup
 [:div
  {:class ["text-center"]}
  [:h1 {:class ["text-center"]} "A Clojure data science toolkit"]
  [:p "A tested and integrated collection of libraries that are known to work seamlessly together from day one."]
  [:button {:type "button" :class ["btn" "btn-primary me-2"]} "Get started"]
  [:button {:type "button" :class ["btn" "btn-primary me-2" ]} "Learn Noj"]])

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
   [:h1 "Why " [:span {:class "home-hero__title"} "Noj"] "?"]]
  (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
        (map feature-card features-data-row1))
  [:br]
  (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
       (map feature-card features-data-row2))])

^:kindly/hide-code
(def library-data
  [{:logo "Clay.png"
    :short-heading "Publish"
    :link "publishing"
    :image "Clay.png"
    :heading "Exploring and Publishing"
    :content [:p "Clay turns a namespace into a document. Whether a website, a pdf or other Quarto-supported documents, something."]}
   {:logo "Plotje.png"
    :short-heading "Visualize"
    :link "visualization"
    :image "Plotje.png"
    :heading "Data Visualization"
    :content [:p "Generate clear and compelling data visualizations."]}
   {:logo "TMD.png"
    :short-heading "HPC"
    :link "hpc"
    :image "TMD.png"
    :heading "High Performance Computing "
    :content [:p "Efficiently transform and prepare data for analysis."]}
   {:logo "TMD.png"
    :short-heading "Process"
    :link "datasets"
    :heading "Data Processing"
    :content [:p "Efficiently transform and prepare data for analysis."]}
   {:logo "Fastmath.png"
    :short-heading "Stats"
    :link "math"
    :image "Fastmath.png"
    :heading "Mathematics and Statistics"
    :content [:p "Perform comprehensive mathematical and statistical operations."]}
   {:logo "Metamorph.ml.png"
    :short-heading "ML"
    :link "ml"
    :image "Matemorph.ml.png"
    :heading "Machine Learning"
    :content [:p "Access powerful machine learning tools and models."]}
   {:logo "Libpython_clj.png"
    :short-heading "Interop"
    :link "interop"
    :image "Libpython_clj.png"
    :heading "Bridges and Interop"
    :content [:p "Connect and interact with other language systems and libraries"]}])

^:kindly/hide-code
(defn library-active-tab [{:keys [short-heading logo link]}]
  [:li {:class "nav-item" :role "presentation"}
   [:a {:class "nav-link active" :data-bs-toggle "tab" :href (str "#" link) :aria-selected "true" :role "tab"} [:img {:src logo :style {:width "1em" :height "auto"}}] " " [:b short-heading]]])

^:kindly/hide-code
(defn library-tab [{:keys [short-heading logo link]}]
  [:li {:class "nav-item" :role "presentation"}
   [:a {:class "nav-link" :data-bs-toggle "tab" :href (str "#" link) :aria-selected "true" :role "tab"} [:img {:src logo :style {:width "1em" :height "auto"}}] " " [:b short-heading]]])

^:kindly/hide-code
(defn library-tab-active-content [{:keys [heading content link image]}]
  [:div {:class "tab-pane fade show active" :id link :role "tabpanel"}
   [:div {:class "row d-flex flex-wrap"}
    [:div {:class "col-6 w-50"}
     [:h4 heading]
     content]
    [:div {:class "col-6 w-50"}
     [:img {:src image}]]]])

^:kindly/hide-code
(defn library-tab-content [{:keys [heading content link image]}]
  [:div {:class "tab-pane fade" :id link :role "tabpanel"}
   [:div {:class "row d-flex flex-wrap"}
    [:div {:class "col-6 w-50"}
     [:h4 heading]
     content]
    [:div {:class "col-6 w-50"}
     [:img {:src image}]]]])

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
         (map library-tab-content (rest library-data))))
  [:h4 {:class "text-center"} "Also check out the other " [:a "recommended libraries"] "."]
  [:br]
  ])

^:kindly/hide-code
(kind/hiccup
 [:div {:class "full-width-bg"}
  [:div
   {:class "text-center"}
   [:h1 "Community"]]
  [:div {:class "row d-flex gap-2"}
   [:div {:class "col-3 w-25"}
    [:p  [:img {:src "SciCloj.png" :style {:width "1.2em" :height "auto"}}] " Noj was compiled and developed by the " [:a {:href "https://scicloj.github.io/"} "SciCloj"] " community. Join us!"]]
   [:div {:class "col-3 w-25"}
    [:p "Check our " [:a  {:href "https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev"} "developer chat"] " on Zulip."]]
   [:div {:class "col-3 w-25"}
    [:p "For any support, write us on " [:a {:href "https://clojurians.zulipchat.com/#narrow/stream/151924-data-science"} " the dedicated Zulip channel"] "."]]
   [:div {:class "col-3 w-25"}
    [:p "Check out the " [:a {:href "https://github.com/scicloj/noj"} "source code"] "."]]
   ]])
