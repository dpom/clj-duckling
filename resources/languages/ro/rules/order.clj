(
 "last order"
 #"(?i)ultima( mea)? comand[aă]"
{:dim :order :value -1}

 "order"
 [#"(?i)comand[aă]" (integer 0)]
 {:dim :order :value (:value %2)}
 )
