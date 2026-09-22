^:kindly/hide-code
(ns index
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]))

^:kindly/hide-code
(kind/hiccup
 [:div
  {:class ["text-center"]}
  [:h1 {:class ["text-center"]} "A Clojure Data Science Toolkit"]
  [:p "A tested and integrated collection of libraries that are known to work seamlessly together from day one."] 
  [:button {:type "button" :class ["btn" "btn-primary me-2"] :onclick "window.location.href=quick_start_guide"} "Quick Start"]
  [:button {:type "button" :class ["btn" "btn-primary me-2"] :onclick "window.location.href=learn/"} "Learn Data Science"]
  [:br]
  [:br]
  [:img {:src "https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg" :style {:height "1.2em" :vertical-align "middle"} :class "me-3" :alt "CI workflow"}]
  [:a {:href "https://clojars.org/org.scicloj/noj"} [:img {:src "https://img.shields.io/clojars/v/org.scicloj/noj.svg" :style {:height "1.2em" :vertical-align "middle"} :class "me-3" :alt "clojars"}]]])

^:kindly/hide-code
(defn feature-card [{:keys [heading text image]}]
  [:div {:class ["flex-fill" "w-100" "overflow-hidden" "p-2"]}
   [:div {:style {:display "flex" :align-items "center" :gap "12px"}}
    [:h3 heading]]
    [:p text]]) ;; try making the font more visible 

^:kindly/hide-code
(def features-data-row1
  [{:heading "Fast"
    :text "The speed of Java matched with fast dataset processing and low-overhead parallelism."}
   {:heading "Maintainable"
    :text "No breaking changes, but accretion."}
   {:heading "Simple"
    :text "Reason easily about code by separating tangled concepts."}])

^:kindly/hide-code
(def features-data-row2
  [{:heading "Rich Ecosystem"
    :text "A complete data analysis ecosystem and direct access to mature Java libraries."}
   {:heading "Easy Deployment"
    :text "Use the same workflow in research and transition seamlessly to production."}
   {:heading "REPL"
    :text "A seamless feedback loop and runtime data manipulation."}])

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
(def library-category-data
  [{:category :publish
    :short-heading "Publishing"
    :link "publishing"
    :image ""
    :heading "Exploring and Publishing"}
   {:category :visualize
    :short-heading "Visualization"
    :link "visualization"
    :image ""
    :heading "Data Visualization"}
   {:category :hpc
    :short-heading "HPC"
    :link "hpc"
    :image ""
    :heading "High Performance Computing "}
   {:category :data-processing
    :short-heading "Data Processing"
    :link "datasets"
    :image ""
    :heading "Data Processing"}
   {:category :math-stats
    :short-heading "Math & Stats"
    :link "math"
    :image ""
    :heading "Mathematics and Statistics"}
   {:category :ml
    :short-heading "ML"
    :link "ml"
    :image ""
    :heading "Machine Learning"}
   {:category :interop
    :short-heading "Interop"
    :link "interop"
    :image ""
    :heading "Bridges and Interop"}])

^:kindly/hide-code
(def library-data 
  [{:library "Clay"
    :category :publish
    :href "ref/clay"
    :logo "clay.png"
    :description "Clay publishes a namespace, whether as a website, a pdf or any other Quarto-supported document. It provides the notebook-oriented workflow familiar from Jupyter."}
   {:library "Kindly"
    :category :publish
    :href "ref/kindly"
    :logo "kindly.png"
    :description "Kindly supports Clay by specifying how forms are presented in the document. With over 30 different supported kinds, it is ready to populate your website with dynamic content."}
   {:library "Plotje"
    :category :visualize
    :href "ref/plotje"
    :logo "plotje.png"
    :description "plotje generates clear and compelling data visualizations. Inspired by the Grammer of Graphics and Julia's Algebra of Graphics, it enables the easy specification of complex plots in just a few lines of code."}
   {:library "Tech.ml.dataset"
    :category :hpc
    :href "ref/tech_ml_dataset"
    :logo "tech_ml_dataset.png"
    :description "tech.ml.dataset is a high-performance dataset processing library providing a fast, Clojure-native alternative to R and Python's data frames."}
   {:library "Ham-fisted"
    :category :hpc
    :href "ref/ham_fisted"
    :logo "SciCloj.png"
    :description "ham-fisted provides high-performance data structures and operations for Clojure."}
   {:library "Dtype-next"
    :category :hpc
    :href "ref/dtype_next"
    :logo "SciCloj.png"
    :description "dtype-next is a high-performance numeric array programming library. It provides a unified pathway for dealing with contiguous containers of primitive datatypes such as ints and floats on the JVM."}
   {:library "Tablecloth"
    :category :data-processing
    :href "ref/tabecloth"
    :logo "tablecloth.png"
    :description "With tablecloth, you can manipulate datasets with the ergonomics of R's dplyr and the speed of tech.ml.dataset"}
   {:library "Tech.parquet"
    :category :data-processing
    :href "ref/tech_parquet"
    :logo "tech.parquet.png"
    :description "tech.parquet provides parquet file format bindings bindings for tech.ml.dataset."}
   {:library "Clojure.java-time"
    :category :data-processing
    :href "ref/clojure_java_time"
    :logo "SciCloj.png"
    :description "clojure.java-time is a Java 8 Date-Time API wrapper for Clojure."}
   {:library "Fastmath"
    :category :math-stats
    :href "ref/fastmath"
    :logo "fastmath.png"
    :description "fastmath is a comprehensive math and statistics library, which includes trigonometric and power functions, vector and matrix operations, procedures for integration and differentiation, distributions, statistical functions, and many more."}
   {:library "Fitdistr"
    :category :math-stats
    :href "ref/fitdistr"
    :logo "fastmath.png"
    :description "fitdistr provides an intuitive interface for fitting distributions, inspired by the fitdistrplus R package."}
   {:library "Same-ish"
    :category :math-stats
    :href "ref/same_ish"
    :logo "same_ish.png"
    :description "same-ish provides approximate numerical comparisons, which are useful for testing notebooks."}
   {:library "Metamorph.ml"
    :category :ml
    :href "ref/metamorph_ml"
    :logo "metamorph_ml.png"
    :description "metamorph.ml is a platform for unified machine learning pipelines. It unifies hyperparameter tuning and data preprosessing in a single approach."}
   {:library "Scicloj.ml.tribuo"
    :category :ml
    :href "ref/scicloj_ml_tribuo"
    :logo "metamorph_ml.png"
    :description "metamorph.ml is an integration of the established Java ML library Tribuo into the metamorph framework."}
   {:library "Sklearn-clj"
    :category :ml
    :href "ref/sklearn_clj"
    :logo "metamorph_ml.png"
    :description "sklearn-clj provides easy access to all models and estimators from scikit-learn in Clojure."}
   {:library "Clojisr"
    :category :interop
    :href "ref/clojisr_clj"
    :logo "clojisr.png"
    :description "Clojisr is a Clojure bridge to R. With it, you can execute any R code within Clojure."}
   {:library "Libpython-clj"
    :category :interop
    :href "ref/kind_pyplot"
    :logo "libpython_clj.png"
    :description "libpython-clj is a deep integration between Python and Clojure. Python and Java objects are bridged and handled by the JVM GC."}
   {:library "Kind-pyplot"
    :category :interop
    :href "ref/kind_pyplot"
    :logo "kind_pyplot.png"
    :description "kind-pyplot is a small Clojure library for displaying Python plots."}])

