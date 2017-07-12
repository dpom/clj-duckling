---

---

Introduction
============

Duckling is a Clojure library that parses text into structured data:

``` clojure
“the first Tuesday of October” => {:value "2014-10-07T00:00:00.000-07:00"
                                   :grain :day}
```

Duckling is:

Agnostic  
it makes no assumption on the kind of data you want to extract, or the language. You can train it with a combination of examples and rules for any task that takes a string as input and produces a map as output.

Probabilistic  
in the real world, a given input string may produce dozens of potential results. Duckling assigns a probability on each result. It decides which results are more probable by using the corpus of examples given in the configuration. Owing to that (and unlike, say, regular expressions or formal grammars), rules can afford to be extremely loose. It makes them much easier to write, and much more robust to user input in the wild.

Extensible  
it leverages the power of Clojure’s "code is data" philosophy.

If you know NLP, Duckling is "almost" a **Probabilistic Context Free Grammar**. But not exactly! It tries to be more flexible and easier to configure than a formal PCFG. It also tries to learn better from examples.

Getting started
===============

To use Duckling in your project, you just need two functions: load! to load all languages, and parse to parse a string.

``` clojure
(ns myproject.core
  (:require [duckling.core :as p]))

(p/load!) ;; Load all languages

(p/parse :en$core ;; core configuration for English ; see also :fr$core, :es$core, :zh$core
         "wake me up the last Monday of January 2015 at 6am"
         [:time]) ;; We are interested in :time expressions only ; see also :duration, :temperature, etc.

;; => ({:body "last Monday of January 2015 at 6am"
;;  :dim :time
;;  :end 49
;;  :start 15
;;  :value {:grain :hour
;;          :type "value"
;;          :value "2015-01-26T06:00:00.000+02:00"
;;          :values ()}})

```

The English module (`:en$core`) will allow you to capture the following dimensions:

| Type     | :dim              | Examples                            |
|----------|-------------------|-------------------------------------|
| currency | :amount-of-money  | "ten dollars", "$20", "ten dollars" |
|          | :cycle            |                                     |
| distance | :distance         |                                     |
|          | :duration         |                                     |
|          | :email            |                                     |
|          | :leven-product)   |                                     |
|          | :leven-unit       |                                     |
|          | :number           |                                     |
|          | :ordinal          |                                     |
|          | :phone-number     |                                     |
|          | :quantity         |                                     |
|          | :temperature      |                                     |
|          | :time             |                                     |
|          | :timezone         |                                     |
|          | :unit             |                                     |
|          | :unit-of-duration |                                     |
|          | :url              |                                     |
|          | :volume           |                                     |

Extending Duckling
==================

Walkthrough
===========

Load Duckling with all languages:

``` clojure
duckling.core=> (load!)
{:ar$core (:number :ordinal)
 :da$core (:time :cycle :unit-of-duration :number :ordinal :duration :timezone)
 :de$core (:time :number :ordinal :cycle :unit-of-duration :duration :timezone)
 :en$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :leven-unit
           :quantity
           :ordinal
           :phone-number
           :cycle
           :unit-of-duration
           :duration
           :timezone
           :url
           :email
           :leven-product)
 :es$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :url
           :timezone
           :phone-number
           :cycle
           :unit-of-duration
           :duration
           :ordinal
           :email)
 :et$core (:number :ordinal)
 :fr$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :url
           :cycle
           :unit-of-duration
           :duration
           :ordinal
           :phone-number
           :timezone
           :email
           :leven-unit
           :leven-product
           :quantity)
 :ga$core (:unit :number :amount-of-money :time :temperature :distance :volume)
 :he$core (:time :cycle :unit-of-duration :ordinal :number :duration)
 :hr$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :ordinal
           :leven-unit
           :quantity
           :timezone
           :phone-number
           :cycle
           :unit-of-duration
           :duration
           :url
           :email
           :leven-product)
 :id$core (:unit :number :amount-of-money :ordinal)
 :it$core (:time
           :cycle
           :unit-of-duration
           :number
           :temperature
           :ordinal
           :phone-number
           :duration
           :timezone
           :url
           :email)
 :ja$core (:number :temperature :ordinal)
 :ko$core (:unit
           :cycle
           :number
           :unit-of-duration
           :amount-of-money
           :time
           :temperature
           :duration
           :distance
           :volume
           :leven-unit
           :quantity
           :phone-number
           :ordinal
           :timezone
           :url
           :email
           :leven-product)
 :my$core (:number)
 :nb$core (:unit
           :number
           :amount-of-money
           :time
           :ordinal
           :cycle
           :unit-of-duration
           :duration
           :timezone)
 :nl$core (:time
           :cycle
           :number
           :unit-of-duration
           :duration
           :distance
           :volume
           :ordinal
           :timezone)
 :pl$core (:time :cycle :unit-of-duration :duration :number :ordinal :timezone)
 :pt$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :leven-unit
           :quantity
           :timezone
           :ordinal
           :unit-of-duration
           :phone-number
           :cycle
           :duration
           :url
           :email)
 :ro$core (:unit
           :number
           :amount-of-money
           :time
           :temperature
           :distance
           :volume
           :url
           :cycle
           :unit-of-duration
           :phone-number
           :timezone
           :email
           :ordinal
           :leven-unit
           :leven-product
           :quantity)
 :ru$core (:number :ordinal)
 :sv$core (:unit
           :number
           :ordinal
           :amount-of-money
           :time
           :unit-of-duration
           :duration
           :cycle
           :timezone)
 :tr$core (:number :ordinal)
 :uk$core (:number :ordinal)
 :vi$core (:unit :number :amount-of-money :time :cycle :ordinal :timezone)
 :zh$core (:time
           :cycle
           :unit-of-duration
           :number
           :temperature
           :duration
           :ordinal)}
```

