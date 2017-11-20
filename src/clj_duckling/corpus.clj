(ns clj-duckling.corpus
  (:use
   [plumbing.core :except [millis]])
  (:require
   [clj-duckling.dims.time.obj :as time]
   [clj-duckling.util :as util]))

                                        ; Checker functions return *nil* when OK, or [expected actual] when not OK

(defn- vec->date-and-map
  "Turns a vector of args into a date and a map of extra fields"
  [args]
  (let [[date-fields other-keys-and-values] (split-with integer? args)
        token-fields (into {} (map vec (partition 2 other-keys-and-values)))
        date (-> (apply time/t -2 date-fields)
                 (?> (:grain token-fields) (assoc :grain (:grain token-fields)))
                 (?> (:timezone token-fields) (assoc :timezone (:timezone token-fields))))]
    [date token-fields]))

(defn datetime
  "Creates a datetime checker function to check if the token is valid"
  [& args]
  (let [[date token-fields] (vec->date-and-map args)]
    (fn [context token]
      (when-not
       (and
        (= :time (:dim token))
        (util/hash-match (select-keys token-fields [:direction :precision])
                         token)
        (= (:value token) date))
        [date (:value token)]))))

(defn datetime-interval
  "Creates a datetime interval checker function"
  [from to]
  (let [[start start-fields] (vec->date-and-map from)
        [end end-fields] (vec->date-and-map to)
        date (time/interval start end)]
    (fn [context {:keys [value dim] :as token}]
      (when-not
       (and
        (= :time dim)
        (= value date))
        [date value]))))





(defn corpus
  "Parse corpus" ;; TODO should be able to load several files, like rules
  [forms]
  (-> (fn [state [head & more :as forms] context tests]
        (if head
          (case state
            :init (cond (map? head) (recur :test-strings more
                                           head
                                           (conj tests {:text [], :checks []}))
                        :else (throw (Exception. (str "Invalid form at init state. A map is expected for context:" (prn-str head)))))

            :test-strings (cond (string? head) (recur :test-strings more
                                                      context
                                                      (assoc-in tests
                                                                [(dec (count tests)) :text (count (:text (peek tests)))]
                                                                head))
                                (fn? head) (recur :test-checks forms
                                                  context
                                                  tests)
                                :else (throw (Exception. (str "Invalid form at test-strings state: " (prn-str head)))))

            :test-checks (cond (fn? head) (recur :test-checks more
                                                 context
                                                 (assoc-in tests
                                                           [(dec (count tests)) :checks (count (:checks (peek tests)))]
                                                           head))
                               (string? head) (recur :test-strings forms
                                                     context
                                                     (conj tests {:text [], :checks []}))
                               :else (throw (Exception. (str "Invalid form at test-checks stats:" (prn-str head))))))
          {:context context, :tests tests}))
      (apply [:init forms [] []])))

(defmacro this-ns "Total hack to get ns of this file at compile time" [] *ns*)

(defn read-corpus
  "Read corpus from a file

  Args:
  new-file (string): file path

  Returns:
  (map): a Corpus map {:context {}, :tests []}"
  [new-file]
  (let [symbols (read-string (slurp new-file))]
    (corpus (map #(binding [*ns* (this-ns)] (eval %)) symbols))))
