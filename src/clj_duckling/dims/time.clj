(ns clj-duckling.dims.time
  (:require
   [plumbing.core :as p]
   [clj-duckling.util.time :as t]
   [clj-duckling.util.engine :refer [export-value resolve-token]]))

(defn export-time-value
  [{:keys [start end grain] :as value} direction date-fn]
  (cond
    (#{:before :after} direction)
    (case direction
      :before {:type "interval"
               :to   {:value (date-fn start)
                      :grain grain}}
      :after  {:type "interval"
               :from {:value (date-fn start)
                      :grain grain}})
    end
    {:type "interval"
     :from {:value (date-fn start)
            :grain grain}
     :to   {:value (date-fn end)
            :grain grain}}
    :else
    {:type "value"
     :value (date-fn start)
     :grain grain}))

;; Given a token, returns its value for the outside world. Datetimes are modified by date-fn."
;; TEMP 'values' hold several hypotheses
(defmethod export-value :time
 [{:keys [dim value direction values] :as token} {:keys [date-fn] :as opts}]
  (let [date-fn (or date-fn str)]
    (when value
      (assoc
       (export-time-value value direction date-fn)
       :values
       (map #(export-time-value % direction date-fn) values)))))


(defmethod resolve-token :time 
 [{:keys [dim pred not-immediate timezone] :as token} {:keys [reference-time] :as context}]
  ;; Turns a token into a list of actual possible time values.
  ;;   Behavior depends on the ref-time in context, and token fields like
  ;;   :not-immediate.
  (try
    ;; we use ref-time twice
    ;; as the first arg of pred, it's just as a lookup starting point
    (let [reference-time (or reference-time (t/now))
          ctx (assoc context :max (t/plus reference-time :year 2000)
                     :min (t/minus reference-time :year 2000))
          [[first-ahead second-ahead :as all-ahead] [first-behind]] (pred reference-time ctx)
          ahead (if (and not-immediate (t/intersect first-ahead reference-time))
                  second-ahead
                  first-ahead)]
      (->> (vector ahead first-behind)
           (remove nil?)
           ;; FIXME use timezone in resolution instead of just adding the field
           (p/?>> timezone (map #(assoc % :timezone timezone)))
           (map #(assoc token :value %))
           ;; TEMP also assoc a 'values' key with the 3 future hypotheses
           ;; this key will be used in api/export-value
           (map #(assoc % :values (take 3 all-ahead)))
           first))
    (catch Throwable e
      (throw (ex-info (format "Error %s while resolving %s" (str e) (dissoc token :route)) {})))))

