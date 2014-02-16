(ns crela.t-utils
  (:use midje.sweet)
  (:require [crela.utils :as u]))

(fact "ensure-vector it does the things"

  (u/ensure-vector 1) => [1]
  (u/ensure-vector nil) => nil
  (u/ensure-vector ['a]) => ['a])

(defrecord TestRecord [foo])

(fact "Class constructors work"

  (u/class-constructor TestRecord)
  => 'crela.t_utils.TestRecord/create

  (u/create-record TestRecord {:foo :bar})
  => (->TestRecord :bar)

  (u/create-record TestRecord {:foo :bar})
  => (u/create-record 'TestRecord {:foo :bar}))

(fact "fns-reducer is like a threading macro"
  (u/fns-reducer [] :a) => :a
  (u/fns-reducer [identity] :a) => :a
  (u/fns-reducer [inc inc] 1) => 3
  (u/fns-reducer [name (partial str '->) symbol] 'foo) => '->foo)
