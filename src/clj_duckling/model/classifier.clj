(ns clj-duckling.model.classifier
  "The classifier model"
  (:require
   [integrant.core :as ig]
   [taoensso.nippy :as nippy]
   [duct.logger :refer [log]]
   [clojure.spec.alpha :as s]
   [nlpcore.spec :as nsp]
   [nlpcore.protocols :as core]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.engine.core :as eng]))

(def ukey
  "this unit key"
  :clj-duckling.model/classifier)



(defrecord ClassifierModel [id classifier language rules corpus binfile logger]
  core/Model
    (load-model! [this]
      (log @logger :error ::load-model {:file binfile :id id})
      (reset! classifier (nippy/thaw-from-file binfile)))
    (train-model! [this]
      (reset! classifier (learn/train-classifiers
                          (core/get-corpus corpus)
                          (eng/get-rules rules)
                          learn/extract-route-features
                          @logger)))
    (save-model! [this]
      (log @logger :error ::save-model {:file binfile :id id})
      (nippy/freeze-to-file binfile @classifier))
    (get-model [this] @classifier))

(extend ClassifierModel
  core/Module
  core/default-module-impl)


(s/def ::loadbin? boolean?)

(defmethod ig/pre-init-spec ukey [_]
  (nsp/known-keys :req-un [:nlpcore/id
                           :nlpcore/language
                           :nlpcore/logger]
                  :opt-un [::loadbin?
                           :nlpcore/corpus
                           :engine/rules
                           :gen/binfile]))


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language rules corpus logger loadbin? binfile] :or {loadbin? false}} spec
        classifier (->ClassifierModel id (atom nil) language rules corpus binfile (atom nil))]
    (log logger :info ::init {:id id :lang language :rules (core/get-id rules) :corpus (core/get-id corpus)})
    (core/set-logger! classifier logger)
    (if loadbin?
      (core/load-model! classifier)
      (core/train-model! classifier))
    classifier))
