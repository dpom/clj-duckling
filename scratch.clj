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

