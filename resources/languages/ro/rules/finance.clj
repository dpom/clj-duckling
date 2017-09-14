(

 "RON"
 #"(?i)(roni?|lei|leu)"
 {:dim :unit
  :unit "RON"}

 "ban"
 #"(?i)(bani?)"
 {:dim :unit
  :unit "ban"}

 "$"
 #"\$|dolari?"
 {:dim :unit
  :unit "USD"}

 "€"
 #"(?i)€|([e€]uro?)"
 {:dim :unit
  :unit "EUR"}

 "£"
 #"(?i)£|lir[eaă]?"
 {:dim :unit
  :unit "GBP"}

 "USD"
 #"(?i)US[D\$]"
 {:dim :unit
  :unit "USD"}
 
 "GBP"
 #"(?i)GBP"
 {:dim :unit
  :unit "GBP"}

 "cent"
 #"(?i)cen[tț]i?|c|¢"
 {:dim :unit
  :unit "cent"}

 "<amount> <unit>"
 [(dim :number) (dim :unit)]
 {:dim :amount-of-money
  :value (:value %1)
  :unit (:unit %2)
  :fields {(:unit %1) (:value %2)}}

 "<amount> de <unit>"
 [(dim :number) #"(?i)de" (dim :unit)]
 {:dim :amount-of-money
  :value (:value %1)
  :unit (:unit %3)
  :fields {(:unit %1) (:value %3)}}


 "intersect (X lei and Y bani)" ;
 [(dim :amount-of-money #(= (:unit %) "RON")) #"(?i)([sș]i)" (dim :amount-of-money #(= (:unit %) "ban"))]
 (compose-money %1 %3)

 "intersect (X $ and Y cents)" ;
 [(dim :amount-of-money #(= (:unit %) "USD")) #"(?i)([sș]i)" (dim :amount-of-money #(= (:unit %) "cent"))]
 (compose-money %1 %3)

 "about <amount-of-money>"
 [#"(?i)cam|aprox(\.|imativ)?|aproape|[iî]n jur (de)?" (dim :amount-of-money)]
 (assoc %2 :precision "approximate")

 "exactly <amount-of-money>"
 [#"(?i)exact" (dim :amount-of-money)]
 (assoc %2 :precision "exact")



 )
