(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [duckling.core :as d]
            [duckling.system :as sys]
            [duckling.spec]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [clojure.spec.alpha :as s]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]))

(defn read-config []
  (sys/read-config "dev.edn"))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src")

(integrant.repl/set-prep! (comp sys/prep read-config))
