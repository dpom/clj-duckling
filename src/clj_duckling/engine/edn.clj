(ns clj-duckling.engine.edn
  "The edn format rules engine"
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]
   [duct.logger :refer [log Logger]]
   [clj-duckling.engine.core :as core])
(:import [java.io File]))

(def ukey
  "this unit key"
  :clj-duckling.engine/edn)



(defrecord EdnEngine [id rules language dirpath logger]
  core/Engine
  (load-rules! [this]
    (log @logger :debug ::load-rules {:path dirpath :lang language})
    )
  (get-rules [this] @rules)
  (get-id [this] id)
  (set-logger! [this newlogger] (reset! logger newlogger))
  )


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language dirpath logger]} spec
        engine (->EdnEngine id (atom nil) language dirpath (atom nil))]
    (log logger :info ::init {:id id :lang language :dirpath dirpath })
    (core/set-logger! engine logger)
    (core/load-rules! engine)
    engine))