^:kindly/hide-code
(defn analysis-step-tab [{:keys [short-heading link]} active?]
  (let [class (if active? "nav-link active" "nav-link")]
    [:li {:class "nav-item" :role "presentation"}
     [:a {:class class :data-bs-toggle "tab" :href (str "#" link) :aria-selected "true" :role "tab"}
      [:b short-heading]]]))

^:kindly/hide-code
(defn library-description [{:keys [library logo description]}]
  [:div
   [:h3 [:img {:src logo :style {:width "1.1em" :height "auto"}}] " " library]
   [:p description]])

^:kindly/hide-code
(defn analysis-step-tab-content [{:keys [category heading link image]} lib-data active?]
  (let [class (str "tab-pane fade" (if active? " show active" ""))
        content (into [:div]
                      (map library-description (filter #(= (:category %) category) lib-data)))]
  [:div {:class class :id link :role "tabpanel"}
   [:h3 {:class "text-center"} heading]
   [:div {:class "row d-flex flex-wrap"}
    [:div {:class "col-6 w-50"} content]
    #_[:div {:class "col-6 w-50"} [:img {:src image}]]]]))

^:kindly/hide-code
(kind/hiccup
 [:div
  [:h1 {:class "text-center"} "Libraries"]
  [:br]
  (into 
   [:ul {:class "nav nav-tabs" :role "tablist"}]
   (cons (analysis-step-tab (first library-category-data) true)
         (map analysis-step-tab (rest library-category-data) (repeat false))))
  (into
   [:div {:id "myTabContent" :class "tab-content"}]
   (cons (analysis-step-tab-content (first library-category-data) library-data true)
         (map analysis-step-tab-content
              (rest library-category-data)
              (repeat library-data)
              (repeat false))))
  [:p {:class "text-center"} "Also check out the other " [:a "recommended libraries"] "."]
  [:br]])

^:kindly/hide-code
(kind/hiccup
 [:div {:class "full-width-bg"}
  [:div
   {:class "text-center"}
   [:h1 "Community"]]
  [:div {:class "row d-flex gap-2"}
   [:div {:class "col-3 w-25"}
    [:p  [:img {:src "SciCloj.png" :style {:width "1.2em" :height "auto"}}] " Noj was developed by the " [:a {:href "https://scicloj.github.io/"} "SciCloj"] " community. " [:a {:href "https://scicloj.github.io/docs/community/getting_involved/"} "Join us!"]]]
   [:div {:class "col-3 w-25"}
    [:p [:img {:src "zulip.png" :style {:width "1.5em" :height "auto"}}] " Check our " [:a  {:href "https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev"} "developer chat"] "."]]
   [:div {:class "col-3 w-25"}
    [:p [:img {:src "github.png" :style {:width "1.2em" :height "auto"}}] " Study the " [:a {:href "https://github.com/scicloj/noj"} "source code"] "."]]
   [:div {:class "col-3 w-25"}
    [:p [:img {:src "zulip.png" :style {:width "1.5em" :height "auto"}}] " For any support, write us on the " [:a {:href "https://clojurians.zulipchat.com/#narrow/stream/151924-data-science"} " dedicated channel"] "."]]]])
