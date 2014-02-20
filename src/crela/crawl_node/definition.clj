(ns crela.crawl-node.definition
  (:use [crela.node-form interface destruct parser]
        crela.utils))

(defrecord CrawlNodeDefinition [node-name attrs merges]
  INodeForm
  (get-attr-names [this]
    (get-attr-names attrs))
  Parser
  (parse [this html]
    (create-record node-name (parse attrs html))))

(defn destruct
  [node-name & forms]
  (let [partitioned (destruct-forms forms)]
    (->CrawlNodeDefinition
      node-name
      (get-keys&concat partitioned [:field :node])
      (or (:merge partitioned) []))))

(defn get-symbols
  [crawl-node-def]
  (get-attr-names-as-symbols crawl-node-def))

(defn node-attr?
  [attr]
  (= :node (:type attr)))

(defn filter-attrs
  [f crawl-node-def]
  (filter f (:attrs crawl-node-def)))

(def node-attrs
  (partial filter-attrs node-attr?))

(def field-attrs
  (partial filter-attrs (complement node-attr?)))

(defn crawl-node-definition?
  [x]
  (instance? CrawlNodeDefinition x))