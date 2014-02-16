(ns crela.node-form.t-attr
  (:use midje.sweet)
  (:require [crela.node-form.attr :as attr :refer [->NodeFormAttr]]
            [crela.node-form.interface :refer [get-attr-names]]
            [crela.node-form.parser :refer [parse]]))

(fact "attr-name simple"
  (attr/attr-name :node 'Foo) => :foos
  (attr/attr-name :foo 'foo) => :foo)

(fact "get-attr-names record"
  (get-attr-names (->NodeFormAttr :node 'Foo nil [])) => [:foos]
  (get-attr-names (->NodeFormAttr :field 'n nil [])) => [:n])

(fact "get-attr-names lists"
  (get-attr-names
    [(->NodeFormAttr :node 'Foo nil [])
     (->NodeFormAttr :field 'a nil [])])
  => [:foos :a]

  (get-attr-names
    (list
      (->NodeFormAttr :node 'Foo nil [])
      (->NodeFormAttr :field 'a nil [])))
  => [:foos :a])

(fact "mapped-attr-names"
  (attr/mapped-names
    [(->NodeFormAttr :node 'Foo nil [])
     (->NodeFormAttr :field 'a nil [])]
    name symbol)
  => ['foos 'a])

(fact "parse works on an attribute"

  (parse (->NodeFormAttr :field 'a nil []) [])
  => {:a []}

  (parse (->NodeFormAttr :field 'foo nil [first]) [1 2])
  => {:foo 1}

  (parse
    [(->NodeFormAttr :field 'first nil [first])
     (->NodeFormAttr :field 'rest nil [rest])]
    [1 2 3 4])
  => {:first 1 :rest [2 3 4]})
