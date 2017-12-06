(ns clj-duckling.engine.core
  "Engine common protocol and specs"
  (:require
   [clojure.spec.alpha :as s]
   [nlpcore.protocols :as core]
   ))

(defprotocol Engine
  (load-rules! [this] "Load rules")
  (get-rules [this] "Get rules"))


(s/def :engine/rules #(and (satisfies? core/Module %) (satisfies? Engine %)))
