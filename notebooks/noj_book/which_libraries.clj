;; # Smile regression models reference - DRAFT 🛠

;; authors: Timothy Pratley, Daniel Slutsky

^:kindly/hide-code
(ns noj-book.which-libraries
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

(def model 
  (-> "data/libraries/model.edn"
      slurp
      read-string))

(defn parse-libraries [model-libs]
  (-> (apply concat model-libs)
      tc/dataset))

(def clojure-libraries
  (-> model
      :libs
      parse-libraries))

(def python-libraries
  (-> model
      :python-libraries
      parse-libraries))

(def name->library
  (->> (concat (tc/rows clojure-libraries :as-maps)
               (tc/rows python-libraries :as-maps))
       (map (fn [{:as row
                  :keys [lib/name]}]
              [name row]))
       (into {})))

(defn name-link [lib-name lib-url]
  (kind/hiccup
   [:a {:href lib-url}
    lib-name]))

(defn tag->libraries [libraries]
  (-> libraries
      (tc/rows :as-maps)
      (->> (mapcat (fn [{:as lib
                         :keys [tags lib/name]}]
                     (->> tags
                          (map (fn [tag]
                                 [tag name])))))
           (group-by first))
      (update-vals (comp set (partial map second)))))


(def tag->clojure-libraries
  (tag->libraries clojure-libraries))

(def tag->python-libraries
  (tag->libraries python-libraries))

(def all-tags
  (into (set (keys tag->clojure-libraries))
        (set (keys tag->clojure-libraries))))

(defn libraries-view [libraries]
  (-> libraries
      (tc/map-columns :experimental
                      [:tags]
                      (fn [tags]
                        (when (tags :exp)
                          "🛠")))
      (tc/map-columns :active
                      [:tags]
                      (fn [tags]
                        (when (tags :act)
                          "✅")))
      (tc/map-columns :star
                      [:star]
                      (fn [star]
                        (when star
                          "🌟")))
      (tc/map-columns :name
                      [:lib/name :lib/url]
                      name-link)
      (tc/select-columns [:name :star :active :experimental :tags :description])
      (kind/table
       {:use-datatables true})))

(libraries-view clojure-libraries)


(-> python-libraries
    (tc/select-columns [:name :star :tags :description])
    (tc/map-columns :clojure-libraries
                    [:tags]
                    (fn [tags]
                      (->> tags
                           (mapcat tag->clojure-libraries)
                           distinct
                           set)))
    kind/table)


(defn library-names->list-of-links [names]
  (->> names
       (map name->library)
       (map (fn [{:keys [lib/name lib/url]}]
              [:li (name-link name url)]))
       (into [:ul])
       kind/hiccup))

(-> all-tags
    (->> (map (fn [tag]
                {:tag tag
                 :clojure (->> tag
                               tag->clojure-libraries
                               library-names->list-of-links)
                 :python (->> tag
                              tag->python-libraries
                              library-names->list-of-links)})))
    tc/dataset
    (kind/table
     {:use-datatables true
      :scrollX true})
    (tc/select-rows #(tag->python-libraries (:tag %))))
