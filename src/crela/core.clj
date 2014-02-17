(ns crela.core
  (:require [crela.crawl-node [definition :as d :refer [destruct node-attrs]]]
            [crela.node-form [parser :refer [parse]]
                             [interface :refer [get-attr-names]]]
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

(declare delayed-scrape)
(defmethod scrape
  :default
  [crawl-node-name html & opts]
  (let [crawl-node-def (crawl-node crawl-node-name)
        bound-scrape (fn [node-name url] (apply scrape node-name url opts))
        scraped-data (parse crawl-node-def html)]
    (delayed-scrape bound-scrape crawl-node-def scraped-data)))

(defn- attr-name
  [attr]
  (first (get-attr-names attr)))

(defn delayed-scrape
  [bound-scrape-fn crawl-node-def found-data]
  (merge found-data
         (into {}
               (map
                (fn [attr]
                  (let [k (attr-name attr)
                        urls (get found-data k)
                        node-name (:name attr)]
                    [k (map #(delay (bound-scrape-fn node-name %)) urls)]))
                (node-attrs crawl-node-def)))))
