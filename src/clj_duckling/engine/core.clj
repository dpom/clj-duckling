(ns clj-duckling.engine.core
  "Engine common protocol and specs"
  (:require
   [clojure.spec.alpha :as s])
  )

(defprotocol Engine
  (load-rules! [this] "Load rules")
  (get-rules [this] "Get rules"))
