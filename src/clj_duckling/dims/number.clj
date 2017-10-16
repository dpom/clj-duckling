(ns clj-duckling.dims.number
  (:require
   [clj-duckling.engine :refer [export-value]]))

(defmethod export-value :number [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})
