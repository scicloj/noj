^:kindly/hide-code
(ns noj-book.tribuo-reference
  (:require
   [clojure.java.classpath]
   [clojure.pprint :as pprint]
   [clojure.reflect]
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.ml.tribuo]
   [scicloj.metamorph.ml :as ml]
   [tech.v3.dataset.modelling :as ds-mod]
   [tablecloth.api :as tc]
   [ noj-book.eval-code :refer [->eval-code]]
   
   )
  (:import
   [com.oracle.labs.mlrg.olcut.config DescribeConfigurable]
   [java.util.regex Matcher]))





;; ## Tribuo reference


^:kindly/hide-code
(defn all-configurables [interface]

  (->> (clojure.java.classpath/classpath-jarfiles)
       (filter (fn [^java.util.jar.JarFile jf]
                 (re-matches #".*tribuo.*" (.getName jf))))
       (mapcat clojure.java.classpath/filenames-in-jar)
       (map (fn [class-filename]
              (try (some-> class-filename
                           (str/replace #"/" ".")
                           (str/replace #"\.class$" "")
                           (Class/forName))
                   (catch Exception _ nil))))
       (filter (fn [cls]
                 (->> cls
                      supers
                      (some #(= % interface
                                ;org.tribuo.Trainer
                                ;com.oracle.labs.mlrg.olcut.config.Configurable
                                )))))))

^:kindly/hide-code
(defn configurable->docu [clazz]
  (->>
   (DescribeConfigurable/generateFieldInfo clazz)
   vals
   (map (fn [field-info]
          {:name  (.name field-info)
           :description (.description field-info)
           :type 
           (kind/md
            (str/replace
             (str (.getGenericType (.field field-info)))
             #"\$"
             (java.util.regex.Matcher/quoteReplacement "\\$")))
           
           :default (.defaultVal field-info)}))))


^:kindly/hide-code
(defn safe-configurable->docu [class]
  {:class class
   :options
   (try
     (configurable->docu class)
     (catch Exception _ nil))})

^:kindly/hide-code
(defn trainer-infos []
  (->> (all-configurables org.tribuo.Trainer)
       (map safe-configurable->docu)
       (remove #(empty? (:options %)))
       (sort-by #(.getName (:class %)))))

^:kindly/hide-code
(defn class->tribuo-url [class]
  (if (nil? class)
    ""
    (str "https://tribuo.org/learn/4.3/javadoc/"
         (str/replace (.getName class)
                      "." "/")
         ".html")))





(def extra-doc 
  {"org.tribuo.classification.baseline.DummyClassifierTrainer"

   (->eval-code
    ^:kindly/hide-code
    (kind/md "The DummyClassifier predicts a value, using a 'dummy' algorithm ")
    (def df
      (->
       (tc/dataset  {:a [1 2]  :target [:x :x]})
       (ds-mod/set-inference-target :target)))
    (kind/table df)
    (def model (ml/train df {:model-type :scicloj.ml.tribuo/classification
                             :tribuo-components [{:name "dummy"
                                                  :type "org.tribuo.classification.baseline.DummyClassifierTrainer"
                                                  :properties {:dummyType :CONSTANT
                                                               :constantLabel "c"}}]
                             :tribuo-trainer-name "dummy"}))
    model
    ^:kindly/hide-code
    (kind/md "'c' in this case:")
    (ml/predict df model))
   })




^:kindly/hide-code
(defn render-configurables [configurables]
  (kind/fragment
   (map
    (fn [trainer]
      (let [class-name (.getName (:class trainer))]
        (kind/fragment
         (concat 
          [
           (kind/md (str "#### " (str/replace class-name
                                              #"org\.tribuo" "o..t..")))
           (kind/hiccup [:a {:href (class->tribuo-url (:class trainer))} "javadoc"])]

          (get extra-doc class-name)
          [(kind/md (str "All configurable options for " class-name ":"))
           (kind/table
             (->
              trainer
              :options
              vec
              tc/dataset))]
          
          ))))
    configurables)))

; ### Tribuo trainer reference 
^:kindly/hide-code
(render-configurables (trainer-infos))


;; ### Tribuo component reference 

^:kindly/hide-code
(def all-non-trainer
  (->> (all-configurables com.oracle.labs.mlrg.olcut.config.Configurable)
       (remove #(str/ends-with? (.getName %) "Trainer"))
       (map safe-configurable->docu)
       (remove #(empty? (:options %)))
       (sort-by #(.getName (:class %)))))

^:kindly/hide-code
(render-configurables  all-non-trainer)

     

