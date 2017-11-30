(ns clj-duckling.tool.duckling
  "The duckling tool"
  (:require
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [plumbing.core :as p]
   [clj-duckling.util.learn :as learn]
   [clj-duckling.util.engine :as engine]
   [clj-duckling.engine.core :as eng]
   [clj-duckling.model.core :as modl]
   [clj-duckling.util.core :as util]
   [clj-duckling.tool.core :as core])
)

(def ukey
  "this unit key"
  :clj-duckling.tool/duckling)


(defn select-winners*
  [compare-fn resolve-fn already-selected candidates]
  (if (seq candidates)
    (let [[maxima others] (util/split-by-partial-max
                           compare-fn
                           candidates
                           (concat already-selected candidates))
          new-winners (->> maxima
                           (mapcat resolve-fn)
                           (filter :value))] ; remove unresolved
      (if (seq maxima)
        (recur compare-fn resolve-fn (concat already-selected new-winners) others)
        already-selected))
    already-selected))

(defn select-winners
  "Winner= token that is not 'smaller' (in the sense of the provided partial
  order) than another winner, and that resolves to a value"
  [compare-fn log-prob-fn resolve-fn candidates]
  (->> candidates
       (map #(assoc % :log-prob (log-prob-fn %)))
       (select-winners* compare-fn resolve-fn [])
       (map #(dissoc % :log-prob))))


(defn compare-tokens
  "Compares two candidate tokens a and b for runtime selection.
  wanted-dim is a hash whose keys are the :dim wanted by the caller, the value
  can be anything truthy.
  Returns nil: not comparable 0: equal 1: greater -1: lesser"
  [a b classifiers wanted-dims]
  {:pre [(map? classifiers)]}
  (let [same-dim (= (:dim a) (:dim b))
        wanted-a (get wanted-dims (:dim a))
        wanted-b (get wanted-dims (:dim b))
        cmp-interval (util/compare-intervals
                      [(:pos a) (:end a)]
                      [(:pos b) (:end b)])] ; +1 0 -1 nil
    ;;(printf "Comparing %d and %d \n" (:index a) (:index b))
    (if-not same-dim
      ;; unless a is wanted and covers b, or the contrary, they are not comparable
      (cond (and wanted-a (= 1 cmp-interval)) 1
            (and wanted-b (= -1 cmp-interval)) -1
            :else nil)
      (if (not= 0 cmp-interval)
        cmp-interval ; one interval recovers the other
        (compare (:log-prob a) (:log-prob b))))))



(defn analyze
  "Analyze a sentence, returns the stash and a curated list of winners.

  Args:
  s (string):
  context (map):
  targets (coll): a coll of {:dim dim :label label} : only winners of these dims are
                  kept, and they receive a :label key = the label provided.
                  If no targets specified, all winners are returned.
  model (Model):
  rules (Engine):
  logger (): current logger

  Returns:
  (map): a map with 2 keys :stash and :winners
  "
  [s targets context model rules logger]
  (let [stash1 (engine/pass-all s rules logger)
        ;; add an index to tokens in the stash
        stash (map #(if (map? %1) (assoc %1 :index %2) %1)
                   stash1
                   (iterate inc 0))
        dim-label (when (seq targets) (into {} (for [{:keys [dim label]} targets]
                                                 [(keyword dim) label])))
        winners (->> stash
                     (filter :pos)
                     ;; just keep the dims we want, and add the label key
                     (p/?>> dim-label (keep #(when-let [label (get dim-label (:dim %))]
                                               (assoc % :label label))))

                     (select-winners
                      #(compare-tokens %1 %2 model dim-label)
                      #(learn/route-prob % model)
                      #(engine/resolve-token % context))

                     ;; adapt the keys for the outside world
                     (map (fn [{:keys [pos end text] :as token}]
                            (merge token {:start pos
                                          :end end
                                          :body text}))))]
    ;; (log/debugf "stash: %s" (with-out-str (clojure.pprint/pprint stash)))
    ;; (log/debugf "winners: %s" (with-out-str (clojure.pprint/pprint winners)))
    {:stash stash :winners winners}))

(defn parse
  "Parse a sentence, returns a curated list of winners.

  Args:
  s (string):
  context (map):
  dims (coll): a coll of dims, only winners of these dims are kept.
               If no targets specified, all winners are returned.
  model (Model): the tool's model (classifier)
  rules (Engine): the tool's rules (engine)
  logger (): current logger

  Returns:
  (collection): a collection of winners tokens"
  [s dims context model rules logger]
  (->> (analyze s (map (fn [dim] {:dim dim :label dim}) dims) context model rules logger)
       :winners
       (map (fn [x] (assoc x :value (engine/export-value x context))))
       (map (fn [x] (select-keys x [:dim :body :value :start :end :latent])))
       distinct))

(defrecord DucklingTool [id model rules logger]
  core/Tool
  (build-tool! [this]
    (log @logger :error ::build-tool! {:error :not-implemented :id id}))
  (apply-tool [this text opts]
    (parse text (get opts :dims []) (get opts :context {}) (modl/get-model model) (eng/get-rules rules) logger))
  (get-id [this] id)
  (set-logger! [this newlogger] (reset! logger newlogger)))

(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id rules model logger]} spec
        tool (->DucklingTool id model rules (atom nil))]
    (log logger :info ::init {:id id :rules (eng/get-id rules) :model (modl/get-id model)})
    (core/set-logger! tool logger)
    tool))

