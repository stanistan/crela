(ns crela.t-core
  (:use midje.sweet)
  (:require [crela.core :refer [def-crawl-node
                                crawl-node
                                scrape
                                crawl-node?
                                realize-crawl-node]]
            [crela.common :refer :all]
            [crela.node-form.interface :refer [get-attr-names]]))


(def-crawl-node Test1
  (with-fields
    field-a _))

(def-crawl-node Test2
  (merges Test1)
  (with-fields
    field-b _))

(def-crawl-node Test3
  (merges Test2)
  (with-fields
    field-c _))

(facts "About crawl nodes"

  (fact "crawl nodes can be constructed"
    (let [crawl-node-def (crawl-node Test1)]
      crawl-node-def => crawl-node?
      (get-attr-names crawl-node-def) => [:field-a]
      (:node-name crawl-node-def) => 'Test1
      (:merges crawl-node-def) => empty?))

  (fact "crawl nodes can merge"
    (let [crawl-node-def (crawl-node Test2)]
      crawl-node-def => crawl-node?
      (get-attr-names crawl-node-def) => [:field-b]
      (:node-name crawl-node-def) => 'Test2
      (:merges crawl-node-def) => ['Test1]))

  (fact "crawl nodes can be realized"
    (realize-crawl-node Test1) => (crawl-node Test1)
    (get-attr-names (realize-crawl-node Test2)) [:field-a :field-b])

  (fact "crawl nodes can be realized recursively"
    (let [crawl-node-def (crawl-node Test3)
          realized (realize-crawl-node Test3)]
      crawl-node-def => crawl-node?
      realized => crawl-node?
      (realize-crawl-node crawl-node-def) => realized
      (get-attr-names crawl-node-def) => [:field-c]
      (into #{} (get-attr-names realized)) => #{:field-a :field-b :field-c})))
