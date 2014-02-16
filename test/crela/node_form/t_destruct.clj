(ns crela.node-form.t-destruct
  (:use midje.sweet)
  (:require [crela.node-form.destruct :as d]
            [crela.node-form.attr :as attr :refer [->NodeFormAttr]]))

(fact "destruct-form with-fields"

  (d/destruct-form
    '(with-fields title _))
  => [(->NodeFormAttr :with-fields 'title nil [])]

  (d/destruct-form
    '(with-fields
      title _
      name :key))
  => [(->NodeFormAttr :with-fields 'title nil [])
      (->NodeFormAttr :with-fields 'name [:key] [])]

  (d/destruct-form
    '(with-fields
      title [:key first second rest]
      name nil
      k [first second]
      another rest))
  => [(->NodeFormAttr :with-fields 'title [:key] [first second rest])
      (->NodeFormAttr :with-fields 'name nil [])
      (->NodeFormAttr :with-fields 'k nil [first second])
      (->NodeFormAttr :with-fields 'another nil [rest])])
