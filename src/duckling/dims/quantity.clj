(ns duckling.dims.quantity
  (:require
   [duckling.engine :refer [export-value]]))

(defmethod export-value :quantity [token _]
  (select-keys token [:value :unit :product]))
