(ns noj-book.eval-code 
  (:require
   [clojure.pprint :as pprint]
   [scicloj.kindly.v4.kind :as kind]))

(defn pp-code-to-str [expression]
  (with-out-str (pprint/write expression :dispatch pprint/code-dispatch)))


(defn eval-code [expression]
  (let [eval-result (eval expression)
        code (kind/code  (pp-code-to-str expression))]

    (cond
      (true? (-> expression meta :kindly/hide-code))
      eval-result
      (var? eval-result)
      code
      :else
      (kind/fragment
       [code
        eval-result]))))


(defmacro ->eval-code [& forms]
  (mapv
   (fn [exp]
     `(eval-code '~exp))
   forms))

