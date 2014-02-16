(ns crela.crawl-node.t-definition
  (:use midje.sweet)
  (:require [crela.crawl-node.definition :refer [destruct ->CrawlNodeDefinition]]
            [crela.node-form [interface :refer [get-attr-names]]
                             [parser :refer [parse]]
                             [attr :refer [->NodeFormAttr]]]))

(fact "destruct"

  (destruct 'Foo)
  => (->CrawlNodeDefinition 'Foo [])

  (let [n (destruct 'Foo '(with-fields foo _))]
    (get-attr-names n) => [:foo]
    n)
  => (->CrawlNodeDefinition
      'Foo
      [(->NodeFormAttr :field 'foo nil [])]))

(defrecord Foo [foo c])
(let [data [:a :b :c :d]]

  (fact "parse returns a regular map if there is no record"
    (let [crawl-node (destruct 'Bar '(with-fields foo first c count))]
      (parse crawl-node data) => {:foo :a :c 4}))

  (fact "parse returns a Record instance if the record exists"
    (let [crawl-node (destruct 'Foo '(with-fields foo first c count))]
      (parse crawl-node data) => (->Foo :a 4))))
