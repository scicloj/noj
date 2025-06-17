;; # Underlying libraries

^:kindly/hide-code
(ns noj-book.underlying-libraries 
  (:require
   [clojure.edn :as edn]
   [scicloj.kindly.v4.kind :as kind]
   [tablecloth.api :as tc]))

;; Noj consists of the following libraries categorized as follows:

;; (See also the list of [other recommended libraries](./noj_book.recommended_libraries.html),
;; which are not included in Noj.)

^:kindly/hide-code
(def underlying-libs (edn/read-string (slurp "notebooks/underlying-libraries.edn")))

^:kindly/hide-code
(defn hiccup-link [{:keys [link-url link-text]}]
  [:a {:href link-url :target "_blank"} link-text])

^:kindly/hide-code
(def libs-by-category
  (reduce
   (fn [acc cat-key]
     (conj acc [(get-in underlying-libs [:categories :keys cat-key])
                (->> ((comp vals :dependencies) underlying-libs)
                     (filter #(= (:category %) cat-key))
                     (map (fn [{:keys [lib-name description links] :as dep-info}]
                            (let [{:keys [github-link ref-link]} links]
                              {"Library Name" lib-name
                               "Description"  (kind/md description)
                               "Links"        (cond-> [:div]
                                                github-link (conj (hiccup-link {:link-url  github-link
                                                                                :link-text "github"}))
                                                (and github-link ref-link) (conj [:span " "])
                                                ref-link (conj (hiccup-link {:link-url  ref-link
                                                                             :link-text "ref"}))
                                                true (kind/hiccup))}))))]))
   []
   (get-in underlying-libs [:categories :orders])))

^:kindly/hide-code
(kind/hiccup
  (for [[cat-name libs] libs-by-category]
    [:div
     [:h2 cat-name]
     (kind/table libs)]))

^:kindly/hide-code
(def direct-deps
  (->
   (edn/read-string (slurp "deps.edn"))
   :deps
   keys
   ))
^:kindly/hide-code
(def all-deps-info
  (-> 
   (clojure.java.shell/sh  "clj" "-X:deps" "list" ":format" ":edn")
   :out
   (edn/read-string)))

^:kindly/hide-code
(def direct-deps-info 
  (select-keys
   all-deps-info
   direct-deps))

;; ## List of All Direct Noj Dependencies

;; The following are the direct dependencies included in Noj:
;; (This section would contain the actual dependency list)

^:kindly/hide-code
(kind/table
 (->
  (map
   (fn [[dep info]]
     (hash-map :lib (str dep)
               :version (:mvn/version info)
               :license (-> info :license :name))
     )
   direct-deps-info)
  (tc/dataset)
  (tc/select-columns {:lib     "Library Name"
                      :version "Version"
                      :license "License"})
  (tc/order-by [:lib])))
