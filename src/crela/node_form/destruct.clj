(ns crela.node-form.destruct
  (:use [crela.node-form utils attr interface]
        crela.utils))

(defmulti attr-name
  (fn [type name]
    type))

(defmethod attr-name
  :node
  [_ n]
  (pluralize-name n))

(defmethod attr-name
  :default
  [_ n]
  n)

(defn destruct-form-body
  [type [name opts]]
  (let [[maybe-selector & fs :as opts] (ensure-vector (if (= '_ opts) nil opts))
        fs (map eval (or (if (selector? maybe-selector) fs opts) []))
        [name _ name-alias] (ensure-vector name)]
    (->NodeFormAttr
      type
      name
      (ensure-selector maybe-selector)
      fs
      (symbol->keyword (or name-alias (attr-name type name))))))

(def form-types
  {'with-fields :field
   'with-nodes :node
   'merges :merge})

(defmulti destruct-form-type
  (fn [type form-body]
    type))

;; Returns a vec of Record names to merge
(defmethod destruct-form-type
  :merge
  [type form-body]
  (flatten form-body))

;; Returns a vector of NodeFormAttr records
(defmethod destruct-form-type
  :default ; :field|:node
  [type form-body]
  (let [destruct-body (partial destruct-form-body type)]
    (->> form-body (partition 2) (map destruct-body))))

(defn get-form-type
  [form-name]
  (if-let [type (get form-types form-name)]
    type
    (throw (Exception. (str "Invalid Form Type: " form-name)))))

(defn destruct-form
  [form]
  (let [type (get-form-type (first form))] ;; Ensures a valid form
    { type
      (destruct-form-type type (rest form)) }))

(defn destruct-forms
  [forms]
  (reduce merge (map destruct-form forms)))

(defn get-attr-names-as-symbols
  [form]
  (mapped-names form name symbol))
