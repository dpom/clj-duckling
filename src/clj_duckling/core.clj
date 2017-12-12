(ns clj-duckling.core
  "The main unit, contains global tests and examples of library use "
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [clojure.test :refer :all]
   [clojure.spec.test.alpha :as stest]
   [clojure.spec.gen.alpha :as gen]
   [nlpcore.protocols :as core]
   [nlpcore.spec :as nsp]
   [clj-duckling.system :as sys]
   [clj-duckling.util.time :as time]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.util.analyze :as anlz]
   [clj-duckling.corpus.edn :as corp]
   [clj-duckling.engine.edn :as eng]
   [clj-duckling.engine.core :as engcore]
   [clj-duckling.model.classifier :as modl]
   [clj-duckling.tool.duckling :as tool])
  (:import
   [java.io File]))


(defn default-context
  "Build a default context for testing. opt can be either :corpus or :now"
  [opt]
  {:reference-time (case opt
                     :corpus (time/t -2 2013 2 12 4 30)
                     :now (time/now))})


(defn make-config
  "Build an integrant config map.

    Args:
  lang (string): language (iso code)
  level (key): log level

  Returns:
  (map): the integrant config map"
  [lang level]
  (let [corpus-dir (-> (str "languages/" lang "/corpus" )
                       io/resource
                       io/as-file
                       (.getAbsolutePath))
        rules-dir (-> (str "languages/" lang "/rules" )
                      io/resource
                      io/as-file
                      (.getAbsolutePath))]
    (merge (sys/make-test-logger level)
           {tool/ukey {:id (str lang "-tool-test")
                       :language lang
                       :model (ig/ref modl/ukey)
                       :rules (ig/ref eng/ukey)
                       :logger (ig/ref :duct.logger/timbre)}
            modl/ukey {:id (str lang "-model-test")
                       :language lang
                       :loadbin? false
                       :binfile (str "test/" lang "_model.bin")
                       :corpus (ig/ref corp/ukey)
                       :rules (ig/ref eng/ukey)
                       :logger (ig/ref :duct.logger/timbre)}
            corp/ukey {:id (str lang "-corpus-test")
                       :language lang
                       :dirpath corpus-dir
                       :logger (ig/ref :duct.logger/timbre)}
            eng/ukey {:id (str lang "-engine-test")
                      :language lang
                      :dirpath rules-dir
                      :logger (ig/ref :duct.logger/timbre)}
            })))

;; test corpus

