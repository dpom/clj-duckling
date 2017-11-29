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

(d/parse :ro$core "vreau sa cumpar un cadou pentru un baiat de 13 ani si 2 luni care sa coste maxim 300 lei" [:budget :duration :gender])

(d/parse :ro$core "2 luni" [:duration])

(d/parse :ro$core "o zi" [:duration])


(d/parse :ro$core "vreau informatii despre ultima mea comanda" [:order])

(d/parse :ro$core "1 an" [:duration])

(require '[clojure.spec.alpha :as s])

(s/valid? :duckling/module-key :ro$core)

(s/valid? :unq/rule (first (:ro$core @d/rules-map)))
(s/explain :unq/rule (first (:ro$core @d/rules-map)))


(s/valid? :unq/rules (:ro$core @d/rules-map))

(s/valid? :unq/rules-map  @d/rules-map)
(s/valid? :unq/corpus-map  @d/corpus-map)
(s/valid? :unq/corpus  (:ro$core @d/corpus-map))
(s/valid? :duckling/tests  (:tests (:ro$core @d/corpus-map)))
(s/valid? :duckling/test  (first (:tests (:ro$core @d/corpus-map))))

(s/explain :duckling/tests  (:tests (:ro$core @d/corpus-map)))

(doc :duckling/test)


(s/explain :unq/corpus-map  @d/corpus-map)

token: {:dim :number, :integer true, :value 300, :text "300", :pos 4, :end 7, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56334/fn--56335]}, :route [{:pos 4, :end 7, :text "300", :groups ["300"]}]}

token: {:unit "RON", :check false, :dim :budget, :rule {:name "max budget", :pattern (#function[duckling.engine/pattern-fn/fn--30494] #function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval55755/fn--55756]}, :value 300, :pos 0, :route [{:pos 0, :end 3, :text "sub", :groups [nil]} {:dim :amount-of-money, :value 300, :unit "RON", :fields {nil nil}, :text "300 lei", :pos 4, :end 11, :rule {:name "<amount> <unit>", :pattern (#function[duckling.engine/pattern-fn/fn--30500] #function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval55968/fn--55969]}, :route [{:dim :number, :integer true, :value 300, :text "300", :pos 4, :end 7, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56334/fn--56335]}, :route [{:pos 4, :end 7, :text "300", :groups ["300"]}]} {:dim :unit, :unit "RON", :text "lei", :pos 8, :end 11, :rule {:name "RON", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55664/fn--55665]}, :route [{:pos 8, :end 11, :text "lei", :groups ["lei"]}]}]}], :level :max, :end 11, :text "sub 300 lei"}

token: {:dim :amount-of-money, :value 300, :unit "RON", :fields {nil nil}, :text "300 lei", :pos 4, :end 11, :rule {:name "<amount> <unit>", :pattern (#function[duckling.engine/pattern-fn/fn--30500] #function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval55968/fn--55969]}, :route [{:dim :number, :integer true, :value 300, :text "300", :pos 4, :end 7, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56334/fn--56335]}, :route [{:pos 4, :end 7, :text "300", :groups ["300"]}]} {:dim :unit, :unit "RON", :text "lei", :pos 8, :end 11, :rule {:name "RON", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55664/fn--55665]}, :route [{:pos 8, :end 11, :text "lei", :groups ["lei"]}]}]}

token: {:dim :unit, :unit "RON", :text "lei", :pos 8, :end 11, :rule {:name "RON", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55664/fn--55665]}, :route [{:pos 8, :end 11, :text "lei", :groups ["lei"]}]}

token: {:dim :temperature, :latent true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "number as temp", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval55747/fn--55748]}, :route [{:dim :number, :integer true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56334/fn--56335]}, :route [{:pos 0, :end 10, :text "0743115099", :groups ["0743115099"]}]}], :check false}

token: {:dim :phone-number, :value "0743115099", :text "0743115099", :pos 0, :end 10, :rule {:name "phone number", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55725/fn--55726]}, :route [{:pos 0, :end 10, :text "0743115099", :groups ["0743115099" nil nil]}], :check false}

