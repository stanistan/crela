(ns crela.node-form.utils
  (:use crela.utils))

(defn pluralize-name
  [s]
  (-> s name lower-case (str "s") keyword))

(defn selector?
  [x]
  (if (or (vector? x) (keyword? x)) true false))

(defn ensure-selector
  [maybe-selector]
  (when (selector? maybe-selector)
    (ensure-vector maybe-selector)))
