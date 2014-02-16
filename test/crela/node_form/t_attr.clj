(ns crela.node-form.t-attr
  (:use midje.sweet)
  (:require [crela.node-form.attr :as attr :refer [->NodeFormAttr]]
            [crela.node-form.interface :refer [get-attr-names]]))

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
