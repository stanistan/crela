(ns crela.crawl-node.definition
  (:use [crela.node-form interface destruct parser]
        crela.utils))

(defrecord CrawlNodeDefinition [node-name forms]
  INodeForm
  (get-attr-names [this]
    (get-attr-names forms))
  Parser
  (parse [this html]
    (create-record
      node-name
      (parse forms html))))

(defn destruct->CrawlNodeDefinition
  [node-name & forms]
  (->CrawlNodeDefinition
    node-name
    (destruct-forms forms)))
