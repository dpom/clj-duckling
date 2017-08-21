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

