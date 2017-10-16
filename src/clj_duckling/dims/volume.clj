(ns clj-duckling.dims.volume
  (:require
   [clj-duckling.engine :refer [export-value]]))

(defmethod export-value :volume [{:keys [value unit] :as token} _]
  {:type "value" :value value :unit unit})