Run the corpus and check that all the tests pass:

``` clojure
duckling.core=> (run)
:sv$core: 378 examples, 0 failed.
:pt$core: 384 examples, 0 failed.
:ko$core: 407 examples, 0 failed.
:id$core: 80 examples, 0 failed.
:nl$core: 209 examples, 0 failed.
:pl$core: 474 examples, 0 failed.
:tr$core: 99 examples, 0 failed.
:hr$core: 473 examples, 0 failed.
:nb$core: 409 examples, 0 failed.
:my$core: 20 examples, 0 failed.
:ru$core: 84 examples, 0 failed.
:ja$core: 55 examples, 0 failed.
:fr$core: 526 examples, 0 failed.
:es$core: 297 examples, 0 failed.
:da$core: 354 examples, 0 failed.
:zh$core: 329 examples, 0 failed.
:ar$core: 45 examples, 0 failed.
:ga$core: 83 examples, 0 failed.
:it$core: 566 examples, 0 failed.
:de$core: 323 examples, 0 failed.
:ro$core: 251 examples, 0 failed.
:vi$core: 251 examples, 0 failed.
:he$core: 137 examples, 0 failed.
:uk$core: 84 examples, 0 failed.
:et$core: 48 examples, 0 failed.
:en$core: 505 examples, 0 failed.
#'duckling.core/c
```

See the detailed parsing of a given string like “in two hours”:

``` clojure
duckling.core=> (play :en$core "in two hours")
W ------------  11 | time      | in <duration>             | P = -4.0729 |  + <integer> <unit-o
W    ---        10 | volume    | number as volume          | P = -2.2022 | integer (0..19)
W    ---         9 | distance  | number as distance        | P = -2.3138 | integer (0..19)
W    ---------   8 | duration  | <integer> <unit-of-duration> | P = -3.3191 | integer (0..19) + ho
W    ---         7 | temperature | number as temp            | P = -2.2950 | integer (0..19)
     ---         6 | null      | number (as relative minutes) | P = -1.5893 | integer (0..19)
     ---         5 | time      | time-of-day (latent)      | P = -2.1165 | integer (0..19)
     ---         4 | time      | year (latent)             | P = -1.3267 | integer (0..19)
         -----   3 | unit-of-duration | hour (unit-of-duration)   | P = 0.0000 | 
W    ---         2 | number    | integer (0..19)           | P = -0.1411 | 
         -----   1 | cycle     | hour (cycle)              | P = 0.0000 | 
  in two hours

6 winners:
number                    {:type "value", :value 2} {:integer true}
temperature (latent)      {:type "value", :value 2} {}
duration                  {:hour 2, :value 2, :unit :hour, :normalized {:value 7200, :unit "second"}} {}
distance (latent)         {:type "value", :value 2} {}
volume (latent)           {:type "value", :value 2} {}
time                      {:type "value", :value "2013-02-12T06:30:00.000-02:00", :grain :minute, :values ({:type "value", :value "2013-02-12T06:30:00.000-02:00", :grain :minute})} {}
For further info: (details idx) where 1 <= idx <= 11
#'duckling.core/token
duckling.core=> 
```
