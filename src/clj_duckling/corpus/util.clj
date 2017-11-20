(ns clj-duckling.corpus.util
  "Utils functions used in corpus files"
  )

(defn number
  "check if the token is a number equal to value.
  If value is integer, it also checks :integer true"
  [value]
  (fn [_ token] (when-not
                    (and
                     (= :number (:dim token))
                     (or (not (integer? value)) (:integer token))
                     (= (:value token) value))
                  [value (:value token)])))

(defn ordinal
  [value]
  (fn [_ token] (when-not
                    (and
                     (= :ordinal (:dim token))
                     (= (:value token) value))
                  [value (:value token)])))

(defn temperature
  "Create a temp condition"
  [value' & [unit' precision']]
  (fn [_ {:keys [dim value unit precision] :as token}]
    (not (and
          (= :temperature dim)
          (= value' value)
          (= unit' unit)
          (= precision' precision)))))

(defn distance
  "Create a distance condition"
  [value' & [unit' normalized' precision']]
  (fn [_ {:keys [dim value unit normalized precision] :as token}]
    (not (and
          (= :distance dim)
          (= value' value)
          (= unit' unit)
          (= normalized' normalized)
          (= precision' precision)))))

(defn money
  "Create a amount-of-money condition"
  [value' & [unit' precision']]
  (fn [_ {:keys [dim value unit precision] :as token}]
    (not (and
          (= :amount-of-money dim)
          (= value' value)
          (= unit' unit)
          (= precision' precision)))))

(defn budget
  "Create a budget condition"
  [value' & [unit' level']]
  (fn [_ {:keys [dim value unit level] :as token}]
    (not (and
          (= :budget dim)
          (= value' value)
          (= unit' unit)
          (= level' level)))))

(defn place
  "Create a place checker"
  [pnl n]
  (fn [token context] (and
                       (= :pnl (:dim token))
                       (= n (:n token))
                       (= pnl (:pnl token)))))

(defn metric
  "Create a metric checker"
  [cat val]
  (fn [token context] (and
                       (= :unit (:dim token))
                       (= val (:val token))
                       (= cat (:cat token)))))

(defn quantity
  "Create a quantity condition"
  [value unit & [product]]
  (fn [token _] (and
                 (= :quantity (:dim token))
                 (= value (-> token :value :value))
                 (= unit (-> token :value :unit))
                 (= product (-> token :value :product)))))

(defn volume
  "Create a volume condition"
  [value unit & [normalized]]
  (fn [token _] (and
                 (= :volume (:dim token))
                 (= value (-> token :value :value))
                 (= unit  (-> token :value :unit))
                 (= normalized (-> token :value :normalized)))))

(defn integer
  "Return a func (duckling pattern) checking that dim=number and integer=true,
  optional range (inclusive), and additional preds"
  [& [min max & predicates]]
  (fn [token]
    (and (= :number (:dim token))
         (:integer token)
         (or (nil? min) (<= min (:value token)))
         (or (nil? max) (<= (:value token) max))
         (every? #(% token) predicates))))


