(ns clj-duckling.model.corpus
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [duct.logger :refer [log Logger]]
   [clj-duckling.model.core :as mdl]))


(defn read-corpus
  "Read corpus from a file

  Args:
  new-file (string): file path

  Returns:
  (map): a Corpus map {:context {}, :tests []}"
  [new-file]
  (let [symbols (read-string (slurp new-file))]
    (corpus (map #(binding [*ns* (this-ns)] (eval %)) symbols))))


(def train-corpus [trainpath]
  (->> (file-seq (io/as-file trainpath))
       )
  )

(defrecord Corpus [id model language trainpath binfile logger]
  modl/Model
  (load-model! [this]
    (log @logger :debug ::load-model {:file binfile})
    (log @logger :error ::not-implemented))
  (train-model! [this]
    (log @logger :debug ::train {:path trainpath :lang language})
    (reset! model ))
  (save-model! [this]
    (log @logger :debug ::save-model! {:file binfile})
    (.serialize ^DoccatModel @model (io/as-file binfile)))
  (get-model [this]
    @model)
  (get-id [this] id)
  (set-logger! [this newlogger]
    (reset! logger newlogger))
  )


(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id language binfile trainfile loadbin? logger] :or {loadbin? true}} spec
        classif (->ClassificationModel id binfile trainfile language (atom nil) (atom nil))]
    (log logger :info ::init {:id id :lang language :binfile binfile :loadbin? loadbin?})
    (modl/set-logger! classif logger)
    (if loadbin?
      (modl/load-model! classif)
      (modl/train-model! classif))
    classif))

