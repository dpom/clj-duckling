(ns clj-duckling.corpus.edn
  "The edn corpus"
  (:require
   [integrant.core :as ig]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]
   [duct.logger :refer [log Logger]]
   [clj-duckling.corpus.core :as core])
  (:import [java.io File]))

(def ukey
  "this unit key"
  :clj-duckling.corpus/edn)

(defn make-reader
  [util-unit]
  (fn [v]
    ;; (prn (format "%s reader: v = %s" util-unit v))
    (binding [*ns* (the-ns util-unit)] (eval (read-string v)))))

(def edn-readers {'clj-duckling/time (make-reader 'clj-duckling.util.time)
                  'clj-duckling/corpus (make-reader 'clj-duckling.util.corpus)})

(defn read-corpus-file
  "Read corpus from a file

  Args:
  corpus-file (string): filename path
  logger (Logger): logger

  Returns:
  (map): a Corpus map {:context {}, :tests []}"
  [corpus-file logger]
  (log logger :debug ::read-corpus-file {:file corpus-file})
  (edn/read-string {:readers edn-readers} (slurp (io/as-file corpus-file))))



(defrecord EdnCorpus [id corpus language dirpath logger]
  core/Corpus
  (build-corpus! [this]
    (log @logger :debug ::load-corpus {:path dirpath :lang language})
    (let [grammar-matcher (.getPathMatcher
                           (java.nio.file.FileSystems/getDefault)
                           "glob:*.{edn}")
          xf (comp
              (filter #(.isFile ^File %))
              (filter #(.matches grammar-matcher (.getFileName (.toPath ^File %))))
              (map #(.getAbsolutePath ^File %))
              (map #(read-corpus-file % @logger)))]
      (reset! corpus (transduce xf
                                (completing (fn [res item]
                                              (merge-with into res item)))
                                {:context {} :tests []}
                                (file-seq (io/file dirpath))))))
  (get-corpus [this] @corpus)
  (get-id [this] id)
  (set-logger! [this newlogger] (reset! logger newlogger))
  )


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language dirpath logger]} spec
        corpus (->EdnCorpus id (atom nil) language dirpath (atom nil))]
    (log logger :info ::init {:id id :lang language :dirpath dirpath })
    (core/set-logger! corpus logger)
    (core/build-corpus! corpus)
    corpus))
