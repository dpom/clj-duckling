(
 {}
 
 "barbat"
 "bărbat"
 "baiat"
 "băiat"
 "sot"
 "soţ"
 "fiu"
 "frate"
 "fecior"
 (fn [token _] (and (= :gender (:dim token))
                    (= :male (:val token))))

 "femeie"
 "muiere"
 "sotie"
 "soţie"
 "fica"
 "fetita"
 "fetiţa"
 (fn [token _] (and (= :gender (:dim token))
                    (= :female (:val token))))
 )
