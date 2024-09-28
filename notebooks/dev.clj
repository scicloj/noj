(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(clay/make! {:show false
             :format [:quarto :html]
             :base-source-path "notebooks"
             :source-path (->> "notebooks/chapters.edn"
                               slurp
                               clojure.edn/read-string
                               (map (fn [part]
                                      (-> part
                                          (update
                                           :chapters
                                           (partial map #(format "noj_book/%s.clj" %))))))
                               (cons "index.clj"))
             :base-target-path "docs"
             :book {:title "Scinojure Documentation"}
             :clean-up-target-dir true
             :quarto {:include-in-header {:text "<meta property=\"og:image\" content=\"https://scicloj.github.io/sci-cloj-logo-transparent.png\"/>
<meta property=\"og:title\" content=\"Scinojure Documentation\"/>
<meta property=\"og:description\" content=\"Clojure libraries for data and science\"/>
<link rel = \"icon\" href = \"data:,\" />"}}})
(System/exit 0)
