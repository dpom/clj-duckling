(ns duckling.logger
  (:require
   [integrant.core :as ig]
   [taoensso.timbre :as log]))

(defmethod ig/init-key :duckling/logger [_ params]
  (log/merge-config! params)
  log/*config*)