token: {:dim :volume, :latent true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "number as volume", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval56218/fn--56219]}, :route [{:dim :number, :integer true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56004/fn--56005]}, :route [{:pos 0, :end 10, :text "0743115099", :groups ["0743115099"]}]}], :check false}

token: {:dim :distance, :latent true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "number as distance", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval55776/fn--55777]}, :route [{:dim :number, :integer true, :value 743115099, :text "0743115099", :pos 0, :end 10, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56334/fn--56335]}, :route [{:pos 0, :end 10, :text "0743115099", :groups ["0743115099"]}]}], :check false}

token: {:dim :url, :value "http://www.bla.com", :text "http://www.bla.com", :pos 0, :end 18, :rule {:name "local url", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55876/fn--55877]}, :route [{:pos 0, :end 18, :text "http://www.bla.com", :groups ["http://www.bla.com" "http://" nil]}], :check false}

token: {:dim :email, :value "alex@wit.ai", :text "alex@wit.ai", :pos 0, :end 11, :rule {:name "email", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55928/fn--55929]}, :route [{:pos 0, :end 11, :text "alex@wit.ai", :groups ["alex@wit.ai"]}], :check false}

token: {:dim :distance, :latent true, :value 3, :text "3", :pos 0, :end 1, :rule {:name "number as distance", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval57491/fn--57493]}, :route [{:dim :number, :integer true, :value 3, :text "3", :pos 0, :end 1, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval58407/fn--58408]}, :route [{:pos 0, :end 1, :text "3", :groups ["3"]}]}]}

token: {:dim :gender, :value :male, :text "barbat", :pos 0, :end 6, :rule {:name "male", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval55669/fn--55670]}, :route [{:pos 0, :end 6, :text "barbat", :groups []}], :check false}