(defn run-lang
  "Run the NLP modules for a specific language.

  Args:
  lang (string): language (iso code)
  level (key): log level

  Returns:
  (list): a list of vectors [0|1 text error-msg]"
  [lang level]
  (let [config-test (make-config lang level)
        system-test (ig/init (sys/prep config-test))
        tool (tool/ukey system-test)
        model (core/get-model (:model tool))
        rules (engcore/get-rules (:rules tool))
        logger @(:logger tool)
        {:keys [context tests]} (core/get-corpus (corp/ukey system-test))]
    ;; (log logger :debug ::run-lang {:model model :rules rules})
    (for [test tests
          text (:text test)]
      (try
        (let [{:keys [stash winners]} (anlz/analyze text [] context  model rules logger)
              check (first (:checks test)) ; only one test is supported now
              check-results (map (partial check context) winners)] ; return nil if OK, [expected actual] if not OK
          ;; (log logger :debug ::run-lang {:check-results check-results})
          (if (some #(or (nil? %) (false? %)) check-results)
            [0 text nil]
            [1 text [(ffirst check-results) (map second check-results)]]))
        (catch Exception e
          [1 text (.getMessage e)])))))

(s/def ::error (s/cat :type #{0 1} :text string? :msg #(or (nil? %) (vector? %))))
(s/def ::ok (s/cat :type #{0} :text string? :msg nil?))
(s/def ::errors (s/coll-of ::error :min-count 1))
(s/def ::noerrors (s/coll-of ::ok :min-count 1))

(s/fdef run-lang
        ;; :args (s/cat :lang #(re-matches #"[a-z]{2}" %) :level #{:debug :info :error})
        :args (s/cat :lang :nlpcore/language :level #{:debug :info :error})
        :ret ::noerrors)

(deftest run-lang-test
  (is (s/valid? ::noerrors (run-lang "ro" :error))))




(defn check-lang [level res lang]
  (assoc res lang (remove (comp (partial = 0) first) (run-lang lang level))))

(defn run
  "Run the NLP modules for all languages in resources/languages.

  Args:
  level (key): log level

  Returns:
  (map): a map with languages as keys and the list of errors as values"
  [level]
  (let [xf (comp
            (filter #(.isDirectory ^File %))
            (map #(.getName ^File %))
            (filter #(re-matches #"[a-z]{2}" %))
            )]
    (transduce xf (completing (partial check-lang level)) {} (file-seq (io/file "resources/languages")))))


;;--------------------------------------------------------------------------
;; REPL utilities
;;--------------------------------------------------------------------------

(defn print-stash
  "Print stash to STDOUT"
  [stash classifiers winners]
  (let [width (count (:text (first stash)))
        winners-indices (map :index winners)]
    (doseq [[tok i] (reverse (map vector stash (iterate inc 0)))]
      (let [pos (:pos tok)
            end (:end tok)]
        (if pos
          (printf "%s %s%s%s %2d | %-9s | %-25s | P = %04.4f | %.20s\n"
                  (if (some #{(:index tok)} winners-indices) "W" " ")
                  (str/join (repeat pos \space))
                  (str/join (repeat (- end pos) \-))
                  (str/join (repeat (- width end -1) \space))
                  i
                  (when-let [x (:dim tok)] (name x))
                  (when-let [x (-> tok :rule :name)] (name x))
                  (float (learn/route-prob tok classifiers))
                  (str/join " + " (mapv #(get-in % [:rule :name]) (:route tok))))
          (printf "  %s\n" (:text tok)))))))

(defn print-tokens
  "Recursively prints a tree representing a route"
  ([tokens classifiers]
   {:pre [(coll? tokens)]}
   (let [tokens (if (vector? tokens)
                  tokens
                  [tokens])
         tokens (if (= 1 (count tokens))
                  tokens
                  [{:route tokens :rule {:name "root"}}])]
     (print-tokens tokens classifiers 0)))
  ([tokens classifiers depth]
   (print-tokens tokens classifiers depth ""))
  ([tokens classifiers depth prefix]
   (doseq [[token i] (map vector tokens (iterate inc 1))]
     (let [;; determine name to display
           name (if-let [name (get-in token [:rule :name])]
                  name
                  (str "text: " (:text token)))
           p (learn/route-prob token classifiers)
           ;; prepare children prefix
           last? (= i (count tokens))
           new-prefix (if last? \space \|)
           new-prefix (str prefix new-prefix \space \space \space)]
       (when (pos? depth)
         (print (format "%s%s-- "
                        prefix
                        (if last? \` \|))))
       (println (format "%s (%s)" name p))
       (print-tokens (:route token)
                     classifiers
                     (inc depth)
                     (if (pos? depth) new-prefix ""))))))

;; ;; (defn play
;; ;;   "Show processing details for one sentence. Defines a 'details' function."
;; ;;   ([module-id s]
;; ;;    (play module-id s nil))
;; ;;   ([module-id s targets]
;; ;;    (play module-id s targets (default-context :corpus)))
;; ;;   ([module-id s targets context]
;; ;;    (let [targets (when targets (map (fn [dim] {:dim dim :label dim}) targets))
;; ;;          {stash :stash
;; ;;           winners :winners} (analyze s context module-id targets nil)]

;; ;;      ;; 1. print stash
;; ;;      (print-stash stash (get-classifier module-id) winners)

;; ;;      ;; 2. print winners
;; ;;      (printf "\n%d winners:\n" (count winners))
;; ;;      (doseq [winner winners]
;; ;;        (printf "%-25s %s %s\n" (str (name (:dim winner))
;; ;;                                     (if (:latent winner) " (latent)" ""))
;; ;;                (engine/export-value winner {:date-fn str})
;; ;;                (dissoc winner :value :route :rule :pos :text :end :index
;; ;;                        :dim :start :latent :body :pred :timezone :values)))

;; ;;      ;; 3. ask for details
;; ;;      (printf "For further info: (details idx) where 1 <= idx <= %d\n" (dec (count stash)))
;; ;;      (defn details [n] (print-tokens (nth stash n) (get-classifier module-id)))
;; ;;      (defn token [n] (nth stash n)))))



;; ;;--------------------------------------------------------------------------
;; ;; The stuff below is specific to Wit.ai and will be moved out of Duckling
;; ;;--------------------------------------------------------------------------

;; ;; (defn extract
;; ;;   "API used by Wit.ai (will be moved to Wit)
;; ;;    targets is a coll of maps {:module :dim :label} for instance:
;; ;;    {:module fr$core, :dim duration, :label wit$duration} to get duration results
;; ;;    Returns a single coll of tokens with :body :value :start :end :label (=wisp) :latent"
;; ;;   [sentence context leven-stash targets]
;; ;;   {:pre [(string? sentence)
;; ;;          (map? context)
;; ;;          (:reference-time context)
;; ;;          (vector? targets)]}
;; ;;   (let [logger (get context :logger (util/get-default-logger))]
;; ;;     (try
;; ;;     (log logger :info ::extract  {:sentence sentence :targets targets})
;; ;;     (letfn [(extract'
;; ;;               [module targets] ; targets specify all the dims we should extract
;; ;;               (let [module (keyword module)
;; ;;                     pic-context (generate-context context)]
;; ;;                 (when-not (module @rules-map)
;; ;;                   (throw (ex-info "Unknown duckling module" {:module module})))
;; ;;                 (->> (analyze sentence pic-context module targets leven-stash)
;; ;;                      :winners
;; ;;                      (map #(assoc % :value (engine/export-value % {:date-fn str})))
;; ;;                      (map #(select-keys % [:label :body :value :start :end :latent])))))]
;; ;;       (->> targets
;; ;;            (group-by :module) ; we want to run each config only once
;; ;;            (mapcat (fn [[module targets]] (extract' module targets)))
;; ;;            vec))
;; ;;     (catch Exception e
;; ;;       (let [err {:e e
;; ;;                  :sentence sentence
;; ;;                  :context context
;; ;;                  :leven-stash leven-stash
;; ;;                  :targets targets}]
;; ;;         (log logger :error ::extract-error err)
;; ;;         [])))))
