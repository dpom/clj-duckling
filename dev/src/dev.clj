(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [duckling.core :as d]
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
  (ig/read-string (slurp (io/resource "dev.edn"))))

(defn dev-prep [config]
  (doto config ig/load-namespaces))

(defn test []
  (eftest/run-tests (eftest/find-tests "src")))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src")

;; (when (io/resource "local.clj")
;;   (load "local"))

(defmethod ig/init-key ::logger [_ params]
  (timbre/merge-config! params)
  timbre/*config*)


(integrant.repl/set-prep! (comp dev-prep read-config))
