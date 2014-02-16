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

;; The types we have are :with-nodes, :with-fields (for now)

(defmulti scrape-fs (fn [type name fs] type))
(defmethod scrape-fs :with-nodes [_ name fs]
  (conj fs (partial (create-record name))))
(defmethod scrape-fs :default [_ _ fs]
  fs)
