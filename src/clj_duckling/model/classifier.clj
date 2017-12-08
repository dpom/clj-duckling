(ns clj-duckling.model.classifier
  "The classifier model"
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [taoensso.nippy :as nippy]
   [duct.logger :refer [log]]
   [nlpcore.spec :as nsp]
   [nlpcore.protocols :as core]
   [clj-duckling.spec :as spec]
   [clj-duckling.system :as sys]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.util.analyze :as anlz]
   [clj-duckling.engine.core :as eng]
   ))

(def ukey
  "this unit key"
  :clj-duckling.model/classifier)



(defrecord ClassifierModel [id classifier dims language rules corpus binfile logger]
  core/Model
  (load-model! [this]
    (log @logger :debug ::load-model {:file binfile :id id})
    (let [model (nippy/thaw-from-file binfile)]
      (reset! classifier (:classifier model))
      (reset! dims (:dims model))))
  (train-model! [this]
    (log @logger :debug ::train-model {:id id :corpus (core/get-id corpus) :rules (core/get-id rules)})
    (let [corp (core/get-corpus corpus)
          engine (eng/get-rules rules)]
      (reset! classifier (learn/train-classifiers
                          corp
                          engine
                          learn/extract-route-features
                          @logger))
      (reset! dims (anlz/get-dims corp @classifier engine @logger))))
  (save-model! [this]
    (log @logger :debug ::save-model {:file binfile :id id})
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
    (log logger :info ::init {:id id :lang language :loadbin? loadbin?})
    (core/set-logger! classifier logger)
    (if loadbin?
      (core/load-model! classifier)
      (core/train-model! classifier))
    classifier))



(deftest classifier-test
  (testing "dims"
    (is (= #{:email :timezone :cycle :phone-number :number :unit
             :leven-unit :time :unit-of-duration :leven-product
             :ordinal :volume :url :amount-of-money :budget :order
             :gender :distance :quantity :temperature}
           @(:dims (sys/get-test-module "test/config_classifier1.edn" ukey)))))
  (testing "save/load"
    (let [model1 (sys/get-test-module "test/config_classifier2.edn" ukey)]
      (core/save-model! model1)
      (let [model2 (sys/get-test-module "test/config_classifier2b.edn" ukey)]
        (is (= @(:dims model1) @(:dims model2)) "test binloading dims")
        (is (= @(:classifier model1) @(:classifier model2)) "test binloading classifier")))))
