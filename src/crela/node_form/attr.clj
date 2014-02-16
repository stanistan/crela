(ns crela.node-form.attr
  (:use crela.utils
        [crela.node-form utils interface parser])
  (:require [net.cgrand.enlive-html :as html]))

(defn select-html
  [html selector]
  (if selector (html/select html selector) html))

(defmulti attr-name (fn [type name] type))
(defmethod attr-name :with-nodes [_ n]
  (pluralize-name n))
(defmethod attr-name :default [_ n]
  (symbol->keyword n))

(defrecord NodeFormAttr [type name selector fs]
  INodeForm
  (get-attr-names [form-node]
    [(attr-name (:type form-node)
                (:name form-node))])
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
