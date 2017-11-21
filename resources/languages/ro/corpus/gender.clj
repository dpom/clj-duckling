(
 {}
 
 "barbat"
 "bărbat"
 "baiat"
 "baietel"
 "băiat"
 "băieţel"
 "sot"
 "soţ"
 "fiu"
 "frate"
 "fecior"
 "tata"
 "bunic"
 (fn [token _] (and (= :gender (:dim token))
                    (= :male (:value token))))

 "femeie"
 "muiere"
 "sotie"
 "soţie"
 "fica"
 "fetita"
 "fetiţa"
 "mama"
 "bunica"
 (fn [token _] (and (= :gender (:dim token))
                    (= :female (:value token))))
 )
