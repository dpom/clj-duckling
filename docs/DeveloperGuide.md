---

---

Probabilistic parsing
=====================

Learn
-----

### Build dataset

The goal of this phase is to build a proper Machine Learning dataset from a corpus of sentences with expected result (interval of time). There is one separate ML model per rule: each rule is seen as a boolean classifier who has to decide, given a 'route' of tokens filling its slots, the probability that it's a good idea for the rule to fire.

The global dataset is a map ='rule name' `> dataset` for the rule

The local dataset for a rule is a vector of samples. A general sample is just a vector `[features output]`

features  
could be anything "seen" by the rule (more on that later). For the moment, it's just the text filling the slots of the rule.

output  
is a boolean, `true` if the rule has matched "successfully" (ie, leading to the right result at the top of the parse tree) for this sample).

### Train model

Once the dataset has been built, different ML techniques can try to build the best ML model for it. It should be clearly separated.

Run
---

At runtime, the model predicts the local probability of each token by running the model of the rule who built the token on its *route*. The global probability of the token is the product of its local prob by the local probs of the tokens of its route.

Features
--------

Feature extraction is used both during learning and runtime. It must be done by a separate function. The same dataset building strategy can be tried with different features approaches.

Architecture
============

core
----

The main module with public API. The entry points are:

load!  
(Re)loads rules and classifiers for languages or/and config.

parse  
Parses text using given module.

Global status variables:

corpus-map  
store the corpus map

``` clojure
{:ro$core {:context {:reference-time {:start #object[org.joda.time.DateTime 0x398ced02 "2013-02-12T04:30:00.000-02:00"], :grain :second},
                     :min {:start #object[org.joda.time.DateTime 0x1cb29052 "1900-01-01T00:00:00.000-02:00"], :grain :year},
                     :max {:start #object[org.joda.time.DateTime 0x2bce42dd "2100-01-01T00:00:00.000-02:00"], :grain :year}},
           :tests ({:text ["10 lei" "10 ron" "10 RON"],
                    :checks [#object[duckling.corpus$money$fn__3878 0x11fa9e27 "duckling.corpus$money$fn__3878@11fa9e27"]]}
                   {:text ["50 bani" "50 BANI"],
                    :checks [#object[duckling.corpus$money$fn__3878 0x19c2bc4a "duckling.corpus$money$fn__3878@19c2bc4a"]]}
                   )}}
```

rules-map  
store the rules map

``` clojure
{:ro$core ({:name "intersect (X cents)",
            :pattern (#object[duckling.engine$pattern_fn$fn__4364 0x802558f "duckling.engine$pattern_fn$fn__4364@802558f"]
                      #object[duckling.engine$pattern_fn$fn__4364 0x6c6d587e "duckling.engine$pattern_fn$fn__4364@6c6d587e"]),
            :production #object[duckling.time.prod$eval6563$fn__6564 0x5fb4773e "duckling.time.prod$eval6563$fn__6564@5fb4773e"]}
           {:name "intersect (and X cents)",
            :pattern (#object[duckling.engine$pattern_fn$fn__4364 0x7e7e5309 "duckling.engine$pattern_fn$fn__4364@7e7e5309"]
                      #object[duckling.engine$pattern_fn$fn__4358 0x79e77776 "duckling.engine$pattern_fn$fn__4358@79e77776"]
                      #object[duckling.engine$pattern_fn$fn__4364 0x52388605 "duckling.engine$pattern_fn$fn__4364@52388605"]),
            :production #object[duckling.time.prod$eval6578$fn__6579 0x476824ec "duckling.time.prod$eval6578$fn__6579@476824ec"]})}
```

classifiers-map  
store the classifiers map

storage
-------

The persistent layer module. It stores the application's entities:

corpus  

rules  

classifiers  

### memory

The storage implementation in memory (atoms).

### resource

Utility functions for resource folder management.

### corpus

learn
-----

analyze
-------

dims
----

Dimensions specific functions.

If you create a new dimension you should add a dimension file here implementing the specific export-value method.

Extending Duckling's Coverage
=============================

Workflow:

1.  Load Duckling
2.  Add tests to the corpus
3.  Run the corpus: the new tests don’t pass
4.  Add or modify rules until the corpus tests pass

Loading Modules
---------------

Each module has a name (en$core), with which it is referred to when you want to use it at runtime, or reload it.

Each module refers to a set of corpus files and rules files (more on this in the following sections).

