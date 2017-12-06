(ns clj-duckling.dims.duration
  (:require
   [clj-duckling.util.engine :refer [export-value]]
   [clj-duckling.util.time :as t]))

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
