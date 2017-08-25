(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [duckling.core :as d]
            ;; [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [eftest.runner :as eftest]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
))

(defn read-config []
  (ig/read-string (slurp (io/resource "dev.edn"))))

(defn test []
  (eftest/run-tests (eftest/find-tests "src")))

;; (defmethod ig/init-key ::include [_ paths] paths)


(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src")

;; (when (io/resource "local.clj")
;;   (load "local"))

(integrant.repl/set-prep! read-config)