Each module is run by Duckling in a separate "sandbox", so for example, rules in module A cannot expect to match tokens created by rules in module B. There’s typically one module per language, but nothing prevents you to use several modules for a given language, as long as these modules don’t need to interact with each other.

Loading module:

``` clojure
(load!)
(load! {:languages ["ro" "en"]})
(load! {:config {:en$numbers {:corpus ["numbers"] :rules ["numbers"]}}})
```

Corpus
------

Corpus files are located in `resources/languages/<lang>/corpus`. You can either edit existing files or create new files. **Once you’ve modified corpus files, you must reload to take the changes into account**.

Here is an example corpus file with two test groups:

``` clojure
(
  {} ; Context map

  "0"
  "naught"
  "zero"
  (number 0)

  "1"
  "one"
  (number 1)
)
```

Each test group is described by one or more strings and a function. To run the group Duckling will take each string one by one, analyze it, a call the function on the output. The test passes if the function returns true (or a truthy value).

For instance, to test that "0", "naught" and "zero" will all produce the output {:dim :number :value 0}, we can use:

``` clojure
"0"
"naught"
"zero"
(fn [token context] (and (= :number (:dim token)) (= 0 (:value token))))
```

For now, the context is just used for date and times, in order to solve relative dates like "tomorrow". You can provide a context map at the beginning of your corpus file, and this map will be provided to the test function. In most cases, you shouldn’t need to use context.

In practice, we use helpers to generate easy to read test functions. In the previous example, we use a helper number defined in `src/duckling/corpus.clj`:

``` clojure
(defn number
  "check if the token is a number equal to value.
  If value is integer, it also checks :integer true"
  [value]
  (fn [_ token] (when-not
                  (and
                    (= :number (:dim token))
                    (or (not (integer? value)) (:integer token))
                    (= (:value token) value))
                  [value (:value token)])))
```

So that the test becomes just (number 0), which is easy to read and reusable.

Duckling will frequently generate several possible results for a given input. In this case, each result is tested by the test function. If the function returns true for at least one result, then the test passes.

Once you’ve added your tests, reload your module (see above) and run the corpus:

``` clojure
clj-duckling.core=> (run :en$core)
O0 FAIL "nil"
    Expected null
:en$core: 356 examples, 1 failed.
#'clj-duckling.core/c
```

Make sure the tests don’t pass anymore (if they do, either you’re very lucky and the existing rules actually cover your new tests, or you did not reload the corpus – usually it’s the latter!). Now you’re ready to write rules.

Rules
-----

Rules files are located in `resources/languages/<lang>/rules`. You can either edit existing files or create new files. Once you’ve modified rules files, you must reload to take the changes into account.

Here is an example file with just one rule:

``` clojure
("zero"                                ; _label_ of the rule, useful for debugging
 #"0|zero|naught"                      ; _pattern_, here it’s a simple regex
 {:dim number :integer true :value 0})   ; _production_ token, it can be any map
```

When the pattern is matched, the production token is produced. Duckling adds this new token to its collection of tokens, which is called the "stash". Then other rules can try to match this token and produce other tokens that are added to the stash, and so on. All rules are tried again and again until no more token is produced.

Here is an illustration of this process, with a stash containing 11 tokens:

``` clojure
clj-duckling.core=> (play :en$core "in two hours")
W ------------  11 | time      | in/after <duration>       | P = -3.4187 |  + <integer> <unit-o
W    ---        10 | volume    | number as volume          | P = -2.1172 | integer (0..19)
W    ---         9 | distance  | number as distance        | P = -2.2680 | integer (0..19)
W    ---         8 | temperature | number as temp            | P = -2.2409 | integer (0..19)
W    ---------   7 | duration  | <integer> <unit-of-duration> | P = -2.9592 | integer (0..19) + ho
     ---         6 | null      | number (as relative minutes) | P = -1.6507 | integer (0..19)
     ---         5 | time      | time-of-day (latent)      | P = -1.6351 | integer (0..19)
     ---         4 | time      | year (latent)             | P = -1.0804 | integer (0..19)
         -----   3 | unit-of-duration | hour (unit-of-duration)   | P = 0.0000 |
         -----   2 | cycle     | hour (cycle)              | P = 0.0000 |
W    ---         1 | number    | integer (0..19)           | P = -0.1866 |
  in two hours

```

### Patterns

