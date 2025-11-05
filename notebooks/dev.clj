(ns dev
  (:require
   [clojure.edn :as edn]
   [scicloj.clay.v2.api :as clay]))


(defn base-config [clj-files]
  {:show false
   :format [:quarto :html]
   :base-source-path "notebooks"
   :source-path clj-files
   :base-target-path "docs"
   :book {:title "Noj"}
   :clean-up-target-dir true
   :quarto {:include-in-header {:text "<meta property=\"og:image\" content=\"https://scicloj.github.io/sci-cloj-logo-transparent.png\"/>
<meta property=\"og:title\" content=\"The Noj book\"/>
<meta property=\"og:description\" content=\"A Clojure toolkit for data science\"/>
<link rel = \"icon\" href = \"data:,\" />"}}})


(def clj-files
  (->> "notebooks/chapters.edn"
       slurp
       edn/read-string
       (map (fn [part]
              (-> part
                  (update
                   :chapters
                   (partial map #(format "noj_book/%s.clj" %))))))
       (cons "index.clj"))

  )

;; For interactive local testing:
(comment
  (-> (base-config clj-files)
      (assoc :show true
             :base-target-path "docs-draft")
      clay/make!))

;; can be called by
;; clj -A:dev -X dev/render-notebook :notebook '"noj_book/ml_basic.clj"'
(defn render-notebook [opts]
  (try 
    (clay/make! (base-config [(:notebook opts)]))
    (catch  Exception e (.printStackTrace e)) 
    )
  (System/exit 0))

(defn render-all-notebooks [_opts]
  (try 
    (clay/make! (base-config clj-files))
    (catch  Exception e (.printStackTrace e)) 
    )
  (System/exit 0))

