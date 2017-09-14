(ns duckling.dims.duration
  (:require
   [duckling.engine :refer [export-value]]
   [duckling.dims.time.obj :as t]))

(defmethod export-value :duration [{:keys [value unit] :as token} _]
  (let [[[unit val] & more] (seq value)
        add-fields (when-not more {:value val
                                   :unit unit})]
    (merge value
           add-fields
           {:normalized {:value (try
                                  (t/period->duration value)
                                  (catch ArithmeticException e (.getMessage e)))
                         :unit "second"}})))
