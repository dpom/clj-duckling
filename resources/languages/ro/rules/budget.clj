(
"max budget"
[#"(?i)sub|max(\.|im)?" (dim :amount-of-money)]
 {:dim :budget
  :value (:value %2)
  :unit (:unit %2)
  :level "max"}
;; (assoc %2 :dim :budget :level "max")

"min budget"
[#"(?i)peste|min(\.|im)?" (dim :amount-of-money)]
(assoc %2 :dim :budget :level "min")
)

