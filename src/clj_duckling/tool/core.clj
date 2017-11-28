(ns clj-duckling.tool.core
  "Tool common protocol and specs"
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
))


(defprotocol Tool
  (build-tool! [this] "(Re)Build the tool")
  (apply-tool [this text] [this text dims] [this text dims context] "Apply the tool to a text")
  (set-logger! [this newlogger] "Set a new logger")
  (get-id [this] "Get tool's id")
  )

