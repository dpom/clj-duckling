(defproject dpom/clj-duckling "0.6.1-dev02"
  :description "A Clojure library that parses text into structured data"
  :license {:url "https://github.com/wit-ai/duckling"
            :comments "see LICENSE"}
  :url "https://dpom.github.io/clj-duckling/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [environ "1.1.0"]
                 [clj-time "0.13.0"]
                 [integrant "0.6.1"]
                 [duct/logger "0.2.1"]
                 [duct/logger.timbre "0.4.1"]
                 [prismatic/plumbing "0.5.4"]]
  ;; :pedantic? :abort
  :plugins [[s3-wagon-private "1.1.2" :exclusions [commons-logging commons-codec]]
            [lein-ancient "0.6.10" :exclusions [commons-logging org.clojure/clojure]]
            [jonase/eastwood "0.2.4"]
            [lein-kibit "0.1.6-beta2" :exclusions [org.clojure/clojure]]
            [lein-cljfmt "0.5.6" :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]
            [lein-codox "0.10.3" :exclusions [org.clojure/clojure]]]
  :repl-options {:init-ns user}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :profiles {:check {:global-vars {*warn-on-reflection* true}}
             :dev {:source-paths   ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :test-paths ["src"]
                   :dependencies [[integrant/repl "0.2.0"]
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
