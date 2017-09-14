(ns duckling.dims.volume
  (:require
   [duckling.engine :refer [export-value]]
   ))


(defmethod export-value :volume [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})
