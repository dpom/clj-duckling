(defproject dpom/clj-duckling "0.4.25-dev03"
  :description "A Clojure library that parses text into structured data"
  :license {:url "https://github.com/wit-ai/duckling"
            :comments "see LICENSE"}
  :url "https://dpom.github.io/clj-duckling/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha19"]
                 [com.taoensso/timbre "4.10.0"]
                 [environ "1.1.0"]
                 [clj-time "0.13.0"]
                 [integrant "0.6.1"]
                 [prismatic/plumbing "0.5.4"]]
  ;; :pedantic? :abort
  :plugins [[s3-wagon-private "1.1.2" :exclusions [commons-logging commons-codec]]
            [lein-ancient "0.6.10" :exclusions [commons-logging org.clojure/clojure]]
            [lein-checkall "0.1.1" :exclusions [org.clojure/tools.namespace org.clojure/clojure]]
            [lein-cljfmt "0.5.6" :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]
            [lein-eftest "0.3.1"]
            [lein-codox "0.10.3" :exclusions [org.clojure/clojure]]]
  :repl-options {:init-ns user}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :profiles {:check {:global-vars {*warn-on-reflection* true}}
             :dev {:source-paths   ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :test-paths ["src"]
                   :env {:timbre-level "trace"
                         :config-file "dev.edn"}
                   :dependencies [[integrant/repl "0.2.0"]
                                  [eftest "0.3.1"]
                                  [org.clojure/tools.trace "0.7.9"]
                                  [cheshire "5.7.1"]]}
             :uberjar {:aot [duckling.core]}}
  :test-selectors {:default (complement :benchmark)
                   :benchmark :benchmark}
  :scm {:name "git"
        :url "https://github.com/dpom/clj-duckling"}
  :pom-addition [:developers [:developer
                              [:name "Dan Pomohaci"]
                              [:url "https://github.com/dpom/clj-duckling"]
                              [:email "dan.pomohaci@gmail.com"]
                              [:timezone "+3"]]]
  :codox {:doc-files []
          :output-path "docs/api"}
  )
