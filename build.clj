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
(def version "2-beta19.1")
(def snapshot (str version "-SNAPSHOT"))
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (let [opts     (or opts {})
        basis    (b/create-basis {:aliases (concat (opts :aliases [:test]))})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "kaocha.runner"
                               ":tests"]}                  
                  )
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
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

(defn generate-tests [_]
  (let [basis    (b/create-basis {:aliases [:gen-tests :model-integration-tests :test]})

        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-e" "(require '[gen-tests])(gen-tests/do-generate-tests)(System/exit 0)"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests generation failed" {})))))

(def opts {})

  

(defn models-integration-tests "Run integration tests." [opts]
  (let [basis    (b/create-basis {:aliases [:model-integration-tests]})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "kaocha.runner"
                               ":integration-tests" ]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Integration tests failed" {}))))
  opts)


(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (models-integration-tests nil)
  (generate-tests nil)
  (test  (assoc opts :aliases [:dev :test]))
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



(def uber-file-clojupyter (format "target/%s-%s-clojupyter.jar" (name lib) version))
(def uber-file-clay (format "target/%s-%s-clay.jar" (name lib) version))

(defn create-uber-clojupyter "Create uber with clojupyter + noj" [opts]
  (let [basis (b/create-basis {:aliases [:clojupyter :uber]})]
    (println "\nCompiling ...")
    (b/compile-clj {:basis basis
                    :ns-compile '[clojupyter.kernel.core
                                  clojupyter.cmdline]
                    :class-dir class-dir})
    (println "\nBuilding" uber-file-clojupyter "...")    
    (b/uber {:uber-file uber-file-clojupyter
             :class-dir "target/classes"
             ;;:conflict-handlers {:default  :warn }
             :basis basis
             :main 'clojupyter.cmdline
             :exclude ["org.scicloj/clay"]})))

(defn create-uber-clay "Create uber with noj incl clay" [opts]
  (let [basis (b/create-basis {:aliases [:uber]})]
    (println "\nCompiling ...")
    (b/compile-clj {:basis basis
                    :ns-compile '[scicloj.clay.v2.main]
                    :class-dir class-dir})
    (println "\nBuilding" uber-file-clay "...")
    (b/uber {:uber-file uber-file-clay
             :class-dir "target/classes"
             ;;:conflict-handlers {:default  :warn }
             :basis basis
             :main 'scicloj.clay.v2.main})))


(defn install-clojupyter-kernel "Install  clojupyter kernel in local Jupyter" [opts]
  
  (let [basis    (b/create-basis {:aliases [:clojupyter]})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "clojupyter.cmdline"
                               "install"
                               "--ident" (str "noj-jupyter-" version)
                               "--jarfile" uber-file-clojupyter]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Install clojupyter kernel failed" {})))))
  
  
(defn remove-clojupyter-kernel "Remove clojupyter kernel from local Jupyter" [opts]
  (let [basis    (b/create-basis {:aliases [:clojupyter]})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "clojupyter.cmdline"
                               "remove-install"
                               (str "noj-jupyter-" version)
                               ]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Remove clojupyter kernel failed" {})))))
  

(defn replace-clojupyter-kernel "Replaces local clojupyter kernel in local Jupyter" [opts]
     (remove-clojupyter-kernel nil)
      (install-clojupyter-kernel nil))
  
