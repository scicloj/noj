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
   [:span {:class "home-hero__title"} "Noj"]
   [:sup
    [:img {:src "Noj.png"
           :style {:height "1.2em"
                   :width "auto"
                   :margin-left "0.15em"}}]]]
  [:h1 [:i "A Clojure data analysis stack"]]])

^:kindly/hide-code
(def card-data-row1
  [{:heading "Publishing"
    :text "Clay is a literate programming library. It uses Quarto to target different formats, from HTML to PDFs. It can be used for data exploration, "
    :image "Clay.png"}
   {:heading "Visualization"
    :text "Plotje is a flexible data visualization library inspired by the Grammar of Graphics and the Algebra of Graphics."
    :image "Plotje.png"}
   {:heading "Data Processing and HPC"
    :text "Tablecloth is a fast a mature dataset library. It matches the feature set of R's tibbles with tidyr and dplyr, at a blazing fast speed."
    :image "TMD.png"}])

^:kindly/hide-code
(def card-data-row2
  [{:heading "Math and Statistics"
    :text "Fastmath includes basic functions (e.g., trigonometric or power), linear algebra, random number generators, distributions, and statistical functions, among others."
    :image "Fastmath.png"}
   {:heading "Machine Learning"
    :text "With metamorph, you can build machine learning pipelines. Use the rich collection of functions in metamorph.ml, backed up by Java's Tribuo."
    :image "Metamorph.ml.png"}
   {:heading "Bridges/Interop"
    :text "With libpython-clj and ClojisR, you can access the vast collection of libraries in the Python and R ecosystems. Zero-overhead access to the rich Java ecosystem."
    :image "Libpython_clj.png"}])

^:kindly/hide-code
(defn feature-card [{:keys [heading text image]}]
  [:div {:class ["flex-fill" "w-100" "border" "rounded" "overflow-hidden" "p-2"]}
   [:div {:style {:display "flex" :align-items "center" :gap "12px"}}
    [:img {:src image
            :style {:width "2.5em" :height "auto"}}]
    [:h3 heading]]
    [:p text]])
        
^:kindly/hide-code
(kind/hiccup
 (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
       (map feature-card card-data-row1)))

^:kindly/hide-code
(kind/hiccup [:br])

^:kindly/hide-code
(kind/hiccup
 (into [:div {:class ["d-flex" "flex-column" "flex-md-row" "gap-3"]}]
       (map feature-card card-data-row2)))

^:kindly/hide-code
(kind/hiccup [:br])

^:kindly/hide-code
(kind/hiccup
 [:section
  [:div
   {:class "text-center"}
   [:h1 "Why Noj?"]
   [:hr]]
  [:h2 "Simplicity"]
  [:h2 "Zero deployment overhead"]
  [:h2 "Speed"]
  [:h2 "Maintainability"]
  [:h2 "REPL"]
  [:h2 "Syntax uniformity"]])

^:kindly/hide-code
(kind/hiccup
 [:section
  [:div
   {:class "text-center"}
   [:h1 "Libraries"]
   [:hr]]]
)
