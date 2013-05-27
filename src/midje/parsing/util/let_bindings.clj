(ns ^{:doc "Parsing lets in facts."}
  midje.parsing.util.let-bindings
  (:require [clojure.walk :refer [prewalk]]
            [midje.parsing.util.recognizing :as recognize]))

;;; Processings let bindings in facts

(defn let? [form]
  (and (list? form) (= 'clojure.core/let (first form))))

(defn checkables-to-facts-in-let-bindings
  "Rewrites let-bindings by adding facts for all checkables.
   Form (let [a 1 => 1]) gets rewritten to:
        (let a 1
             _ (fact ... a => 1)])."
  [bindings]
  (reduce
    (fn [form [k v]]
      (if (recognize/all-arrows (str k))
        (conj form '_ `(midje.sweet/fact
                         ~(str (-> form butlast last) " " k " in let-bindings")
                         ~(-> form butlast last) ~k ~v))
        (conj form k v)))
    [] (partition 2 bindings)))

(defn checkables-to-facts-in-lets
  "Rewrites the let-bindings from the form recursively to support
   using checkables in bindings. See bind-facts-to-checkables for details."
  [form]
  (prewalk
    (fn [x]
      (if (let? x)
        `(let ~(checkables-to-facts-in-let-bindings (second x)) ~@(nnext x))
        x))
    form))
