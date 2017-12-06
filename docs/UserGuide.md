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

The English module will allow you to capture the following dimensions:

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
