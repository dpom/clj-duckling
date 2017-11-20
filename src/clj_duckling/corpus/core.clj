(ns clj-duckling.corpus.core
  "Corpus common protocol and specs"
  (:require
   [clojure.spec.alpha :as s]))

(defprotocol Corpus
  (get-id [this] "Get corpus id")
  (build-corpus! [this] "Build and save corpus.")
  (get-corpus [this] "Get corpus")
  (set-logger! [this newlogger] "Set logger"))

(s/def :corpus/dirpath string?)
(s/def :corpus/logger map?)


