(ns clj-duckling.tool.duckling
  "The duckling tool"
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer :all]
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [nlpcore.spec :as nsp]
   [nlpcore.protocols :as core]
   [clj-duckling.spec :as spec]
   [clj-duckling.system :as sys]
   [clj-duckling.engine.core :as eng]
   [clj-duckling.util.analyze :as anlz])
  )

(def ukey
  "this unit key"
  :clj-duckling.tool/duckling)


(defrecord DucklingTool [id model rules logger]
  core/Tool
  (build-tool! [this]
    (log @logger :error ::build-tool! {:error :not-implemented :id id}))
  (apply-tool [this text opts]
    (anlz/parse text (get opts :dims []) (get opts :context {}) (core/get-model model) (eng/get-rules rules) @logger)))

(extend DucklingTool
  core/Module
  (merge core/default-module-impl
         {:get-features (fn [{:keys [model rules]}] (merge (core/get-features model)
                                                           (core/get-features rules)
                                                           {:type :entity-extractor}))}))

(defmethod ig/pre-init-spec ukey [_]
  (nsp/known-keys :req-un [:nlpcore/id
                           :nlpcore/model
                           :engine/rules
                           :nlpcore/logger]))

(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id rules model logger]} spec
        tool (->DucklingTool id model rules (atom nil))]
    (log logger :info ::init {:id id :rules (core/get-id rules) :model (core/get-id model)})
    (core/set-logger! tool logger)
    tool))

(deftest tool-test
  (let [tool (sys/get-test-module "test/config_tool.edn" ukey)]
    (testing "get-features"
      (is (=  {:entities #{:email :timezone :cycle :phone-number :number :unit
                           :leven-unit :time :unit-of-duration :leven-product
                           :duration :ordinal :volume :url :amount-of-money :budget
                           :order :gender :distance :quantity :temperature},
               :language "ro",
               :type :entity-extractor}
             (core/get-features tool))))
    (testing "apply-tool with dims"
      (is (= [{:dim :gender,
               :body "baiat",
               :value {:value :male},
               :start 37,
               :end 42}
              {:dim :duration,
               :body "5 ani",
               :value
               {:year 5,
                :value 5,
                :unit :year,
                :normalized {:value 157766400, :unit "second"}},
               :start 43,
               :end 48}
              {:dim :budget,
               :body "sub 300 lei",
               :value
               {:type "value", :value 300, :unit "RON", :level :max},
               :start 15,
               :end 26}]
             (core/apply-tool tool "vreau un cadou sub 300 lei pentru un baiat 5 ani" {:dims [:gender :duration :budget]}))))
    (testing "apply-tool without dims"
      (is (= [{:dim :phone-number,
            :body "123456789",
            :value {:value "123456789"},
            :start 26,
            :end 35}
           {:dim :number,
            :body "123456789",
            :value {:type "value", :value 123456789, :unit nil},
            :start 26,
            :end 35}
           {:dim :temperature,
            :body "123456789",
            :value {:type "value", :value 123456789, :unit nil},
            :start 26,
            :end 35,
            :latent true}
           {:dim :distance,
            :body "123456789",
            :value {:type "value", :value 123456789, :unit nil},
            :start 26,
            :end 35,
            :latent true}
           {:dim :volume,
            :body "123456789",
            :value {:type "value", :value 123456789, :unit nil},
            :start 26,
            :end 35,
            :latent true}
           {:dim :order,
            :body "comanda 123456789",
            :value {:value 123456789},
            :start 18,
            :end 35}]

             (core/apply-tool tool "informatii despre comanda 123456789" {:dims []}))))
    ))
