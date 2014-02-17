(ns crela.node-form.attr
  (:use crela.utils
        [crela.node-form utils interface parser])
  (:require [net.cgrand.enlive-html :as html]))

(defn select-html
  [html selector]
  (if selector (html/select html selector) html))

(defrecord NodeFormAttr [type name selector fs name-alias]

  INodeForm
  (get-attr-names [form-node] [name-alias])

  Parser
  (parse [this html]
    (let [n (first (get-attr-names this))
          data (fns-reducer fs (select-html html selector))]
      { n data })))

(defn mapped-names
  [iform & fs]
  (let [f (partial fns-reducer fs)]
    (->> iform
         get-attr-names
         (map f))))
