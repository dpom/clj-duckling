(ns duckling.system
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre]
   [integrant.core :as ig]))

(def system   nil)

(defn read-config [filename]
  (ig/read-string (slurp (io/resource filename))))

(defn prep [config]
  (doto config ig/load-namespaces))
