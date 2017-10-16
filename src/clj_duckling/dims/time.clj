(ns clj-duckling.dims.time
  (:require
   [clj-duckling.engine :refer [export-value]]))

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
(defmethod export-value :time [{:keys [dim value direction values] :as token} {:keys [date-fn] :as opts}]
  (let [date-fn (or date-fn str)]
    (when value
      (assoc
       (export-time-value value direction date-fn)
       :values
       (map #(export-time-value % direction date-fn) values)))))
