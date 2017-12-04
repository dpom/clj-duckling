(ns clj-duckling.ml.naivebayes
  (:require
   [clojure.string :as str]
   [clojure.test :refer :all]
))

;; accessors
(defn datum-features [datum] (first datum))
(defn datum-class [datum] (second datum))

(defn- p-doc-given-class
  "Computes the probability of a doc (bag of features) for a given class
  using log probabilities.
  P(d|c) = P(x1|c) . P(x2|c) ... (Pxn|c) . P(c)"
  [bag-of-feats cla]
  (let [class-proba (:class-proba cla)
        unk-p (:unk-proba cla)
        compute-feat-proba (fn [[feat wcount]]
                             (* wcount (get-in cla [:feat-probas feat] unk-p)))
        doc-proba-given-cla (reduce + (map compute-feat-proba bag-of-feats))]
    (+ doc-proba-given-cla class-proba)))

(defn classify
  "Tries to find the most likely class by computing each score for the given
  bag of features"
  [classifier bag-of-feats]
  (let [classes (:classes classifier)
        f (fn [max-cla [cla-name cla-info]]
            (let [[_ max-proba] max-cla
                  cla-proba (p-doc-given-class bag-of-feats cla-info)]
              (if-not max-cla
                [cla-name cla-proba]
                (if (> cla-proba max-proba)
                  [cla-name cla-proba]
                  max-cla))))
        [winner p] (reduce f nil classes)]
    [winner p]))

(defn- aggregate
  "Transform the various counts into probabilities"
  [classifier]
  (let [classes (:classes classifier)
        class-infos (vals classes)
        total-docs (reduce + (map :n class-infos))
        vocab-size (count (set (mapcat #(keys (:feat-counts %)) class-infos)))
        f (fn [class-info]
            (let [counts (:feat-counts class-info)
                  total-counts (reduce + (vals counts)) ;; Laplace smoothing
                  class-proba (Math/log (/ (:n class-info) total-docs))
                  smoothed-denum (+ vocab-size total-counts)
                  feat-probas (into {}
                                    (for [[k v] counts]
                                      [k (Math/log (/ (inc v) smoothed-denum))]))
                  unk-proba (Math/log (/ 1 (inc smoothed-denum)))
                  new-map (hash-map :class-proba class-proba
                                    :feat-probas feat-probas
                                    :unk-proba unk-proba
                                    :n (:n class-info))]
              new-map))
        classes (into {} (for [[k v] classes] [k (f v)]))]
    {:classes classes}))

(defn train-classifier
  "Returns a Naive Bayes classifier.
  Accepts a dataset following:
  [[{:feat1 1 :feat2 4 :feat3 6} <class1>]
   [{:feat2 5 :feat3 1 :feat6 9} <class2>]]
  First, counts every occurrence of each feature for each class
  Then, aggregates these counts into probabilities"
  [dataset]
  (let [f (fn [c datum]
            (let [counts (datum-features datum)
                  cla (datum-class datum)
                  c (update-in c [:classes cla :n] #(inc (or % 0)))
                  c (update-in c [:classes cla :feat-counts]
                               #(merge-with + % counts))]
              c))
        c (reduce f {} dataset)] ;; create classifier with simple counts
    (aggregate c)))

(defn top10-classes [classifier]
  (->> (:classes classifier)
       (into [])
       (sort-by #(-> % val :n) (comp - compare)) ;; descending
       (map key)
       (take 10)))

;; tests
(def text-dataset [["chinese beijing chinese" "c"]
                   ["chinese chinese shangai" "c"]
                   ["chinese macao" "c"]
                   ["tokyo japan chinese" "j"]])

(defn count-words
  [text]
  (-> text
      (str/split #"\s")
      (->> (remove empty?))
      (->> (map str/lower-case))
      frequencies))

(def text-classifier
  (train-classifier
   (map #(vector (count-words (first %)) (second %)) text-dataset)))

(def gender-classifier
  (train-classifier [[{:height 6    :weight 180 :foot-size 12} "male"]
                     [{:height 5.92 :weight 190 :foot-size 11} "male"]
                     [{:height 5.58 :weight 170 :foot-size 12} "male"]
                     [{:height 5.92 :weight 165 :foot-size 10} "male"]
                     [{:height 5    :weight 100 :foot-size 6} "female"]
                     [{:height 5.5  :weight 150 :foot-size 8} "female"]
                     [{:height 5.42 :weight 130 :foot-size 7} "female"]
                     [{:height 5.75 :weight 150 :foot-size 9} "female"]]))

(deftest classify-test
  (testing "classify gender"
    (is (= ["female" -56.20678430840421]
           (classify gender-classifier {:height 6 :weight 130 :foot-size 8})))
    (is (= "male"
           (first (classify gender-classifier {:height 6 :weight 170 :foot-size 10})))))
  (testing "classify word"
    (is (= ["c" -8.245676055817812]
           (classify text-classifier (count-words "chinese chinese chinese tokyo japan"))))))

