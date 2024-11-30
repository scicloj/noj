^:kindly/hide-code
(ns noj-book.tribuo-reference
  (:require
   [clojure.java.classpath]
   [clojure.reflect]
   [clojure.string :as str]
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind])
  (:import
   [java.util.regex Matcher]
   [com.oracle.labs.mlrg.olcut.config DescribeConfigurable]))

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




^:kindly/hide-code
(defn render-configurables [configurables]
  (kind/fragment
   (map
    (fn [trainer]
      (kind/fragment
       [
        (kind/md (str "#### " (str/replace (.getName (:class trainer))
                                           #"org\.tribuo" "o..t..")))
        (kind/hiccup [:a {:href (class->tribuo-url (:class trainer))} "javadoc"])
        (kind/table
         (->
          trainer
          :options
          vec
          tc/dataset))]))
    configurables)))

;; ### Tribuo trainer reference 
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

     

