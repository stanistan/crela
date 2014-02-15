(ns crela.attrs
  (:require [clojure.string :as string]))

(def valid-form-types
  [:with-nodes
   :with-fields])

(defprotocol ICrawlNode
  (get-attr-names [form] "Gets symbol names from a form as a list"))

(defrecord CrawlNode [type attrs])
(defrecord CrawlNodeAttr [type name selector fs])

(declare get-attr-name) ;; multimethod below

(extend-protocol ICrawlNode

  CrawlNodeAttr
  (get-attr-names [form-node]
    [(get-attr-name (:type form-node) (:name form-node))])

  clojure.lang.ISeq
  (get-attr-names [nodes]
    (mapcat get-attr-names nodes))

  clojure.lang.PersistentVector
  (get-attr-names [nodes]
    (get-attr-names (seq nodes)))

  CrawlNode
  (get-attr-names [form]
    (get-attr-names (:attrs form))))

(defn get-attr-names-as-symbols
  [iform]
  (map #(-> % name symbol) (get-attr-names iform)))

(defn selector?
  [x]
  (if (or (vector? x) (keyword? x)) true false))

(defn ensure-vector
  [x]
  (cond
    (vector? x) x
    (nil? x) x
    :else [x]))

(defn destruct-form-body
  [type [name opts]]
  (let [opts (if (= '_ opts) nil opts)
        [maybe-selector & fs :as opts] (ensure-vector opts)]
    (->CrawlNodeAttr
      type
      name
      (when (selector? maybe-selector) (ensure-vector maybe-selector))
      (map eval (or (if (selector? maybe-selector) fs opts) [])))))

(defn destruct-form
  [form]
  (let [type (-> form first name keyword)
        destruct-body (partial destruct-form-body type)
        attrs (->> form rest (partition 2) (map destruct-body))]
    (->CrawlNode type attrs)))

(defn destruct-forms
  [forms]
  (map destruct-form forms))

(defmulti get-attr-name (fn [type name] type))
(defmethod get-attr-name :with-nodes [_ n]
  (-> n name string/lower-case (str "s") keyword))
(defmethod get-attr-name :default [_ n]
  (-> n name keyword))
