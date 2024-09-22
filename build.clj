(ns build
  "Noj's build script.

  clojure -T:build ci
  clojure -T:build deploy

  Run tests via:
  clojure -X:test

  For more information, run:

  clojure -A:deps -T:build help/doc"
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))



(def lib 'org.scicloj/noj)
(def version "2-alpha7")
(def snapshot (str version "-SNAPSHOT"))
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (doseq [alias [:1.11 :1.12 :master]]
    (println "\nRunning tests for Clojure" (name alias))
    (let [basis    (b/create-basis {:aliases (concat (opts :aliases [:test alias]))})
          cmds     (b/java-command
                    {:basis     basis
                     :main      'clojure.main
                     :main-args ["-m" "cognitect.test-runner"]})
          {:keys [exit]} (b/process cmds)]
      (when-not (zero? exit) (throw (ex-info "Tests failed" {})))))
  opts)

(defn- pom-template [version]
  [[:description "A Clojure framework for data science"]
   [:url "https://scicloj.github.io/noj/"]
   [:licenses
    [:license
     [:name "Eclipse Public License - v 2.0"]
     [:url "https://www.eclipse.org/legal/epl-2.0/"]]]])

(defn- jar-opts [opts]
  (let [version (if (:snapshot opts) snapshot version)]
    (assoc opts
           :lib lib   :version version
           :jar-file  (format "target/%s-%s.jar" lib version)
           :basis     (b/create-basis {})
           :class-dir class-dir
           :target    "target"
           :src-dirs  ["src"]
           :pom-data  (pom-template version))))

(defn generate-tests [opts]
  (let [basis    (b/create-basis {:aliases [:gen-tests :dev]})

        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-e" "(require '[gen-tests])(gen-tests/do-generate-tests)(System/exit 0)"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests generation failed" {})))))
(def opts {})
(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (generate-tests (assoc opts :aliases [:dev]))
  (test  (assoc opts :aliases [:dev :gen-tests :test]))
  (b/delete {:path "target"})
  (let [opts (jar-opts opts)]
    (println "\nWriting pom.xml...")
    (b/write-pom opts)
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println "\nBuilding" (:jar-file opts) "...")
    (b/jar opts))
  opts)

(defn deploy "Deploy the JAR to Clojars." [opts]
  (let [{:keys [jar-file] :as opts} (jar-opts opts)]
    (dd/deploy {:installer :remote :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))}))
  opts)
