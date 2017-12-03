(ns clj-duckling.core
  "The main module with public API."
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as string]
   [integrant.core :as ig]
   [duct.logger :refer [log]] 
   [plumbing.core :as p]
   [environ.core :refer [env]]
   [clojure.test :refer :all]
   [clj-duckling.system :as sys]
   [clj-duckling.corpus.core :as corpus]
   clj-duckling.spec
   [clj-duckling.util.engine :as engine]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.dims.core :as dims]
   [clj-duckling.util.time :as time]
   [clj-duckling.util.core :as util]))


;; ;; for tests
;; (use-fixtures :once
;;   (fn [tests]
;;     (println "============== start core tests")
;;     (alter-var-root #'sys/system (fn [_] (ig/init (-> "test.edn"
;;                                                       sys/read-config
;;                                                       sys/prep))))
;;     (tests)
;;     (ig/halt! sys/system)
;;     (println "============== stop core tests")))

(defn default-context
  "Build a default context for testing. opt can be either :corpus or :now"
  [opt]
  {:reference-time (case opt
                     :corpus (time/t -2 2013 2 12 4 30)
                     :now (time/now))})


;;--------------------------------------------------------------------------
;; REPL utilities
;;--------------------------------------------------------------------------

(defn- print-stash
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
                  (string/join (repeat pos \space))
                  (string/join (repeat (- end pos) \-))
                  (string/join (repeat (- width end -1) \space))
                  i
                  (when-let [x (:dim tok)] (name x))
                  (when-let [x (-> tok :rule :name)] (name x))
                  (float (learn/route-prob tok classifiers))
                  (string/join " + " (mapv #(get-in % [:rule :name]) (:route tok))))
          (printf "  %s\n" (:text tok)))))))

