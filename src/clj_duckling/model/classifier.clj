(ns clj-duckling.model.classifier
  "The classifier model"
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [integrant.core :as ig]
   [taoensso.nippy :as nippy]
   [duct.logger :refer [log]]
   [nlpcore.spec :as nsp]
   [nlpcore.protocols :as core]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.util.analyze :as anlz]
   [clj-duckling.engine.core :as eng]))

(def ukey
  "this unit key"
  :clj-duckling.model/classifier)



(defrecord ClassifierModel [id classifier dims language rules corpus binfile logger]
  core/Model
    (load-model! [this]
      (log @logger :error ::load-model {:file binfile :id id})
      (let [model (nippy/thaw-from-file binfile)]
        (reset! classifier (:classifier model))
        (reset! dims (:dims model))))
    (train-model! [this]
      (let [corp (core/get-corpus corpus)
            engine (eng/get-rules rules)]
        (reset! classifier (learn/train-classifiers
                            corp 
                            engine
                            learn/extract-route-features
                            @logger))
        (reset! dims (anlz/get-dims corp @classifier engine @logger))))
    (save-model! [this]
      (log @logger :error ::save-model {:file binfile :id id})
      (nippy/freeze-to-file binfile {:classifier @classifier :dims @dims}))
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
        classifier (->ClassifierModel id (atom nil) (atom nil) language rules corpus binfile (atom nil))]
    (log logger :info ::init {:id id :lang language :rules (core/get-id rules) :corpus (core/get-id corpus)})
    (core/set-logger! classifier logger)
    (if loadbin?
      (core/load-model! classifier)
      (core/train-model! classifier))
    classifier))

 

