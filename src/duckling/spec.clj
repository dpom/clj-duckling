(ns duckling.spec
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as t]
   ))


(s/def :duckling/module-key (s/and keyword? #(re-matches #"[a-z]{2}\$core" (name %))))

;; rules
(s/def :duckling/name string?)
(s/def :duckling/pattern (s/coll-of t/function? :distinct true))
(s/def :duckling/production t/function?)
(s/def :duckling/rule (s/keys :req-un [:duckling/name :duckling/pattern :duckling/production]))
(s/def :duckling/rules-map (s/map-of :duckling/module-key (s/coll-of :duckling/rule)))

;; corpus
(s/def :duckling/text (s/coll-of string? :min-count 1))
(s/def :duckling/checks (s/coll-of t/function? :min-count 1 :distinct true))
(s/def :duckling/test (s/keys :req-un [:duckling/text :duckling/checks]))
(s/def :duckling/tests (s/coll-of :duckling/test))
(s/def :duckling/grain keyword?)
(s/def :duckling/start #(= org.joda.time.DateTime (class %)))
(s/def :duckling/context-key (s/and keyword? #(re-matches #"reference-time|min|max" (name %))))
(s/def :duckling/datetime (s/keys :req-un [:duckling/start :duckling/grain]))
(s/def :duckling/context (s/map-of :duckling/context-key :duckling/datetime :min-count 0))
(s/def :duckling/corpus (s/keys :req [:duckling/context :duckling/tests]))
(s/def :unq/corpus (s/keys :req-un [:duckling/context :duckling/tests]))
(s/def :duckling/corpus-map (s/map-of :duckling/module-key :duckling/corpus))
(s/def :unq/corpus-map (s/map-of :duckling/module-key :unq/corpus))
