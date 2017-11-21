(ns clj-duckling.engine.core
  "Engine common protocol and specs"
  (:require
   [clojure.spec.alpha :as s])
  )

(defprotocol Engine
  (load-rules! [this] "Load rules")
  (get-rules [this] "Get rules")
  (get-id [this] "Get the engine id")
  (set-logger! [this newlogger] "Set a new logger"))
