(ns crela.core
  (:require [crela.crawl-node [definition :as d :refer [destruct]]]
            [crela.node-form [parser :refer [parse]]]
            [crela.url :as url]))

;; def-crawl-node adds methods to crawl-node to dispatch on record type
;; .......................................................................................

(defmulti crawl-node eval)

;; .......................................................................................

(defmacro def-crawl-node
  "Creates a Record for the node name provided and methods to crawl it."
  [node-name & forms]
  (let [node-definition (apply destruct node-name forms)
        attr-names (d/get-symbols node-definition)]
    `(do
      (defrecord ~node-name [~@attr-names])
      (remove-method crela.core/crawl-node ~node-name)
      (defmethod crela.core/crawl-node ~node-name [_#] ~node-definition)
      nil)))

;; The main dispatch for grabbing data
;; .......................................................................................

(defmulti scrape
  (fn [crawl-node-name to-crawl & opts]
    (class to-crawl)))

(defmethod scrape
  String
  [crawl-node-name url & opts]
  (let [html (apply url/fetch-html url opts)]
    (assoc
      (apply scrape crawl-node-name html opts)
      :url url)))

(defmethod scrape
  java.net.URL
  [crawl-node-name url & opts]
  (apply scrape crawl-node-name (.toString url) opts))

(defmethod scrape
  :default
  [crawl-node-name html & opts]
  (parse
    (crawl-node crawl-node-name)
    html))
