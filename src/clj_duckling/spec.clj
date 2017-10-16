(ns clj-duckling.spec
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as t]))

(s/def :gen/module-key (s/and keyword? #(re-matches #"[a-z]{2}\$core" (name %))))
(s/def :gen/grain keyword?)
(s/def :gen/start #(= org.joda.time.DateTime (class %)))
(s/def :gen/datetime (s/keys :req-un [:gen/start :gen/grain]))

(def dimensions #{:amount-of-money
                  :budget
                  :communication :cycle
                  :distance :duration
                  :email
                  :finance
                  :gender
                  :leven-product :leven-unit
                  :measure
                  :number
                  :ordinal :order
                  :phone-number
                  :quantity
                  :temperature :time :timezone
                  :unit :unit-of-duration :url
                  :volume})
(def dimensions-str (set (map name dimensions)))

(s/def :gen/dim dimensions)
(s/def :gen/dim-str dimensions-str)
(s/def :gen/load-result (s/map-of :gen/module-key (s/coll-of :gen/dim)))

;;; config
(s/def :config/corpus (s/coll-of :gen/dim-str))
(s/def :config/rules (s/coll-of :gen/dim-str))
(s/def :config/config (s/map-of :gen/module-key (s/keys :req-un [:config/corpus :config/rules])))

;; rule
(s/def :rule/name string?)
(s/def :rule/pattern (s/coll-of t/function? :distinct true))
(s/def :rule/production t/function?)
(s/def :rule/rule (s/keys :req-un [:rule/name :rule/pattern :rule/production]))
(s/def :rule/rules (s/coll-of :rule/rule))
(s/def :rule/rules-map (s/map-of :gen/module-key :rule/rules))

;; corpus
(s/def :corpus/text (s/coll-of string? :min-count 1 :distinct false))
(s/def :corpus/checks (s/coll-of t/function? :min-count 1 :distinct true))
(s/def :corpus/test (s/keys :req-un [:corpus/text :corpus/checks]))
(s/def :corpus/tests (s/coll-of :corpus/test))
(s/def :corpus/context-key (s/and keyword? #(re-matches #"reference-time|min|max" (name %))))
(s/def :corpus/context (s/map-of :corpus/context-key :gen/datetime :min-count 0))
(s/def :corpus/corpus (s/keys :req-un [:corpus/context :corpus/tests]))
(s/def :corpus/corpus-map (s/map-of :gen/module-key :corpus/corpus))

;; route
(s/def :route/pos nat-int?)
(s/def :route/end nat-int?)
(s/def :route/text string?)
(s/def :route/fields map?)
(s/def :route/groups (s/coll-of (s/or :route/text nil?)))
(s/def :route/route (s/keys :req-un [:route/pos :route/end :route/text]))
(s/def :route/routes (s/coll-of :route/route))

;; tokens
(s/def :token/check boolean?)
(s/def :token/latent boolean?)
(s/def :token/common (s/keys :req-un [:gen/dim
                                      :route/pos :route/end :route/text
                                      :rule/rule
                                      :route/routes]
                             :opt-un [:route/fields :token/check :token/latent]))

(def hdim (-> (make-hierarchy)
              (derive :temperature :dimension)
              (derive :volume :dimension)
              (derive :distance :dimension)
              (derive :quantity :dimension)
              (derive :url :text)
              (derive :email :text)
              (derive :url :text)
              (derive :level-unit :text)
              (derive :level-product :text)))

(defmulti token-type :dim :hierarchy #'hdim)
(s/def :token/token (s/multi-spec token-type :dim))

;;; default
(s/def :number/value number?)
(defmethod token-type :default [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value])))

;; text
(s/def :text/value string?)
(defmethod token-type :text [_]
  (s/merge :token/common
           (s/keys :req-un [:text/value])))

;;; number
(s/def :number/integer boolean?)
(defmethod token-type :number [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value]
                   :opt-un [:number/integer])))

;; unit
(s/def :unit/unit string?)
(defmethod token-type :unit [_]
  (s/merge :token/common
           (s/keys :req-un [:unit/unit])))

;;; dimension
(s/def :dimension/normalized (s/keys :req-un [:number/value :unit/unit]))
(defmethod token-type :dimension [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value]
                   :opt-un [:unit/unit :dimension/normalized])))

;;; money
(defmethod token-type :amount-of-money [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value :unit/unit])))

;;; budget
(s/def :budget/level #{:min :max})
(defmethod token-type :budget  [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value :unit/unit :budget/level])))

;;; gender
(s/def :gender/value #{:male :female})
(defmethod token-type :gender [_]
  (s/merge :token/common
           (s/keys :req-un [:number/value :unit/unit :budget/level])))

;;; time
(s/def :time/pred t/function?)
(s/def :time/check (s/or :number pos-int? :datetime :gen/datetime))
(s/def :time/values coll?)
(defmethod token-type :time [_]
  (s/merge :token/common
           (s/keys :req-un [:time/check :time/pred :time/values])))

;; classifier
;; (s/def :classifier/classifier (s/cat :name string? ))
;; (s/def :classifier/classifiers-map (s/map-of ::module-key (s/col-of :classifier/classifier)))