(defn- print-tokens
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

;; (defn play
;;   "Show processing details for one sentence. Defines a 'details' function."
;;   ([module-id s]
;;    (play module-id s nil))
;;   ([module-id s targets]
;;    (play module-id s targets (default-context :corpus)))
;;   ([module-id s targets context]
;;    (let [targets (when targets (map (fn [dim] {:dim dim :label dim}) targets))
;;          {stash :stash
;;           winners :winners} (analyze s context module-id targets nil)]

;;      ;; 1. print stash
;;      (print-stash stash (get-classifier module-id) winners)

;;      ;; 2. print winners
;;      (printf "\n%d winners:\n" (count winners))
;;      (doseq [winner winners]
;;        (printf "%-25s %s %s\n" (str (name (:dim winner))
;;                                     (if (:latent winner) " (latent)" ""))
;;                (engine/export-value winner {:date-fn str})
;;                (dissoc winner :value :route :rule :pos :text :end :index
;;                        :dim :start :latent :body :pred :timezone :values)))

;;      ;; 3. ask for details
;;      (printf "For further info: (details idx) where 1 <= idx <= %d\n" (dec (count stash)))
;;      (defn details [n] (print-tokens (nth stash n) (get-classifier module-id)))
;;      (defn token [n] (nth stash n)))))

;;--------------------------------------------------------------------------
;; Configuration loading
;;--------------------------------------------------------------------------


;; (defn- get-dims-for-test
;;   [context module {:keys [text]}]
;;   (let [logger (get context :logger (util/get-default-logger))]
;;     (mapcat (fn [text]
;;             (try
;;               (->> (analyze text context module nil nil)
;;                    :stash
;;                    (keep :dim))
;;               (catch Exception e
;;                 (log logger :warn ::get-dims-fot-test {:module module :context context :text text})
;;                 [])))
;;           text)))

;; (defn get-dims
;;   "Retrieves all available dimensions for module by running its corpus."
;;   [module {:keys [context tests]}]
;;   (->> tests
;;        (pmap (partial get-dims-for-test context module))
;;        (apply concat)
;;        distinct
;;        sort))





;;--------------------------------------------------------------------------
;; Corpus running
;;--------------------------------------------------------------------------

;; (defn run-corpus
;;   "Run the corpus given in parameter for the given module.
;;   Returns a list of vectors [0|1 text error-msg]"
;;   [{context :context, tests :tests} module]
;;   (for [test tests
;;         text (:text test)]
;;     (try
;;       (let [{:keys [stash winners]} (analyze text context module nil nil)
;;             winner-count (count winners)
;;             check (first (:checks test)) ; only one test is supported now
;;             check-results (map (partial check context) winners)] ; return nil if OK, [expected actual] if not OK
;;         (if (some #(or (nil? %) (false? %)) check-results)
;;           [0 text nil]
;;           [1 text [(ffirst check-results) (map second check-results)]]))
;;       (catch Exception e
;;         [1 text (.getMessage e)]))))

;; (defn run
;;   "Runs the corpus and prints the results to the terminal."
;;   ([]
;;    (run (keys @corpus-map)))
;;   ([module-id]
;;    (loop [[md & more] (if (seq? module-id) module-id [module-id])
;;           line 0
;;           acc []]
;;      (if md
;;        (let [output (run-corpus (md @corpus-map) md)
;;              failed (remove (comp (partial = 0) first) output)]
;;          (doseq [[[error-count text error-msg] i] (map vector failed (iterate inc line))]
;;            (printf "%d FAIL \"%s\"\n    Expected %s\n" i text (first error-msg))
;;            (doseq [got (second error-msg)]
;;              (printf "    Got      %s\n" got)))
;;          (printf "%s: %d examples, %d failed.\n" md (count output) (count failed))
;;          (recur more (+ line (count failed)) (concat acc (map (fn [[_ t _]] [md t]) failed))))
;;        (defn c [n]
;;          (let [[md text] (nth acc n)]
;;            (printf "(play %s \"%s\")\n" md text)
;;            (play md text)))))))


;;--------------------------------------------------------------------------
;; The stuff below is specific to Wit.ai and will be moved out of Duckling
;;--------------------------------------------------------------------------

;; (defn- generate-context
;;   "Wit.ai internal. Will move to Wit."
;;   [base-context]
;;   (p/?> base-context
;;         (instance? org.joda.time.DateTime (:reference-time base-context))
;;         (assoc :reference-time {:start (:reference-time base-context)
;;                                 :grain :second})))

;; (defn extract
;;   "API used by Wit.ai (will be moved to Wit)
;;    targets is a coll of maps {:module :dim :label} for instance:
;;    {:module fr$core, :dim duration, :label wit$duration} to get duration results
;;    Returns a single coll of tokens with :body :value :start :end :label (=wisp) :latent"
;;   [sentence context leven-stash targets]
;;   {:pre [(string? sentence)
;;          (map? context)
;;          (:reference-time context)
;;          (vector? targets)]}
;;   (let [logger (get context :logger (util/get-default-logger))]
;;     (try
;;     (log logger :info ::extract  {:sentence sentence :targets targets})
;;     (letfn [(extract'
;;               [module targets] ; targets specify all the dims we should extract
;;               (let [module (keyword module)
;;                     pic-context (generate-context context)]
;;                 (when-not (module @rules-map)
;;                   (throw (ex-info "Unknown duckling module" {:module module})))
;;                 (->> (analyze sentence pic-context module targets leven-stash)
;;                      :winners
;;                      (map #(assoc % :value (engine/export-value % {:date-fn str})))
;;                      (map #(select-keys % [:label :body :value :start :end :latent])))))]
;;       (->> targets
;;            (group-by :module) ; we want to run each config only once
;;            (mapcat (fn [[module targets]] (extract' module targets)))
;;            vec))
;;     (catch Exception e
;;       (let [err {:e e
;;                  :sentence sentence
;;                  :context context
;;                  :leven-stash leven-stash
;;                  :targets targets}]
;;         (log logger :error ::extract-error err)
;;         [])))))
