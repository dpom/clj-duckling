(ns clj-duckling.corpus.edn
  "The edn corpus"
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]
   [duct.logger :refer [log Logger]]
   [clj-duckling.corpus.util :as corpus]
   [clj-duckling.corpus.core :as core]))

(def ukey
  "this unit key"
  :clj-duckling.corpus/edn)

(defn default-reader
  [t v]
  (prn (format "t: %s, v:%s" t v))
  (apply (resolve t) v))

(defn read-corpus
  "Read corpus from a file

  Args:
  corpus-file (string): filename path

  Returns:
  (map): a Corpus map {:context {}, :tests []}"
  [corpus-file]
  (edn/read-string {:default default-reader} (slurp (io/as-file corpus-file))))


(defn train-corpus
  [traindir]
  (->> (file-seq (io/as-file traindir))
       )
  )

(defrecord EdnCorpus [id corpus language dirpath logger]
  core/Corpus
  (build-corpus! [this]
    (log @logger :debug ::train {:path traindir :lang language})
    (log @logger :error ::not-implemented))
  (get-corpus [this]
    @model)
  (get-id [this] id)
  (set-logger! [this newlogger]
    (reset! logger newlogger))
  )


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language dirpath logger]} spec
        corpus (->EdnCorpus id (atom nil) language dirpath (atom nil))]
    (log logger :info ::init {:id id :lang language :dirpath dirpath })
    (core/set-logger! corpus logger)
    (core/build-corpus! corpus)
    corpus))

