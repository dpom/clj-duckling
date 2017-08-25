(

 "intersect (X bani)" ;
 [(dim :amount-of-money) (dim :amount-of-money #(= (:unit %) "ban"))]
 (compose-money %1 %2)

 "intersect (and X bani)" ;
 [(dim :amount-of-money) #"(?i)(si|și)" (dim :amount-of-money #(= (:unit %) "ban"))]
 (compose-money %1 %3)

 "intersect" ;
 [(dim :amount-of-money) (dim :number)]
 (compose-money %1 %2)

 "intersect (and number)" ;
 [(dim :amount-of-money) #"(?i)(si|și)" (dim :number)]
 (compose-money %1 %3)

                                        ; #(not (:number-prefixed %)

 "RON"
 #"(?i)(roni?|lei)"
 {:dim :unit
  :unit "RON"} ; ambiguous

 "ban"
 #"(?i)(bani?)"
 {:dim :unit
  :unit "ban"} ; ambiguous

 "$"
 #"\$|dolari?"
 {:dim :unit
  :unit "$"} ; ambiguous

 "€"
 #"(?i)€|([e€]uro?)"
 {:dim :unit
  :unit "EUR"} ; not ambiguous

 "£"
 #"(?i)£|lire?"
 {:dim :unit
  :unit "£"}

  "USD"
 #"(?i)US[D\$]"
 {:dim :unit
  :unit "USD"}

 "GBP"
 #"(?i)GBP"
 {:dim :unit
  :unit "GBP"}

  "cent"
 #"(?i)cen[tț]i?|c|¢" ; to do:localize the corpus and rules per language
 {:dim :unit
  :unit "cent"}

  "<unit> <amount>"
 [(dim :unit) (dim :number)]
 {:dim :amount-of-money
  :value (:value %2)
  :unit (:unit %1)
  :fields {(:unit %1) (:value %2)}}

 "<amount> <unit>"
 [(dim :number) (dim :unit)]
 {:dim :amount-of-money
  :value (:value %1)
  :unit (:unit %2)
  :fields {(:unit %1) (:value %2)}}

                                        ;precision for "about $15"
 "about <amount-of-money>"
 [#"(?i)cam|aprox(\.|imativ)?|aproape|[iî]n jur (de)?" (dim :amount-of-money)]
 (assoc %2 :precision "approximate")

 "exactly <amount-of-money>"
 [#"(?i)exact" (dim :amount-of-money)]
 (assoc %2 :precision "exact")


 )
