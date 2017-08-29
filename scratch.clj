;; use for repl

(def corpus (make-corpus "ro" ["finance"
                               "time"
                               "communication"
                               "temperature"
                               "numbers"
                               "measure"]))

(def rules (make-rules "ro"  ["finance"
                              "time"
                              "communication"
                              "temperature"
                              "numbers"
                              "measure"]))

(learn/train-classifiers corpus rules learn/extract-route-features)

(def sentences-and-check (for [test (:tests corpus)
      text (:text test)]
  [text (first (:checks test))]))

(def context {})

(def dataset {})

(def text (first (first sentences-and-check)))

(def check (second (first sentences-and-check)))

(def feature-extractor learn/extract-route-features)

(def dataset-item (learn/sentence->dataset text context check rules feature-extractor dataset))
;; {"RON" ([() true]), "<amount> <unit>" ([("integer (numeric)RON") true] [("integer (numeric)RON") true]), "integer (numeric)" ([() true] [() true])}

(load! {:languages ["ro"]})


(d/run :ro$core)

(d/play :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 lei" [:budget])


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 lei" [:budget])

(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste cam 200 lei" [:amount-of-money])


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 de lei" [:amount-of-money])


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:amount-of-money])

(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:budget :amount-of-money])


(d/play :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:budget :amount-of-money])
