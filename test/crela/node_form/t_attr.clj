(ns crela.node-form.t-attr
  (:use midje.sweet)
  (:require [crela.node-form.attr :as attr :refer [->NodeFormAttr]]
            [crela.node-form.interface :refer [get-attr-names]]
            [crela.node-form.parser :refer [parse]]))

(fact "attr-name simple"
  (attr/attr-name :with-nodes 'Foo) => :foos
  (attr/attr-name :foo 'foo) => :foo)

(fact "get-attr-names record"
  (get-attr-names (->NodeFormAttr :with-nodes 'Foo nil [])) => [:foos]
  (get-attr-names (->NodeFormAttr :with-fields 'n nil [])) => [:n])

(fact "get-attr-names lists"
  (get-attr-names
    [(->NodeFormAttr :with-nodes 'Foo nil [])
     (->NodeFormAttr :with-fields 'a nil [])])
  => [:foos :a]

  (get-attr-names
    (list
      (->NodeFormAttr :with-nodes 'Foo nil [])
      (->NodeFormAttr :with-fields 'a nil [])))
  => [:foos :a])

(fact "mapped-attr-names"
  (attr/mapped-names
    [(->NodeFormAttr :with-nodes 'Foo nil [])
     (->NodeFormAttr :with-fields 'a nil [])]
    name symbol)
  => ['foos 'a])

(fact "parse works on an attribute"

  (parse (->NodeFormAttr :with-fields 'a nil []) [])
  => {:a []}

  (parse (->NodeFormAttr :with-fields 'foo nil [first]) [1 2])
  => {:foo 1}

  (parse
    [(->NodeFormAttr :with-fields 'first nil [first])
     (->NodeFormAttr :with-fields 'rest nil [rest])]
    [1 2 3 4])
  => {:first 1 :rest [2 3 4]})
