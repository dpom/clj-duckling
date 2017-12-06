(ns clj-duckling.dims.temperature
  (:require
   [clj-duckling.util.engine :refer [export-value]]))

(defmethod export-value :temperature [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})
