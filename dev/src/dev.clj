(ns dev
  (:refer-clojure :exclude [test])
  (:require
   [clojure.repl :refer :all]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.tools.namespace.repl :refer [refresh]]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [clj-duckling.core :as d]
   [clj-duckling.system :as sys]
   [clj-duckling.spec]
   [duct.logger :as logger :refer [log]] 
   [integrant.core :as ig]
   [integrant.repl :refer [clear halt go init prep reset]]
   [integrant.repl.state :refer [config system]]))

(defn read-config []
  (sys/read-config "dev.edn"))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src")

(integrant.repl/set-prep! (comp sys/prep read-config))
