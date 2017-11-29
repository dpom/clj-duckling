(ns clj-duckling.util.learn
  (:require
   [clojure.set :as sets]
   [clj-time.core :as t]
   [duct.logger :refer [log]] 
   [clojure.pprint :refer [pprint]]
   [clj-duckling.util.engine :as engine]
   [clj-duckling.ml.naivebayes :as naive]
   [clj-duckling.util.core :as util]))

(defn extract-route-features
  "Extracts names of previous routes used to produce this route token.
   This is the feature extractor we use."
                                        ; FIXME the grain feature should be moved to the time module
  [token]
  ;; (log/debugf "token: %s" token)
  (let [rules (reduce str (map #(get-in % [:rule :name]) (:route token)))
        time-tokens (filter #(= :time (:dim %)) (:route token))
        grains (when (pos? (count time-tokens))
                 (reduce str (map #(-> % :pred meta :grain) time-tokens)))]
    (filter identity [rules grains])))

(defn simple-feature-extractor
  "A very simple one to show if it works. Not used for now.
  Takes a token, returns a vector of features
  (can be anything as long as the model understands it)."
  [token]
  [(:text token)])

(defn subtokens
  "Get a set of all the tokens in the tree who eventually produced the given token
  (including token itself)"
  [token]
  (reduce (fn [prior-set tok]
            (sets/union prior-set (subtokens tok))) #{token} (:route token)))

(defn sentence->dataset
  "Enriches the dataset

  Args:
    s (string): a sentence
    context (map): the context
    check (func): fn that determines if a winner is valid
    rules (map):
    feature-extractor (func):
    dataset (vector): the existing dataset

  Returns:
    vector: an enriched dataset [{<rule-name> [features, output]}]
          Output is true if the rule was contributing
          successfully, false otherwise"
  [s context check rules feature-extractor dataset logger]
  (let [fc-tokens (->> (engine/pass-all s rules logger)
                       (filter #(and (:pos %) (= (count s) (- (:end %) (:pos %)))))
                       (mapcat #(engine/resolve-token % context)) ; fully-covering tokens
                       (map #(assoc % :check (check context %))))
        fc-tokens-ok (remove :check fc-tokens)
        fc-tokens-ko (filter :check fc-tokens)
        found     (seq fc-tokens-ok)
        _ (when-not found (prn "not found" s))
        tokens-ok (apply sets/union
                         (for [tok fc-tokens-ok]
                           ;; all subtokens of OK fully covering tokens which have a :rule
                           (sets/select :rule (subtokens tok))))
        tokens-ko (sets/difference
                       (apply sets/union
                              (for [tok fc-tokens-ko]
                                ;; remove OK tokens from KO set
                                (sets/select :rule (subtokens tok))))
                       tokens-ok)
        f (fn [ds tok]
            (update-in ds [(-> tok :rule :name)]
                       #(conj % [(feature-extractor tok) true])))
        dataset-updated-with-positives (reduce f dataset tokens-ok)
        f2 (fn [ds tok]
             (update-in ds [(-> tok :rule :name)]
                        #(conj % [(feature-extractor tok) false])))
        final-dataset (reduce f2 dataset-updated-with-positives tokens-ko)]
    #_(when (= s "de 9h30 jusqu'à 11h jeudi")
        (prn (count fc-tokens-ok) (count fc-tokens-ko))
        (doseq [t tokens-ok]
          (prn (-> t :rule :name) (extract-route-features t))))
    final-dataset))

(defn corpus->dataset
  "Takes a corpus and a feature extractor and builds a dataset (phase 1.a. on clj-duckling.md)."
  [{:keys [context tests] :as corpus} rules feature-extractor logger]
  (let [sentences-and-check (for [test tests
                                  text (:text test)]
                              [text (first (:checks test))])]
    (reduce (fn [dataset [t c]]
              (log logger :debug ::corpus->dataset {:t t :c c})
              (sentence->dataset t context c rules feature-extractor dataset logger))
            {} ;; initial dataset
            sentences-and-check)))

(defn print-dataset
  "Print dataset to STDOUT"
  [dataset]
  (doseq [[rule-name data] dataset]
    (println rule-name)
    (doseq [[text result] data]
      (println (format "  %s (%s)" text result)))))

(defn route-prob
  "Computes the _log_ prob for a route."
  [route classifiers]
  (if-let [classifier (get classifiers (-> route :rule :name))]
    (let [feat-count (-> route extract-route-features frequencies)
          [_ prob]   (naive/classify classifier feat-count)]
      (+ prob
         (reduce + (map #(route-prob % classifiers) (:route route)))))
    0))

(defn judge-ml
  "Choose the winning token using a classifier.
  Computes prob of each rule according to their routes."
  [stash classifiers]
  (when-let [candidates (->> stash
                             (util/keep-max (fn [{:keys [end pos]}]
                                              (if (some nil? [end pos])
                                                0
                                                (- end pos))))
                             (filter :pred)
                             not-empty)]
    (apply max-key #(route-prob % classifiers) candidates)))

(defn- train-rule
  "Returns a trained Naive Bayes classifier for one rule"
  [[name examples]]
  (comment [[{:feat1 1 :feat2 4 :feat3 6} class1]
            [{:feat2 5 :feat3 1 :feat6 9} class2]])
  (let [all-counts (map #(vector (frequencies (first %)) (second %)) examples)]
    (naive/train-classifier all-counts)))

(defn train-classifiers
  "Given a corpus and a set of rules, train a classifier per rule"
  [corpus rules fextractor logger]
  (log logger :debug ::train-classifiers  {:corpus-cnt (count (:tests corpus))
                                           :rules-cnt (count rules)})
  (let [dataset (corpus->dataset corpus rules fextractor logger)]
    (into {} (for [[name examples :as example] dataset]
               [name (train-rule example)]))))
