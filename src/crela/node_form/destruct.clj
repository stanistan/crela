(ns crela.node-form.destruct
  (:use [crela.node-form utils attr interface]
        crela.utils))

(defn destruct-form-body
  [type [name opts]]
  (let [[maybe-selector & fs :as opts] (ensure-vector (if (= '_ opts) nil opts))
        fs (map eval (or (if (selector? maybe-selector) fs opts) []))]
    (->NodeFormAttr
      type
      name
      (ensure-selector maybe-selector)
      (scrape-fs type name fs))))

(defn destruct-form
  [form]
  (let [type (-> form first name keyword)
        destruct-body (partial destruct-form-body type)]
    (->> form
         rest
         (partition 2)
         (map destruct-body))))

(defn destruct-forms
  [forms]
  (flatten (map destruct-form forms)))

(defn attr-names$symbols
  [form]
  (mapped-names form name symbol))
