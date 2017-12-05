(ns clj-duckling.tool.duckling
  "The duckling tool"
  (:require
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [nlpcore.spec :as nsp]
   [nlpcore.protocols :as core]
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
  core/default-module-impl)


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id rules model logger]} spec
        tool (->DucklingTool id model rules (atom nil))]
    (log logger :info ::init {:id id :rules (core/get-id rules) :model (core/get-id model)})
    (core/set-logger! tool logger)
    tool))