1.  Base Patterns

    There are two types of base patterns:

    -   regular expressions that try to match the input text
    -   functions that try to match tokens in the stash

    Any function accepting one token as argument (a Clojure map) can work as a pattern. It must return true when the token matches. For example:

    ``` clojure
    ; this pattern will match a token with :dim :number whose :val is 0
    (fn [token] (and (= :number (:dim token)) (= 0 (:value token))))
    ```

    Protip: These patterns are very close, but should not be confused with Corpus test patterns. We might merge them later.

2.  Helpers

    Like for corpus test functions, you’ll find yourself using the same patterns again and again. We use helpers that produce pattern functions. For instance:

    ``` clojure
    (number 3) ; => (fn [token] (and (= :number (:dim token)) (= 3 (:value token))))

    (dim :number) ; => (fn [token] (= :number (:dim token)))
    ```

    ou should reuse existing helpers or define your own as much as possible, as it makes the rules much easier to read.

    Protip: Using `(dim :number)` is better than a regex like =\#"+"=, because if will match any number even "twenty", "minus six", "2M", etc. You actually leverage other Duckling rules that are just responsible to recognize numbers.

3.  Slots

    Let’s say you want to parse something like "10 degrees", "twenty degrees", and "30°". The right approach is to look for a token of :dim :number, immediately followed by a word like "degrees" or "°". In this case, we say the pattern has two slots. It is written like this:

    ``` clojure
    [(dim :number)   ; first slot is a token with :dim :number
     #"degrees?|°"]  ; second slot is the string "degree", "degrees" or "°" in the input string
    ```

### Production

Once a rule’s pattern matches, Duckling creates a token and adds it to the stash.

In its simplest form, the production is just the token to produce:

``` clojure
{:dim :number
 :integer true
 :value 0}
```

But what if the product token is a function of a token matched by the pattern? You can use %1, %2, … %S to represent the tokens matched in the S slots:

``` clojure
"<n> degrees"                ; label
[(dim :number)e #"degreees?"]  ; pattern (2 slots)
{:dim :temperature           ; production
 :degrees (:value %1)}
```

**Protip**: Internally, the production form is expanded with \#(...). It becomes a function, which is called with the matching tokens as arguments.

**Warning**: If the pattern has S slots, you MUST use %S (even if you don’t need it) if you need any %i. That will set the right arity to the production function.

1.  Special case of regex patterns

    If the base pattern is a regex and you need to use the groups matched by the regex in the production, you use the `:groups` key:

    ``` clojure
    "international phone numbeer"
    #"\+(\d+) (\de+)" ; regex capturing two groups
    {:dim :phone-number
     :country-code (-> %1 :groups first)
     :number (-> %1 :groups second)}
    ```

Debugging
---------

When a corpus test doesn’t pass and you don’t understand why, you can have a closer look at what happens with play:

``` clojure
clj-duckling.core=> (play :en$core "45 degrees")
W ----------   7 | temperature | <latent temp> degrees     | P = -1.9331 | number as temp +
W --           6 | volume    | number as volume          | P = -1.8094 | integer (numeric)
W --           5 | distance  | number as distance        | P = -1.6120 | integer (numeric)
  --           4 | temperature | number as temp            | P = -1.9331 | integer (numeric)
  --           3 | null      | number (as relative minutes) | P = -0.9374 | integer (numeric)
W --           2 | time      | year (latent)             | P = -1.0603 | integer (numeric)
W --           1 | number    | integer (numeric)         | P = -0.1665 |
  45 degrees

5 winners:
```

Each line represents a token in the stash. The input string is at the bottom.

Columns:

1.  W indicates a winner token
2.  The -- represent the span in the text input
3.  Token index (starting at 1, since the input string itself is token 0)
4.  :dim
5.  Label of the rule that produced the token (that’s why labeling your rules clearly is important)
6.  Probability (the higher the most probable – and it’s actually the log of the probabily, hence the negative value)
7.  Labels of the rules that produced the tokens in the slots below

If you need more information about a specific token, call the details function with the token index:

``` clojure
clj-duckling.core=> (details 7)
<latent temp> degrees (-1.9331200116060705)
|-- number as temp (-1.9331200116060705)
|   `-- integer (numeric) (-0.16649651564955764)
|       `-- text: 45 (0)
`-- text: degrees (0)
nil
```

If you really need to examine token 7 in depth, you can get the full map with `(token 7)`.
