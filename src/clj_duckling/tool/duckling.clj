(ns clj-duckling.tool.duckling
  "The duckling tool"
  (:require
   [integrant.core :as ig]
   [duct.logger :refer [log]]
   [clj-duckling.util.learn :as learn]
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





(defrecord DucklingTool [id model rules logger]
  core/Tool
  (build-tool! [this]
    (log @logger :error ::build-tool! {:error :not-implemented :id id}))
  (apply-tool
    ([this text dims context] text)
    ([this text dims] (core/apply-tool this text dims {}))
    ([this text] (core/apply-tool this text [] {})))
  (get-id [this] id)
  (set-logger! [this newlogger] (reset! logger newlogger)))

(defmethod ig/init-key ukey [_ spec]
  (let [{:keys [id rules model logger]} spec
        tool (->DucklingTool id model rules (atom nil))]
    (log logger :info ::init {:id id :rules (eng/get-id rules) :model (modl/get-id model)})
    (core/set-logger! tool logger)
    tool))

