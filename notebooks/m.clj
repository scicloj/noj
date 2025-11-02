(ns m
  (:import [java.text Normalizer Normalizer$Form])
  (:require
   [fastmath.stats :as stats]
   [scicloj.ml.tribuo]
   [scicloj.kindly.v4.kind :as kind]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [scicloj.tableplot.v1.plotly :as plotly]
   [tech.v3.dataset.categorical :as ds-cat]
   [tech.v3.dataset.modelling :as ds-mod]
   [scicloj.metamorph.ml.loss :as loss]
   [scicloj.metamorph.ml :as ml]
   [clojure.string :as str]))

;; # Helper functions

(defn sanitize-column-name-str [s]
  (if (or (nil? s) (empty? s))
    s
    (let [hyphens (str/replace s #"_" "-")
          trimmed (str/trim hyphens)
          nfd-normalized (Normalizer/normalize trimmed Normalizer$Form/NFD)
          no-diacritics (str/replace nfd-normalized #"\p{InCombiningDiacriticalMarks}+" "") ; temporarily
          no-spaces (str/replace no-diacritics #" " "-")
          no-brackets (str/replace no-spaces #"\(|\)" "")
          lower-cased (str/lower-case no-brackets)]
      lower-cased)))

;; ## Helper function for sanitizing book names
(defn parse-books [s]
  (->> (str/split s #",\s\d+")
       (map #(str/replace % #"\d*×\s" ""))
       (map #(str/replace % #"," ""))
       (map #(str/replace % #"\(A\+E\)|\[|\]|komplet|a\+e|\s\(P\+E\+A\)|\s\(e\-kniha\)|\s\(P\+E\)|\s\(P\+A\)|\s\(E\+A\)|papír|papir|audio|e\-kniha|taška" ""))
       (map #(str/replace % #"\+" ""))
       (map #(str/trim %))
       (map sanitize-column-name-str)
       (map #(str/replace % #"\-\-.+$" "")) ;; doubled names
       (map #(str/replace % #"\-+$" "")) ;; hyphens at the end
       (map #(str/replace % #"3" "k3")) ;; elimination of the number 3 at the beginning of two books
       (remove (fn [item] (some (fn [substr] (str/includes? (name item) substr))
                                ["balicek" "poukaz" "zapisnik" "limitovana-edice" "taska" "aktualizovane-vydani" "cd"])))
       distinct
       (mapv keyword)))

(def orders-raw-ds
  (tc/dataset
   "notebooks/wc-orders-report-export-17504026733182-cut.csv"
   {:header? true :separator ","
    #_#_:column-allowlist ["Produkt (produkty)" "Zákazník"]
    #_#_:num-rows 2000
    :key-fn #(keyword (sanitize-column-name-str %))})) ;; this only modifies the column names!

(defn aggregate-ds-onehot [raw-ds]
  (let [;; First, we aggregate all purchases by customer
        customer+orders (-> raw-ds
                            (ds/drop-missing :zakaznik)
                            (tc/group-by [:zakaznik])
                            (tc/aggregate {:all-products #(str/join ", " (ds/column % :produkt-produkty))})
                            (tc/rename-columns {:summary :all-products}))

        ;; We get all unique books from all rows
        all-titles (->> (ds/column customer+orders :all-products)
                        (mapcat parse-books)
                        distinct
                        sort)

        ;; For each customer, we create rows where each book is the target in turn
        customers->rows (map
                         (fn [customer-row]
                           (let [customer-name (:zakaznik customer-row)
                                 books-bought-set (set (parse-books (:all-products customer-row)))
                                 one-hot-map (reduce (fn [acc book]
                                                       (assoc acc book (if (contains? books-bought-set book) 1 0)))
                                                     {}
                                                     all-titles)]
                             (merge {:zakaznik customer-name}
                                    one-hot-map)))
                         (tc/rows customer+orders :as-maps))

        ;; We create a new dataset from the one-hot data
        one-hot-ds (tc/dataset customers->rows)]

    ;; We return the dataset with one-hot encoding and set inference target
    (-> one-hot-ds
        #_(ds/drop-columns [""]))))

;; ## Correlation matrix 

;; ### Here we do correlations between books
(defn correlation-matrix [dataset]
  (let [columns (-> dataset
                    (tc/drop-columns [:zakaznik])
                    (tc/column-names dataset))
        data (tc/select-columns dataset columns)]
    (->> (for [col1 columns
               col2 columns]
           {:var1 col1
            :var2 col2
            :correlation (stats/pearson-correlation ;; or ? https://techascent.github.io/tech.ml.dataset/tech.v3.dataset.column.html#var-correlation

                          (tc/column data col1)
                          (tc/column data col2))})
         (tc/dataset))))

(def simple-ds-onehot (aggregate-ds-onehot orders-raw-ds))

(def corr-matrix-new
  (correlation-matrix simple-ds-onehot))

(defn get-correlation-for-book [book-col corr-matrix]
  (-> corr-matrix
      (tc/select-rows #(= (:var1 %) book-col))
      (tc/drop-columns [:var1])
      (tc/order-by :correlation :desc)))

(get-correlation-for-book :nexus corr-matrix-new)

(kind/table
 (-> corr-matrix-new
     (tc/rename-columns {:var1 :book1
                         :var2 :book2})
     (tc/group-by :book1)
     (tc/select-columns [:book2 :correlation])
     (tc/order-by :correlation :desc))
 {:use-datatables true
  :datatables {:scrollY 800}})

(-> (tc/select-columns simple-ds-onehot (ds/column-names simple-ds-onehot))
    (tc/drop-columns [:zakaznik])
    (plotly/layer-correlation)
    (plotly/plot)
    (assoc-in [:layout] {:title "Correlations"
                         :width 1200
                         :height 900
                         :xaxis {:tickangle 45}}))

;; # Prediction

;; ## One-hot 
(defn aggregate-multiply-and-one-hot-encode [raw-ds]
  (let [;; First, we aggregate all purchases by customer
        customer-books (-> raw-ds
                           (ds/drop-missing :zakaznik)
                           (tc/group-by [:zakaznik])
                           (tc/aggregate {:all-products #(str/join ", " (ds/column % :produkt-produkty))})
                           (tc/rename-columns {:summary :all-products}))

        ;; We get all unique books from all rows
        all-books (->> (ds/column customer-books :all-products)
                       (mapcat parse-books)
                       distinct
                       sort)

        ;; For each customer, we create rows where each book is the target in turn
        rows-with-books (mapcat
                         (fn [customer-row]
                           (let [customer-name (:zakaznik customer-row)
                                 books-bought (parse-books (:all-products customer-row))]
                             (when (> (count books-bought) 1)  ; Only customers with more than one book
                               ;; For each purchased book, we create a row
                               (for [target-book books-bought]
                                 (let [feature-books (set (remove #(= % target-book) books-bought))
                                       one-hot-map (reduce (fn [acc book]
                                                             (assoc acc book (if (contains? feature-books book) 1 0)))
                                                           {}
                                                           all-books)]
                                   (merge {:zakaznik customer-name
                                           :next-predicted-buy target-book}
                                          one-hot-map))))))
                         (tc/rows customer-books :as-maps))

        ;; We create a new dataset from the one-hot data
        one-hot-ds (tc/dataset rows-with-books)
        _ (println "Customers with more than 1 book: " (ds/row-count rows-with-books))]

    ;; We return the dataset with one-hot encoding and set inference target
    (-> one-hot-ds
        (ds/drop-columns [:zakaznik])
        (ds-mod/set-inference-target [:next-predicted-buy]))))

(def multimplied-ds-onehot
  (-> orders-raw-ds
      aggregate-multiply-and-one-hot-encode
      (ds/categorical->number [:next-predicted-buy])))

(kind/table
 (tc/head multimplied-ds-onehot))

;; ## We just split the dataset for training and testing purposes
(def split
  (-> multimplied-ds-onehot
      (tc/split->seq :holdout {:seed 42})
      first))

(def nb-model-t  ;;https://tribuo.org/learn/4.3/javadoc/org/tribuo/classification/mnb/package-summary.html#class-summary
  (ml/train
   (ds-mod/set-inference-target (:train split) [:next-predicted-buy])
   {:model-type :scicloj.ml.tribuo/classification
    :tribuo-components [{:name "tribuo-classification-mnnaivebayes"
                         :target-columns [:next-predicted-buy]
                         :type "org.tribuo.classification.mnb.MultinomialNaiveBayesTrainer"}]
    :tribuo-trainer-name "tribuo-classification-mnnaivebayes"}))

(defn convert-predictions-to-categories
  "Converts numeric predictions back to categories using categorical maps from reference dataset"
  [prediction-dataset reference-dataset]
  (let [cat-maps (ds-cat/dataset->categorical-maps reference-dataset)]
    (ds-cat/invert-categorical-map prediction-dataset cat-maps)))


(-> (:test split)
    (convert-predictions-to-categories (:train split))
    (ds/column :next-predicted-buy))

(ml/predict (:test split) nb-model-t) ;; !now it works with less rows!

(ds/categorical->number)
(-> split :test tc/info)
(-> split :test :next-predicted-buy)
;; ## Testing accuracy

(-> (:test split)
     (ds/column :next-predicted-buy)
      meta    
    )

(-> (ml/predict (:test split) nb-model-t)
    
    (ds/column :next-predicted-buy)
    
    )

 

(loss/classification-accuracy
 (-> (:test split)
     (ds-cat/reverse-map-categorical-xforms)
     (ds/column :next-predicted-buy))
 (-> (ml/predict (:test split) nb-model-t)
     (ds-cat/reverse-map-categorical-xforms)
     (ds/column :next-predicted-buy)))

(-> (ml/predict (:test split) nb-model-t)
    
    (ds/column :next-predicted-buy))

;; ## What book will customer buy next?

(defn predict-next-book
  "Predicts the next book based on owned books - uses native categorical conversion"
  [owned-books my-model]
  (try
    (let [; Ensure that owned-books is always a collection
          owned-books-coll (if (coll? owned-books) owned-books [owned-books])
          ; Create a list of book columns from the training data
          train-features (-> (:train split)
                             (ds/drop-columns [:next-predicted-buy])
                             tc/column-names)
          ; Filter only the books that exist in the training data
          valid-owned-books (filter #(contains? (set train-features) %) owned-books-coll)
          ; Create a zero map from all column names
          zero-map (zipmap train-features (repeat 0))
          ; Merge with a smaller map of ones for the books the user owns
          input-data (merge zero-map (zipmap valid-owned-books (repeat 1)))
          ;; Create input dataset with placeholder target column 
          full-input-data (assoc input-data :next-predicted-buy nil)
          ; Convert map to ds and set inference target
          input-ds (-> (ds/->dataset [full-input-data])
                       (ds-mod/set-inference-target [:next-predicted-buy]))
          ;; Prediction – creates a dataset where books will have predicted probabilities
          raw-pred (ml/predict input-ds my-model)
          ;; NATIVE CONVERSION: Use ds-cat/reverse-map-categorical-xforms instead of manual conversion
          predicted-categories-ds (convert-predictions-to-categories raw-pred (:train split))
          predicted-category (-> predicted-categories-ds
                                 (ds/column :next-predicted-buy)
                                 first)]
      predicted-category)
    (catch Exception e
      (println "Error in predict-next-book:" (.getMessage e))
      (println "Stack trace:")
      (.printStackTrace e)
      nil)))

(defn predict-next-n-books [input n]
  (loop [acc []
         predict-from input
         idx n]
    (let [predicted (predict-next-book predict-from nb-model-t)]
      (if (> idx 0)
        (recur (conj acc predicted) (conj predict-from predicted) (dec idx))
        (distinct acc)))))

(predict-next-book [:zacnete-s-proc] nb-model-t)

(predict-next-n-books [:mysleni-rychle-a-pomale :nexus] 5)