(defproject clj-duckling "0.4.25-dev"
  :description "A Clojure library that parses text into structured data"
  :license {:url "https://github.com/wit-ai/duckling"
            :comments "see LICENSE"}
  :url "https://dpom.github.io/clj-duckling/"
  :plugins [[s3-wagon-private "1.1.2" :exclusions [commons-logging commons-codec]]
            [lein-ancient "0.6.10" :exclusions [org.clojure/clojure]]
            [lein-codeindex "0.1.0"]
            [lein-checkall "0.1.1" :exclusions [org.clojure/tools.namespace org.clojure/clojure]]
            [lein-cljfmt "0.5.6"]
            [lein-codox "0.10.3"]]
  :repl-options {:init-ns duckling.core}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [org.clojure/tools.logging "0.4.0"]
                 [clj-time "0.13.0"]
                 [prismatic/plumbing "0.5.4"]]
  :pedantic? :abort
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :profiles {:check {:global-vars {*warn-on-reflection* true}}
             :dev {:test-paths ["src"]
                   :dependencies [[org.clojure/tools.trace "0.7.9"]
                                  ;; [midje "1.8.3"]
                                  [pjstadig/humane-test-output "0.8.0"]
                                  [cheshire "5.7.1"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                                }
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
