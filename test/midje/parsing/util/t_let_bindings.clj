(ns midje.parsing.util.t-let-bindings
  (:use [midje.parsing.util.let-bindings])
  (:use [midje.sweet]))

(fact "let?"
  (let? '(clojure.core/let [a 1])) => true)

(fact "keeps the bindigs"
  (checkables-to-facts-in-let-bindings
    '[a 1 => 1
      b 2 =not=> 1]) => (just ['a 1
                               '_ (just 'midje.sweet/fact string? 'a '=> 1)
                               'b 2
                               '_ (just 'midje.sweet/fact string? 'b '=not=> 1)]))

(def checker-call-count (atom 0))
(def value-call-count (atom 0))

(defn plus [x y] (swap! value-call-count inc) (+ x y))
(defn is [x] (swap! checker-call-count inc) x)

(fact "let-bindings work integrated with fact"
  (reset! checker-call-count 0)
  (reset! value-call-count 0)
  (let [a (plus 1 1) =not=> (is 3)
        b (plus a 1) => (is 3)]
    a => 2
    b => 3)

  (fact "checker is called twice"
    @checker-call-count => 2)

  (fact "binding value is evalued once per binding"
    @value-call-count => 2))
