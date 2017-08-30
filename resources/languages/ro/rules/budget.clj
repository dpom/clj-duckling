(
"max budget"
[#"(?i)sub|max(\.|im)?" (dim :amount-of-money)]
 {:dim :budget
  :value {:value (:value %2)
          :unit (:unit %2)
          :level "max"}}

"min budget"
[#"(?i)peste|min(\.|im)?" (dim :amount-of-money)]
 {:dim :budget
  :value {:value (:value %2)
          :unit (:unit %2)
          :level "min"}}
)

