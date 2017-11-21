#!/usr/bin/env inlein

;; syntax: convert dir dirpath

'{:dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [cljfmt "0.5.7"]]}

(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.pprint :as pp])
(require '[cljfmt.core :refer [reformat-string]])

(defn get-context-value
  [s]
  (let [xf (comp
            (map str/trim)
            (remove (fn [item] (str/starts-with? item ";")))
            ;; (map #(str/replace % #"\"" "\\\""))
            (map #(str/replace % #"\(time/t" "#clj-duckling/time \"(t"))
            (map #(str/replace % #"\)" ")\"")))]
    (into [] xf s)))

(defn get-context
  [item]
  (if (seq item)
    (str " :context " (str/join "\n" (get-context-value item)) "\n"
         " :tests [")
    ""))




(defn get-test
  [item]
  (let [els (partition-by #(str/starts-with? % "\"") item)]
    (format "{:text [%s]\n :checks [#clj-duckling/corpus \"%s\"]}\n"
            (str/join "\n" (first els))
            (str/replace (str/join "\n" (second els)) #"\"" "\\\\\"") )))

(defn convert-file
  [infile outfile]
  (let [xf (comp
            (map str/trim)
            (remove (fn [item] (str/starts-with? item ";")))
            (partition-by empty?)
            (remove (fn [item] (str/blank? (first item)))))
        items (with-open [rdr (io/reader infile)]
                (into [] xf (line-seq rdr)))]
    (with-open [w (io/writer outfile)]
      (.write w "{\n")
      (doseq [item items]
        ;; (printf "item: %s\n" item)
        (let [a (first item)]
          (.write w (cond
                      (str/starts-with? a "(")  (get-context (rest item))
                      (str/starts-with? a "{")  (get-context item)
                      (str/starts-with? a "\"") (get-test item)
                      true ""))))
      (.write w "]\n}"))
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
