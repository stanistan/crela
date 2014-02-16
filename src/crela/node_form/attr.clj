(ns crela.node-form.attr
  (:use crela.utils
        [crela.node-form utils interface])
  (:require [clojure.string :as string]))

(defmulti attr-name (fn [type name] type))
(defmethod attr-name :with-nodes [_ n]
  (pluralize-name n))
(defmethod attr-name :default [_ n]
  (symbol->keyword n))

(defrecord NodeFormAttr [type name selector fs]
  INodeForm
  (get-attr-names [form-node]
    [(attr-name (:type form-node)
                (:name form-node))]))

(defn mapped-names
  [iform & fs]
  (let [f (partial fns-reducer fs)]
    (->> iform
         get-attr-names
         (map f))))
