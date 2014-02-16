(ns crela.node-form.t-destruct
  (:use midje.sweet)
  (:require [crela.node-form.destruct :as d]
            [crela.node-form.attr :as attr :refer [->NodeFormAttr]]))

(fact "destruct-form with-fields"

  (d/destruct-form
    '(with-fields title _))
  => [(->NodeFormAttr :field 'title nil [])]

  (d/destruct-form
    '(with-fields
      title _
      name :key))
  => [(->NodeFormAttr :field 'title nil [])
      (->NodeFormAttr :field 'name [:key] [])]

  (d/destruct-form
    '(with-fields
      title [:key first second rest]
      name nil
      k [first second]
      another rest))
  => [(->NodeFormAttr :field 'title [:key] [first second rest])
      (->NodeFormAttr :field 'name nil [])
      (->NodeFormAttr :field 'k nil [first second])
      (->NodeFormAttr :field 'another nil [rest])])
