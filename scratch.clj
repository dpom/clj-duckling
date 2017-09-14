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


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 lei" [:budget :age])

(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste cam 200 lei" [:amount-of-money])


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 de lei" [:amount-of-money])


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:amount-of-money])

(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:budget :amount-of-money])


(d/play :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste peste 300 de lei" [:budget :amount-of-money])

(def rorules (:ro$core @d/rules-map))
(count rorules)
(count (distinct rorules))
(first rorules)
(second rorules)
(count (map :name rorules))

(count (into #{} (map :name rorules)) )

{:index 51,
 :unit "RON",
 :dim :budget,
 :rule {:name "min budget",
         :pattern (#function[duckling.engine/pattern-fn/fn--26004]
                   #function[duckling.engine/pattern-fn/fn--26010]),
         :production #function[duckling.dims.time.prod/eval31017/fn--31018]},
 :value 300,
 :start 65,
 :pos 65,
 :route [{:pos 65, :end 70, :text "peste", :groups [nil]}
         {:dim :amount-of-money,:value 300,:unit "RON",:fields {nil nil},:text "300 de lei",:pos 71,:end 81,:rule {:name "<amount> de <unit>",
                                                                                                                   :pattern (#function[duckling.engine/pattern-fn/fn--26010]
                                                                                                                             #function[duckling.engine/pattern-fn/fn--26004]
                                                                                                                             #function[duckling.engine/pattern-fn/fn--26010]),
                                                                                                                   :production #function[duckling.dims.time.prod/eval31183/fn--31184]},
          :route [{:dim :number,:integer true,:value 300,:text "300",:pos 71,:end 74,:rule {:name "integer (numeric)",
                                                                                            :pattern (#function[duckling.engine/pattern-fn/fn--26004]),
                                                                                            :production #function[duckling.dims.time.prod/eval31226/fn--31227]},:route [{:pos 71, :end 74, :text "300", :groups ["300"]}]} {:pos 75, :end 77, :text "de", :groups []}
                  {:dim :unit,:unit "RON",:text "lei",:pos 78,:end 81,:rule {:name "RON",
                                                                             :pattern (#function[duckling.engine/pattern-fn/fn--26004]),:production #function[duckling.dims.time.prod/eval30873/fn--30874]},
                   :route [{:pos 78, :end 81, :text "lei", :groups ["lei"]}]}]}],
 :level "min",
 :label :budget,
 :end 81,
 :body "peste 300 de lei",
 :text "peste 300 de lei"
}


(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani care sa coste maxim 300 lei" [:budget :age :gender])


(d/parse :en$core "the car is 2 meters long and costs 3000$." [:distance :amount-of-money])

