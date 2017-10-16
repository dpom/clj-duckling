(ns clj-duckling.dims.temperature
  (:require
   [clj-duckling.engine :refer [export-value]]))

(defmethod export-value :temperature [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})
