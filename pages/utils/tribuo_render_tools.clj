(ns noj-book.utils.tribuo-render-tools
  (:require
   [clojure.java.classpath]
   [clojure.reflect]
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.ml.tribuo]
   [tablecloth.api :as tc]
   [noj-book.utils.example-code :refer [example-code]])
  (:import
   [com.oracle.labs.mlrg.olcut.config DescribeConfigurable]))



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



(defn safe-configurable->docu [class]
  {:class class
   :options
   (try
     (configurable->docu class)
     (catch Exception _ nil))})


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



(defn render-configurables [configurables]
  (kind/fragment
   (map
    (fn [trainer]
      (let [class-name (.getName (:class trainer))]
        (kind/fragment
         (concat
          [(kind/md (str "#### " (str/replace class-name
                                              #"org\.tribuo" "o..t..")))
           (kind/hiccup [:a {:href (class->tribuo-url (:class trainer))} "javadoc"])]

          (get example-code class-name)
          [(kind/md (str "All configurable options for " class-name ":"))
           (kind/table
            (->
             trainer
             :options
             vec
             tc/dataset))]))))
    configurables)))


(defn all-non-trainer[]
  (->> (all-configurables com.oracle.labs.mlrg.olcut.config.Configurable)
       (remove #(str/ends-with? (.getName %) "Trainer"))
       (map safe-configurable->docu)
       (remove #(empty? (:options %)))
       (sort-by #(.getName (:class %)))))
