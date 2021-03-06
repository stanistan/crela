(ns crela.utils
  (:require [clojure.string :as string]))

(defn ensure-vector
  [x]
  (cond
    (vector? x) x
    (nil? x) x
    :else [x]))

(defn fns-reducer
  [fs initial]
  (reduce #(%2 %1) initial fs))

(defn class-name
  "Takes a symbol that that is a class/record/type and returns its name as a string."
  [cl]
  (->> (str cl) ;; "class namespace.ClassName"
       (re-seq #"[^ ]*")
       (filter (complement empty?)) ;; ["class" "namespace.className"]
       second))

(defn class-constructor
  "Takes a symbol that is a record and creates a static class constructor as a symbol."
  [cl]
  (-> (class-name cl)
      (str "/create")
      symbol))

(defn map->Record$class
  [cl data]
  (let [constructor (class-constructor cl)]
    (eval `(~constructor ~data))))

(defn map->Record$symbol
  "Takes a symbol that is a class/record name and returns an the function map->sym."
  [sym data]
  (let [f (->> sym name (str 'map->) symbol eval)]
    (f data)))

(defmulti create-record
  (fn [of-type with-data]
    (class of-type)))

(defmethod create-record
  clojure.lang.Symbol
  [cl data]
  (try
    (map->Record$symbol cl data)
    (catch Exception e data)))

(defmethod create-record
  java.lang.Class
  [cl data]
  (try
    (map->Record$class cl data)
    (catch Exception e data)))

(defmethod create-record
  nil
  [_ data]
  data)

(def symbol->keyword (comp keyword name))
(def lower-case string/lower-case)

(defn get-keys
  [m ks]
  (map #(get m %) ks))

(defn get-keys&concat
  [m ks]
  (apply concat (get-keys m ks)))
