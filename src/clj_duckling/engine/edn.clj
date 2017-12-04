(ns clj-duckling.engine.edn
  "The edn format rules engine"
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [duct.logger :refer [log Logger]]
   [nlpcore.protocols :as core]
   [clj-duckling.dims.time.prod]
   [clj-duckling.util.core :as util]
   [clj-duckling.engine.core :as eng])
(:import [java.io File]))

(def ukey
  "this unit key"
  :clj-duckling.engine/edn)

;; Lookup and basic matching functions, used by patterns in rules

(defn- re-pos
  "Finds regex matches in s, with their position and groups.
  Returns a vec of [pos matched_text groups]"
  [re s]
  (loop [m (re-matcher re s)
         res []]
    (if (.find m)
      (recur m
             (conj res
                   [(.start m)
                    (.group m)
                    (vec (map #(.group m (int %)) (range 1 (inc (.groupCount m)))))]))
      res)))

(defn- lookup-re
  "Lookup regex in s, starting at a given position, and builds one token per found match"
  [regex [{s :text}] position]
  {:pre [regex s]}
  (try
    (let [matches (for [[pos word groups] (re-pos regex (subs s position))]
                    (do
                      (when (str/blank? word)
                        (throw (ex-info "Zero-length or blank captures forbidden"
                                        {:regex regex :s s})))
                      {:pos (+ position pos)
                       :end (+ position pos (count word))
                       :text word
                       :groups groups}))]
       (filter #(util/separated-substring? s (:pos %) (:end %)) matches))
    (catch Exception e
      (throw (ex-info "@lookup-re" {:exception e :s s :regex regex})))))

(defn- lookup-token
  "Finds tokens satisfying constraints"
  [pattern-filter stash]
  {:pre [pattern-filter]}
  ;; FIXME brute force approach that could be improved!
  ;; note that position is ignored at this point, adjacent? will need to do the job
  ;; this fn does not do much and could be avoided... but might be more complex later
  (try
    (filter pattern-filter stash)
    (catch Exception e
      (throw (Exception. "@Look-up token")))))


;; Rules parsing

(defn pattern-fn
  "Makes a pattern function from the pattern slice (regex...)"
  [pattern]
  (cond
    (instance? java.util.regex.Pattern pattern)
    (fn [stash position]
      (lookup-re pattern stash position))

    (map? pattern)
    (fn [stash position]
      (lookup-token #(util/hash-match pattern %) stash))

    (fn? pattern)
    (fn [stash position]
      (lookup-token pattern stash))

    :else (throw
           (Exception. (str "Unable to parse pattern: " (prn-str pattern) " class:" (class pattern))))))


(defn rule-reader
  [{:keys [name pattern production]}]
  ;; (println (format "rule-reader: name = %s, pattern = %s, production = %s" name pattern production))
  (let [duckling-helper-ns (the-ns 'clj-duckling.dims.time.prod)
        p (binding [*ns* duckling-helper-ns] (eval (read-string pattern)))
        p-vec (if (vector? p) p [p])
        prod (read-string production)]
    {:name name
     :pattern (map pattern-fn p-vec)
     :production (binding [*ns* duckling-helper-ns]
                   (eval `(fn ~(vec (map #(symbol (str "%" %))
                                         (range 1 (inc (count p-vec)))))
                            ~prod)))}))



(def edn-readers {'clj-duckling.engine/rule rule-reader})

(defn read-rules-file
  "Read rules from a file

  Args:
  rules-file (string): filename path
  logger (Logger): logger

  Returns:
  (map): a Rules map {:context {}, :tests []}"
  [rules-file logger]
  (log logger :debug ::read-rules-file {:file rules-file})
  (edn/read-string {:readers edn-readers} (slurp (io/as-file rules-file))))

(defrecord EdnEngine [id rules language dirpath logger]
  eng/Engine
  (load-rules! [this]
    (log @logger :debug ::load-rules {:path dirpath :lang language})
    (let [grammar-matcher (.getPathMatcher
                           (java.nio.file.FileSystems/getDefault)
                           "glob:*.{edn}")
          xf (comp
              (filter #(.isFile ^File %))
              (filter #(.matches grammar-matcher (.getFileName (.toPath ^File %))))
              (map #(.getAbsolutePath ^File %))
              (map #(read-rules-file % @logger)))]
      (reset! rules (flatten (into [] xf (file-seq (io/file dirpath)))))))
  (get-rules [this] @rules))

(extend EdnEngine
  core/Module
  core/default-module-impl)

(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language dirpath logger]} spec
        engine (->EdnEngine id (atom nil) language dirpath (atom nil))]
    (log logger :info ::init {:id id :lang language :dirpath dirpath })
    (core/set-logger! engine logger)
    (eng/load-rules! engine)
    engine))
