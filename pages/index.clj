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
  [:a {:href "quick_start_guide"} [:button {:type "button" :class ["btn" "btn-primary me-2"]} "Get started"]]
  [:a {:href "learn/"} [:button {:type "button" :class ["btn" "btn-primary me-2" ]} "Learn Noj"]]])

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
   {:heading "Fast"
    :text "The speed of Java and tech.ml.dataset."}
   {:heading "Maintainable"
    :text "With the backward compatibility of Clojure."}])

^:kindly/hide-code
(def features-data-row2
  [{:heading "Deploy easily"
    :text "Use the same code in development and production."}
   {:heading "REPL"
    :text "An unparalleled interactivity and in a quick feedback loop."}
   {:heading "Uniform syntax"
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
    :content [:div
              [:p [:a {:href "ref/clay"} "Clay"] " turns a namespace into a document, whether a website, a pdf or any other Quarto-supported document. It provides the notebook-oriented workflow familiar from Jupyter."]
              [:p [:a {:href "ref/kindly"} "Kindly"] " supports Clay by specifying how forms are presented in the document. With over 30 different supported kinds, it is ready to populate your website with dynamic content."]]}
   {:logo "Plotje.png"
    :short-heading "Visualize"
    :link "visualization"
    :image "Plotje.png"
    :heading "Data Visualization"
    :content [:p [:a {:href "ref/plotje"} "Plotje"] " generates clear and compelling data visualizations. Inspired by the " [:a {:href "https://en.wikipedia.org/wiki/Wilkinson%27s_Grammar_of_Graphics"} "Grammer of Graphics"] " and Julia's " [:a {:href "aog.makie.org"} "Algebra of Graphics"] ", it enables the easy specification of complex plots in just a few lines of code."]}
   {:logo "TMD.png"
    :short-heading "HPC"
    :link "hpc"
    :image "TMD.png"
    :heading "High Performance Computing "
    :content [:div
              [:p [:a {:href "ref/tech_ml_dataset"} "Tech.ml.dataset"] " is a high-performance dataset processing library providing a fast, Clojure-native alternative to R and Python's data frames."]
              [:p [:a {:href "ref/ham_fisted"} "Ham-fisted"] " provides high-performance data structures and operations for Clojure."]
              [:p [:a {:href "ref/dtype_next"} "Dtype-next"] " is a high-performance numeric array programming library. It provides a unified pathway for dealing with contiguous containers of primitive datatypes such as ints and floats on the JVM."]]}
   {:logo "TMD.png"
    :short-heading "Process"
    :link "datasets"
    :heading "Data Processing"
    :content [:div
              [:p "With " [:a {:href "ref/tablecloth"} "tablecloth"] ", you can manipulate datasets with the ergonomics of R's dplyr and the speed of " [:a {:href "ref/tech_ml_dataset"} "tech.ml.dataset"] ". "]
              [:p [:a {:href "ref/tmd_parquet"} "TMD Parquet"] " provides parquet file format bindings bindings for tech.ml.dataset."]
              [:p [:a {:href "ref/clojure_java_time"} "clojure.java-time"] " is a Java 8 Date-Time API wrapper for Clojure."]]}
   {:logo "Fastmath.png"
    :short-heading "Stats"
    :link "math"
    :image "Fastmath.png"
    :heading "Mathematics and Statistics"
    :content [:div
              [:p [:a {:href "ref/fastmath"} "Fastmath"] " is a comprehensive math and statistics library, which includes trigonometric and power functions, vector and matrix operations, procedures for integration and differentiation, distributions, statistical functions, and many more."]
              [:p [:a {:href "ref/fitdistr"} "Fitdistr"] " provides an intuitive interface for fitting distributions, inspired by the famous R package."]
              [:p [:a {:href "ref/ish"} "Same-ish"] " provides approximate numerical comparisons, which are useful for testing notebooks."]]}
   {:logo "Metamorph.ml.png"
    :short-heading "ML"
    :link "ml"
    :image "Metamorph.ml.png"
    :heading "Machine Learning"
    :content [:div
              [:p [:a {:href "ref/metamorph_ml"} "Metamorph.ml"] " is a platform for unified machine learning pipelines. It unifies hyperparameter tuning and data preprosessing in a single approach."]
              [:p [:a {:href "ref/scicloj_ml_tribuo"} "Scicloj.ml.tribuo"] " is an integration of the established Java ML library " [:a {:href "https://tribuo.org/"} "Tribuo"] " into the metamorph framework."]
              [:p [:a {:href "ref/sklearn_clj"} "Sklearn-clj"] " provides easy access to all models and estimators from " [:a {:href "https://scikit-learn.org/"} "scikit-learn"] " in Clojure."]]}
   {:logo "Libpython_clj.png"
    :short-heading "Interop"
    :link "interop"
    :image "Libpython_clj.png"
    :heading "Bridges and Interop"
    :content [:div
              [:p [:a {:href "ref/clojisr"} "ClojisR"] " is a Clojure bridge to R. With it, you can execute any R code within Clojure."]
              [:p [:a {:href "ref/libpython_clj"} "Libpython_clj"] " is a deep integration between Python and Clojure. Python and Java objects are bridged and handled by the JVM GC."]
              [:p [:a {:href "ref/kind_pyplot"} "Kind-pyplot"] " is a small Clojure library for displaying Python plots."]]}])

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
    #_[:div {:class "col-6 w-50"}
       [:img {:src image}]]]])

^:kindly/hide-code
(defn library-tab-content [{:keys [heading content link image]}]
  [:div {:class "tab-pane fade" :id link :role "tabpanel"}
   [:div {:class "row d-flex flex-wrap"}
    [:div {:class "col-6 w-50"}
     [:h4 heading]
     content]
    #_[:div {:class "col-6 w-50"}
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
  [:p {:class "text-center"} "Also check out the other " [:a "recommended libraries"] "."]
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
    [:p [:img {:src "zulip.png" :style {:width "1.5em" :height "auto"}}] " Check our " [:a  {:href "https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev"} "developer chat"] "."]]
   [:div {:class "col-3 w-25"}
    [:p [:img {:src "github.png" :style {:width "1.2em" :height "auto"}}] " Check out the " [:a {:href "https://github.com/scicloj/noj"} "source code"] "."]]
   [:div {:class "col-3 w-25"}
    [:p [:img {:src "zulip.png" :style {:width "1.5em" :height "auto"}}] " For any support, write us on the " [:a {:href "https://clojurians.zulipchat.com/#narrow/stream/151924-data-science"} " dedicated channel"] "."]]]])
