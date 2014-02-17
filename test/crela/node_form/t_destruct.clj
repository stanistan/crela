(ns crela.node-form.t-destruct
  (:use midje.sweet)
  (:require [crela.node-form.destruct :as d]
            [crela.node-form.attr :as attr :refer [->NodeFormAttr]]))

(fact "destruct-form with-fields"

  (d/destruct-form
    '(with-fields title _))
  => {:field [(->NodeFormAttr :field 'title nil [] :title)] }

  (d/destruct-form
    '(with-fields
      title _
      name :key))
  => {:field [(->NodeFormAttr :field 'title nil [] :title)
              (->NodeFormAttr :field 'name [:key] [] :name)]}

  (d/destruct-form
    '(with-fields
      title [:key first second rest]
      name nil
      k [first second]
      another rest))
  => {:field [(->NodeFormAttr :field 'title [:key] [first second rest] :title)
              (->NodeFormAttr :field 'name nil [] :name)
              (->NodeFormAttr :field 'k nil [first second] :k)
              (->NodeFormAttr :field 'another nil [rest] :another)]})
