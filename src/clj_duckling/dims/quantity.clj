(ns clj-duckling.dims.quantity
  (:require
   [clj-duckling.engine :refer [export-value]]))

(defmethod export-value :quantity [token _]
  (select-keys token [:value :unit :product]))
