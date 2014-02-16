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

(def form-types
  {'with-fields :field
   'with-nodes :node})

(defn get-form-type
  [form-name]
  (if-let [type (get form-types form-name)]
    type
    (throw (Exception. (str "Invalid Form Type: " form-name)))))

(defn destruct-form
  [form]
  (let [type (get-form-type (first form))
        destruct-body (partial destruct-form-body type)]
    (->> form
         rest
         (partition 2)
         (map destruct-body))))

(defn destruct-forms
  [forms]
  (flatten (map destruct-form forms)))

(defn get-attr-names-as-symbols
  [form]
  (mapped-names form name symbol))
