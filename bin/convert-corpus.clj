#!/usr/bin/env inlein

;; syntax: convert dir dirpath

'{:dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [cljfmt "0.5.7"]]}

(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.pprint :as pp])
(require '[cljfmt.core :refer [reformat-string]])

(defn convert-form [c]
  (case c
    \" "\\\""
    \( "\"("
    \)  ")\""
    nil))

(defn convert-context [x]
  (-> x
      prn-str
      (str/replace #"\(time/t" "#clj-duckling/time (t")
      (str/escape convert-form)))


(defn convert-file
  [infile outfile]
  (let [items (->> (read-string (slurp infile))
                   (partition-by (fn [x] (or (str/starts-with? x "(") (str/starts-with? x "{"))))
                   ;; (map build-rule)
                   )]
    (with-open [w (io/writer outfile)]
      (.write w "{\n")
      (doseq [item items]
        ;; (printf "item: %s\n" item)
        (let [i (first item)]
          (cond
            (str/starts-with? i "{") (do
                                       (.write w ":context ")
                                       (.write w (convert-context i))
                                       (.write w "\n :tests ["))
            (str/starts-with? i "(") (do
                                       (.write w (format ":checks [#clj-duckling/corpus %s]}\n" (str/escape (prn-str i) convert-form))))
            true                     (do
                                        (.write w "{:text ")
                                        (.write w (prn-str (vec item))))
            )))
      (.write w "\n]}"))
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
