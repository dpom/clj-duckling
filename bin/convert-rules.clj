#!/usr/bin/env inlein

;; syntax: convert-rules  dirpath

'{:dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [cljfmt "0.5.7"]]}

(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.pprint :as pp])
(require '[cljfmt.core :refer [reformat-string]])



(defn build-rule [[name pattern production]]
  {:name name
   :pattern (pr-str pattern)
   :production (pr-str production)})

(defn convert-file
  [infile outfile]
  (let [items (->> (read-string (slurp infile))
               (partition 3)
               (map build-rule))]
    (with-open [w (io/writer outfile)]
      (.write w "[\n")
      (doseq [item items]
        ;; (printf "item: %s\n" item)
        (.write w "#clj-duckling.engine/rule ")
       (.write w (prn-str item)))
      (.write w "]"))
    (spit outfile (reformat-string (slurp outfile)))))


(defn convert-dir
  [dirpath]
  (let [grammar-matcher (.getPathMatcher
                         (java.nio.file.FileSystems/getDefault)
                         "glob:*.{clj}")
        files (->> dirpath
                   io/file
                   file-seq
                   (filter #(.isFile %))
                   (filter #(.matches grammar-matcher (.getFileName (.toPath %))))
                   (map #(.getAbsolutePath %)))]
    (doseq [f files]
      (pp/pprint f)
      (convert-file f (str/replace f #"\.clj" ".edn")))))


(convert-dir (first *command-line-args*))
