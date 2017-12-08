(ns clj-duckling.system
  "Integrant system specific functions"
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(def system   nil)

(defn read-config [filename]
  (ig/read-string (slurp (io/resource filename))))

(defn prep [config]
  (doto config ig/load-namespaces))


(defn make-test-logger [level]
  {
   :duct.logger/timbre {:level level
                        :appenders {:duct.logger.timbre/brief (ig/ref :duct.logger.timbre/brief)}},
   :duct.logger.timbre/brief {:min-level level}
   }
  )

(defn get-test-module
  [configfile ukey]
  (-> configfile
      io/file
      slurp
      ig/read-string
      prep
      ig/init
      ukey))
