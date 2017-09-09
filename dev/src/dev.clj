(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [duckling.core :as d]
            [duckling.system :as sys]
            ;; [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [eftest.runner :as eftest]
            [taoensso.timbre :as timbre]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
))

(defn read-config []
  (sys/read-config "dev.edn"))

(defn test []
  (eftest/run-tests (eftest/find-tests "src")))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src")

(integrant.repl/set-prep! (comp sys/prep read-config))
