(ns clj-duckling.dims.money
  (:require
   [clj-duckling.util.engine :refer [export-value]]))

(defmethod export-value :amount-of-money [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})

(defmethod export-value :budget [{:keys [value unit level] :as token} _]
  {:type "value" :value value :unit unit :level level})
