(
 {}
 
 "ultima comanda"
 (fn [token _] (and (= :order (:dim token))
                    (= -1 (:value token))))

 "comanda 123456789"
 (fn [token _] (and (= :order (:dim token))
                    (= 123456789 (:value token))))
 )
