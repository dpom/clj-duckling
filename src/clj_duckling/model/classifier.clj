(ns clj-duckling.model.classifier
  "The classifier model"
  (:require
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [clj-duckling.learn :as learn]
   [clj-duckling.engine.core :as eng]
   [clj-duckling.corpus.core :as corp]
   [clj-duckling.model.core :as core]))

(def ukey
  "this unit key"
  :clj-duckling.model/classifier)



(defrecord ClassifierModel [id classifier language rules corpus logger]
  core/Model
    (load-model! [this]
      (log @logger :error ::load-model {:error :not-implemented :id id}))
    (train-model! [this]
      (reset! classifier (learn/train-classifiers
                          (corp/get-corpus corpus)
                          (eng/get-rules rules)
                          learn/extract-route-features
                          @logger)))
    (save-model! [this]
      (log @logger :error ::save-model {:error :not-implemented :id id}))
    (get-model [this] @classifier)
    (get-id [this] id)
    (set-logger! [this newlogger] (reset! logger newlogger)))


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language rules corpus logger]} spec
        classifier (->ClassifierModel id (atom nil) language rules corpus (atom nil))]
    (log logger :info ::init {:id id :lang language :rules (eng/get-id rules) :corpus (corp/get-id corpus)})
    (core/set-logger! classifier logger)
    (core/train-model! classifier)
    classifier))