token: {:normalized {:value 3000, :unit "metre"}, :unit "kilometre", :check false, :dim :distance, :rule {:name "<latent dist> km", :pattern (#function[duckling.engine/pattern-fn/fn--30500] #function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval57574/fn--57575]}, :value 3, :pos 0, :route [{:dim :distance, :latent true, :value 3, :text "3", :pos 0, :end 1, :rule {:name "number as distance", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval57491/fn--57493]}, :route [{:dim :number, :integer true, :value 3, :text "3", :pos 0, :end 1, :rule {:name "integer (numeric)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval58407/fn--58408]}, :route [{:pos 0, :end 1, :text "3", :groups ["3"]}]}]} {:pos 2, :end 12, :text "kilomètres", :groups ["ilo" "ètre"]}], :end 12, :text "3 kilomètres"}

token: {:dim :leven-unit, :value "pound", :text "pounds", :pos 5, :end 11, :rule {:name "pounds", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval59337/fn--59338]}, :route [{:pos 5, :end 11, :text "pounds", :groups []}]}

token: {:unit "pound", :check true, :dim :quantity, :rule {:name "<number> <units>", :pattern (#function[duckling.engine/pattern-fn/fn--30500] #function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval59123/fn--59124]}, :value 9, :pos 0, :route [{:dim :number, :integer true, :value 9, :text "nine", :pos 0, :end 4, :rule {:name "integer (0..19)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval58191/fn--58192]}, :route [{:pos 0, :end 4, :text "nine", :groups ["nine"]}]} {:dim :leven-unit, :value "pound", :text "pounds", :pos 5, :end 11, :rule {:name "pounds", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval59337/fn--59338]}, :route [{:pos 5, :end 11, :text "pounds", :groups []}]}], :form :no-product, :end 11, :text "nine pounds"}

token: {:dim :leven-product, :value "café", :text "café", :pos 12, :end 16, :rule {:name "café", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval58990/fn--58991]}, :route [{:pos 12, :end 16, :text "café", :groups []}]}

token: {:dim :leven-unit, :value "tasse", :text "tasses", :pos 2, :end 8, :rule {:name "tasse", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval58985/fn--58986]}, :route [{:pos 2, :end 8, :text "tasses", :groups []}]}

token: {:pred #function[clojure.lang.AFunction/1], :check [1 {:start #object[org.joda.time.DateTime 0x3ebee7ab "2001-01-01T00:00:00.000-02:00"], :grain :year}], :dim :time, :rule {:name "year (latent)", :pattern (#function[duckling.engine/pattern-fn/fn--30500]), :production #function[duckling.dims.time.prod/eval59220/fn--59221]}, :value {:start #object[org.joda.time.DateTime 0x3ebee7ab "2001-01-01T00:00:00.000-02:00"], :grain :year}, :latent true, :pos 0, :route [{:dim :number, :integer true, :value 1, :text "une", :pos 0, :end 3, :rule {:name "number (0..16)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval57488/fn--57489]}, :route [{:pos 0, :end 3, :text "une", :groups ["une"]}]}], :values (), :end 3, :text "une"}

token: {:dim :ordinal, :value 2, :text "al doilea", :pos 0, :end 9, :rule {:name "ordinals (primul..9lea)", :pattern (#function[duckling.engine/pattern-fn/fn--30494]), :production #function[duckling.dims.time.prod/eval56736/fn--56737]}, :route [{:pos 0, :end 9, :text "al doilea", :groups ["al doilea" nil "al" "doilea" nil nil nil nil nil nil nil nil nil nil nil nil nil nil]}], :check nil}


(:context (:ro$core @d/corpus-map))

{:reference-time {:start #object[org.joda.time.DateTime 0x4d655c8e "2013-02-12T04:30:00.000-02:00"], :grain :second}, :min {:start #object[org.joda.time.DateTime 0x164c7f13 "1900-01-01T00:00:00.000-02:00"], :grain :year}, :max {:start #object[org.joda.time.DateTime 0x1505a62c "2100-01-01T00:00:00.000-02:00"], :grain :year}}


(first (:tests (:ro$core @d/corpus-map)))


{:text ["sub 300 lei" "sub 300 de lei" "maxim 300 lei"], :checks [#function[clj-duckling.corpus/budget/fn--21160]]}

(require '[taoensso.nippy :as nippy])

(def frozen-corpus-map (nippy/freeze @d/corpus-map))

(file-seq (io/as-file "/home/dan/emag/clj-duckling/resources/languages/ro/corpus"))

(require '[clj-duckling.util.corpus :as corpus])

(def f (corpus/budget 300 "RON" :max))

(defn default-reader
  [t v]
  (println (format "t: %s, v:%s" t v))
  (apply (resolve t) v))

(require '[clojure.edn :as edn])

(edn/read-string {:default default-reader} (slurp (io/as-file "resources/languages/ro/corpus/budget.edn")))


(require '[clj-duckling.model.corpus :as c])
(c/read-corpus "resources/languages/ro/corpus/budget.edn")

(def filename "resources/languages/ro/corpus/communication.clj")

(def filename "resources/languages/ro/corpus/time.clj")


(require '[clojure.string :as str])

(let [xf (comp
          (map str/trim)
          (partition-by empty?)
          (remove (fn [item] (str/blank? (first item)))))
      items  (with-open [rdr (io/reader filename)]
               (into [] xf (line-seq rdr)))]
  items)


(defn build
  [corpus item]
  (printf "corpus: %s, item: %s\n" corpus item)
  (let [a (first item)
        b (rest item)]
    (cond
      (str/starts-with? a "(") (if (seq b)
                                 (assoc corpus :context (read-string (str/join b)))
                                 corpus)
      true corpus)))

(let [xf (comp
          (map str/trim)
          (remove (fn [item] (str/starts-with? item ";")))
          (partition-by empty?)
          (remove (fn [item] (str/blank? (first item)))))]
  (with-open [rdr (io/reader filename)]
    (transduce xf build {} (line-seq rdr))))

(def s ":reference-time (time/t -2 2013 2 12 4 30 0)")
(let [index (str/index-of s " ")
      k (str/join "" (take index s))
      val (str/split (str/join "" (drop-last (drop (+ index 2) s))) #"\s")]
  (format "%s #%s [%s]" k (first val) (str/join " " (rest val))))


(def s "  ; Context map
  ; Tuesday Feb 12, 2013 at 4:30am is the \"now\" for the tests
  {:reference-time (time/t -2 2013 2 12 4 30 0)
   :min (time/t -2 1900)
   :max (time/t -2 2100)}
  ")

(def s "{}")
(let [xf (comp
          (map str/trim)
          (remove (fn [item] (str/starts-with? item ";")))
          (map #(str/replace % #"\(time/t" "#time/t ["))
          (map #(str/replace % #"\)" "]"))
          )]
  (into [] xf (str/split s #"\n")))


(let [grammar-matcher (.getPathMatcher
                       (java.nio.file.FileSystems/getDefault)
                       "glob:*.{clj}")]
  (->> "resources/languages/ro/corpus"
       clojure.java.io/file
       file-seq
       (filter #(.isFile %))
       (filter #(.matches grammar-matcher (.getFileName (.toPath %))))
       (mapv #(.getAbsolutePath %))))

;; 2017-11-20

(require [clj-duckling.corpus.edn :refer [read-corpus-file]])

(def dirpath "resources/languages/ro/corpus")



(require '[clj-duckling.corpus.edn :as corpus])

(def edncorpus (get system corpus/ukey))

@(get edncorpus :corpus)
(def logger @(get edncorpus :logger))

(let [grammar-matcher (.getPathMatcher
                       (java.nio.file.FileSystems/getDefault)
                       "glob:*.{edn}")
      xf (comp
          (filter #(.isFile %))
          (filter #(.matches grammar-matcher (.getFileName (.toPath %))))
          (map #(.getAbsolutePath %))
          (map #(corpus/read-corpus-file % logger))
          )]
  (transduce xf (completing (fn [res item]
                              (fipp (format "** res: %s\n item: %s" res item))
                              (merge-with into res item)))
             {:context {} :tests []}
             (file-seq (io/file dirpath))))

;; 2017-11-21

(def t "corpus/temperature")
(def v "[37 \"celsius\"]")

(def t1 "eval")

(require '[clojure.string :as str])

(str/replace "ab \" cd" #"\"" "\\\"")

(time (def rules1 (-> "languages/ro/rules/temperature.clj"
                      io/resource
                      slurp
                      read-string
                      clj-duckling.engine/rules)))


(require '[clj-duckling.engine.edn :as eng])

(def logger (:duct.logger/timbre system)) 

(time (def rules2 (eng/read-rules-file "resources/languages/ro/rules/temperature.edn" logger)))

;; 2017-11-22

(= (first rules1) (first rules2))

(def rules3 (-> "languages/ro/rules/temperature.clj"
                io/resource
                slurp
                read-string))


(time (def rules2 (eng/read-rules-file "tmp/temperature.edn" logger)))

(get-in system [:clj-duckling.engine/edn :rules])


(require '[clj-duckling.engine.edn :as eng])

(crt/with-progress-reporting
  (crt/bench (-> "languages/ro/rules/temperature.clj"
                 io/resource
                 slurp
                 read-string
                 clj-duckling.engine/rules) :verbose))


(crt/with-progress-reporting
  (crt/bench (eng/read-rules-file "tmp/temperature.edn" logger) :verbose))

;; 2017-11-23


(require '[clj-duckling.model.classifier :as c]) 
(require '[clj-duckling.model.core :as m]) 

(def model (get system c/ukey)) 

(def classif (m/get-model model)) 

;; 2017-11-25

(require '[clj-duckling.engine.edn :as rul])

(def engine (get system rul/ukey)) 

(count @(:rules engine)) 


(first @(:rules engine)) 

(first classif) 

(require '[taoensso.nippy :as nippy])

(def frozen-classif (nippy/freeze classif)) 

(def rules @(:rules engine)) 
(def model (get system c/ukey)) 

(def classif (m/get-model model)) 


(def logger @(:logger model)) 

(require '[clj-duckling.engine :as engine]) 

(def s "informatii despre comanda 123456789")  

(let [stash1 (engine/pass-all s rules nil)
      stash2 (map #(if (map? %1) (assoc %1 :index %2) %1)
                 stash1
                 (iterate inc 0))
      winners (->> stash
                   (filter :pos)
                   (select-winners
                    #(compare-tokens %1 %2 classifiers dim-label)
                    #(learn/route-prob % classifiers)
                    #(engine/resolve-token % context module)))
      ]
      winners) 

;; 2017-11-28


(require '[clj-duckling.tool.core :as core])  
(require '[clj-duckling.tool.duckling :as tl])  

(def tool (get system tl/ukey)) 
(def model (:model tool)) 
(def rules (:rules tool)) 
(def logger @(:logger tool)) 


(require '[clj-duckling.util.engine :as engine]) 

;; 2017-11-29


(require '[clj-duckling.util.learn :as learn]) 

(let [stash1 (engine/pass-all s rules nil)
      stash2 (map #(if (map? %1) (assoc %1 :index %2) %1)
                  stash1
                  (iterate inc 0))
      winners (->> stash
                   (filter :pos)
                   (select-winners
                    #(compare-tokens %1 %2 model dim-label)
                    #(learn/route-prob % model)
                    #(engine/resolve-token % context module)))
      ]
  winners)



(def s "informatii despre ultima comanda") 
(engine/pass-all s rules logger) 

(require 
'[clj-duckling.engine.core :as eng]
'[clj-duckling.model.core :as modl]
 ) 


(tl/analyze s [{:dim "order" :label "order"}] {} (modl/get-model model) (eng/get-rules rules) logger)  


(tl/analyze s {} {} (modl/get-model model) (eng/get-rules rules) logger)  


(tl/analyze "Vreau un cadou sub 300 lei pentru un baiat 5 ani" [] {} (modl/get-model model) (eng/get-rules rules) logger)  

(def targets [{:dim "gender" :label "sex"}
              {:dim "duration" :label "age"}
              {:dim "budget" :label "budget"}]) 

(fipp (:winners (tl/analyze "Vreau un cadou sub 300 lei pentru un baiat 5 ani" targets {} (modl/get-model model) (eng/get-rules rules) logger))) 


({:index 1,
  :dim :gender,
  :rule {:name "male",
         :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                           "0x49e35d09"
                           "clj_duckling.engine.edn$pattern_fn$fn__28402@49e35d09"]),
         :production #object[clj_duckling.dims.time.prod$eval34597$fn__34598
                             "0x1c6c126e"
                             "clj_duckling.dims.time.prod$eval34597$fn__34598@1c6c126e"]},
  :value :male,
  :start 37,
  :pos 37,
  :route [{:pos 37, :end 42, :text "baiat", :groups [nil]}],
  :label "sex",
  :end 42,
  :body "baiat",
  :text "baiat"}
 {:index 49,
  :dim :duration,
  :rule {:name "<integer> <unit-of-duration>",
         :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x270adc73"
                           "clj_duckling.engine.edn$pattern_fn$fn__28408@270adc73"]
                   #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x2e5089ab"
                           
"clj_duckling.engine.edn$pattern_fn$fn__28408@2e5089ab"]),
         :production #object[clj_duckling.dims.time.prod$eval35576$fn__35577
                             "0x1b0b9482"
                             "clj_duckling.dims.time.prod$eval35576$fn__35577@1b0b9482"]},
  :value {:year 5},
  :start 43,
  :pos 43,
  :route [{:dim :number,
           :integer true,
           :value 5,
           :text "5",
           :pos 43,
           :end 44,
           :rule {:name "integer (numeric)",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                    "0x45324ed3"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28402@45324ed3"]),
                  :production #object[clj_duckling.dims.time.prod$eval35358$fn__35359
                                      "0x511a5520"
                                      "clj_duckling.dims.time.prod$eval35358$fn__35359@511a5520"]},
           :route [{:pos 43, :end 44, :text "5", :groups ["5"]}]}
          
{:dim :unit-of-duration,
           :grain :year,
           :text "ani",
           :pos 45,
           :end 48,
           :rule {:name "ani (unit-of-duration)",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                    "0x21b6f91d"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28402@21b6f91d"]),
                  :production #object[clj_duckling.dims.time.prod$eval35552$fn__35553
                                      "0x502e501c"
                                      "clj_duckling.dims.time.prod$eval35552$fn__35553@502e501c"]},
           :route [{:pos 45, :end 48, :text "ani", :groups ["i"]}]}],
  :label "age",
  :end 48,
  :body "5 ani",
  :text "5 ani"}
 {:index 50,
  :dim :duration,
  :rule {:name "<integer> <unit-of-duration>",
         :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x270adc73"
                           "clj_duckling.engine.edn$pattern_fn$fn__28408@270adc73"
]
                   #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x2e5089ab"
                           "clj_duckling.engine.edn$pattern_fn$fn__28408@2e5089ab"]),
         :production #object[clj_duckling.dims.time.prod$eval35576$fn__35577
                             "0x1b0b9482"
                             "clj_duckling.dims.time.prod$eval35576$fn__35577@1b0b9482"]},
  :value {:year 5},
  :start 43,
  :pos 43,
  :route [{:dim :number,
           :integer true,
           :value 5,
           :text "5",
           :pos 43,
           :end 44,
           :rule {:name "integer (numeric)",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                    "0x6ac21324"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28402@6ac21324"]),
                  :production #object[clj_duckling.dims.time.prod$eval35374$fn__35375
                                      "0x1c312943"
                                      
"clj_duckling.dims.time.prod$eval35374$fn__35375@1c312943"]},
           :route [{:pos 43, :end 44, :text "5", :groups ["5"]}]}
          {:dim :unit-of-duration,
           :grain :year,
           :text "ani",
           :pos 45,
           :end 48,
           :rule {:name "ani (unit-of-duration)",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                    "0x21b6f91d"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28402@21b6f91d"]),
                  :production #object[clj_duckling.dims.time.prod$eval35552$fn__35553
                                      "0x502e501c"
                                      "clj_duckling.dims.time.prod$eval35552$fn__35553@502e501c"]},
           :route [{:pos 45, :end 48, :text "ani", :groups ["i"]}]}],
  :label "age",
  :end 48,
  :body "5 ani",
  :text "5 ani"}
 {:index 51,
  :unit "RON",
  :dim :budget,
  :rule {:name "max budget",
         :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402

                           "0x59d161d1"
                           "clj_duckling.engine.edn$pattern_fn$fn__28402@59d161d1"]
                   #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x145166e3"
                           "clj_duckling.engine.edn$pattern_fn$fn__28408@145166e3"]),
         :production #object[clj_duckling.dims.time.prod$eval35636$fn__35637
                             "0x41b7ed23"
                             "clj_duckling.dims.time.prod$eval35636$fn__35637@41b7ed23"]},
  :value 300,
  :start 15,
  :pos 15,
  :route [{:pos 15, :end 18, :text "sub", :groups [nil]}
          {:dim :amount-of-money,
           :value 300,
           :unit "RON",
           :fields {nil nil},
           :text "300 lei",
           :pos 19,
           :end 26,
           :rule {:name "<amount> <unit>",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28408
                                    "0x132932f2"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28408@132932f2"
]
                            #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                                    "0x18624170"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28408@18624170"]),
                  :production #object[clj_duckling.dims.time.prod$eval35486$fn__35487
                                      "0x673304bb"
                                      "clj_duckling.dims.time.prod$eval35486$fn__35487@673304bb"]},
           :route [{:dim :number,
                    :integer true,
                    :value 300,
                    :text "300",
                    :pos 19,
                    :end 22,
                    :rule {:name "integer (numeric)",
                           :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                             "0x45324ed3"
                                             "clj_duckling.engine.edn$pattern_fn$fn__28402@45324ed3"]),
                           :production #object[clj_duckling.dims.time.prod$eval35358$fn__35359

                                               "0x511a5520"
                                               "clj_duckling.dims.time.prod$eval35358$fn__35359@511a5520"]},
                    :route [{:pos 19,
                             :end 22,
                             :text "300",
                             :groups ["300"]}]}
                   {:dim :unit,
                    :unit "RON",
                    :text "lei",
                    :pos 23,
                    :end 26,
                    :rule {:name "RON",
                           :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                             "0xf0bb136"
                                             "clj_duckling.engine.edn$pattern_fn$fn__28402@f0bb136"]),
                           :production #object[clj_duckling.dims.time.prod$eval35452$fn__35453
                                               "0x69d4b5f4"
                                               "clj_duckling.dims.time.prod$eval35452$fn__35453@69d4b5f4"
]},
                    :route [{:pos 23,
                             :end 26,
                             :text "lei",
                             :groups ["lei"]}]}]}],
  :level :max,
  :label "budget",
  :end 26,
  :body "sub 300 lei",
  :text "sub 300 lei"}
 {:index 52,
  :unit "RON",
  :dim :budget,
  :rule {:name "max budget",
         :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                           "0x59d161d1"
                           "clj_duckling.engine.edn$pattern_fn$fn__28402@59d161d1"]
                   #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                           "0x145166e3"
                           "clj_duckling.engine.edn$pattern_fn$fn__28408@145166e3"]),
         :production #object[clj_duckling.dims.time.prod$eval35636$fn__35637
                             "0x41b7ed23"
                             "clj_duckling.dims.time.prod$eval35636$fn__35637@41b7ed23"]},
  :value 300,
  :start 15,
  :pos 15,
  :route [{:pos 15, :end 18, :text "sub", :groups
 [nil]}
          {:dim :amount-of-money,
           :value 300,
           :unit "RON",
           :fields {nil nil},
           :text "300 lei",
           :pos 19,
           :end 26,
           :rule {:name "<amount> <unit>",
                  :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28408
                                    "0x132932f2"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28408@132932f2"]
                            #object[clj_duckling.engine.edn$pattern_fn$fn__28408
                                    "0x18624170"
                                    "clj_duckling.engine.edn$pattern_fn$fn__28408@18624170"]),
                  :production #object[clj_duckling.dims.time.prod$eval35486$fn__35487
                                      "0x673304bb"
                                      "clj_duckling.dims.time.prod$eval35486$fn__35487@673304bb"]},
           :route [{:dim :number,
                    :integer true,
                    :value 300,
                    
:text "300",
                    :pos 19,
                    :end 22,
                    :rule {:name "integer (numeric)",
                           :pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                             "0x6ac21324"
                                             "clj_duckling.engine.edn$pattern_fn$fn__28402@6ac21324"]),
                           :production #object[clj_duckling.dims.time.prod$eval35374$fn__35375
                                               "0x1c312943"
                                               "clj_duckling.dims.time.prod$eval35374$fn__35375@1c312943"]},
                    :route [{:pos 19,
                             :end 22,
                             :text "300",
                             :groups ["300"]}]}
                   {:dim :unit,
                    :unit "RON",
                    :text "lei",
                    :pos 23,
                    :end 26,
                    :rule {:name "RON",
                           
:pattern (#object[clj_duckling.engine.edn$pattern_fn$fn__28402
                                             "0xf0bb136"
                                             "clj_duckling.engine.edn$pattern_fn$fn__28402@f0bb136"]),
                           :production #object[clj_duckling.dims.time.prod$eval35452$fn__35453
                                               "0x69d4b5f4"
                                               "clj_duckling.dims.time.prod$eval35452$fn__35453@69d4b5f4"]},
                    :route [{:pos 23,
                             :end 26,
                             :text "lei",
                             :groups ["lei"]}]}]}],
  :level :max,
  :label "budget",
  :end 26,
  :body "sub 300 lei",
  :text "sub 300 lei"})



(fipp
 (mapv #(select-keys % [:index :dim :value :start :end :label :body])
       (:winners (tl/analyze "Vreau un cadou sub 300 lei pentru un baiat 5 ani" targets {} (modl/get-model model) (eng/get-rules rules) logger)))) 


[{:index 1,
  :dim :gender,
  :value :male,
  :start 37,
  :end 42,
  :label "sex",
  :body "baiat"}
 {:index 49,
  :dim :duration,
  :value {:year 5},
  :start 43,
  :end 48,
  :label "age",
  :body "5 ani"}
 {:index 50,
  :dim :duration,
  :value {:year 5},
  :start 43,
  :end 48,
  :label "age",
  :body "5 ani"}
 {:index 51,
  :dim :budget,
  :value 300,
  :start 15,
  :end 26,
  :label "budget",
  :body "sub 300 lei"}
 {:index 52,
  :dim :budget,
  :value 300,
  :start 15,
  :end 26,
  :label "budget",
  :body "sub 300 lei"}]
