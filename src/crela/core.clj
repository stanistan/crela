(ns crela.core
  (:require [crela.crawl-node [definition :as d :refer [destruct node-attrs]]]
            [crela.node-form [parser :refer [parse]]
                             [interface :refer [get-attr-names]]]
            [crela.url :as url]))

;; .......................................................................................

(defmulti crawl-node eval) ;; methods are attched in def-crawl-node

(def crawl-node? d/crawl-node-definition?)

(declare realize-crawl-node*)
(defn realize-crawl-node
  "Recursively attaches attributes of merging crawl nodes to the parent."
  [node-name-or-def]
  (realize-crawl-node*
    (if (crawl-node? node-name-or-def)
      node-name-or-def
      (crawl-node node-name-or-def))))

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
  [crawl-node-name ^java.net.URL url & opts]
  (apply scrape crawl-node-name (.toString url) opts))

(declare scrape-with-delay)
(declare opt-bound-scrape)

(defmethod scrape
  :default
  [crawl-node-name html & opts]
  (let [crawl-node-def (realize-crawl-node crawl-node-name)]
    (scrape-with-delay
      crawl-node-def
      (parse crawl-node-def html)
      (opt-bound-scrape opts))))

;; .......................................................................................

; scrape helpers

(defn- opt-bound-scrape
  [opts]
  (fn [node-name url]
    (apply scrape node-name url opts)))

(defn- delay-fn
  [attr & [scrape-fn]]
  (let [scrape-fn (or scrape-fn scrape)]
    (fn [url]
      {:type (:name attr)
       :url url
       :content (delay (scrape-fn (:name attr) url))})))

(defn- delay-urls
  [attr urls & [scrape-fn]]
  (let [delay-fn (delay-fn attr scrape-fn)]
    (if (sequential? urls)
      (map delay-fn urls)
      (delay-fn urls))))

(defn- delay-data
  [attr data & [scrape-fn]]
  (let [data-key (-> attr get-attr-names first)]
    (when-let [urls (get data data-key)]
      [data-key (delay-urls attr urls scrape-fn)])))

(defn- scrape-with-delay
  [crawl-node-def found-data & [scrape-fn]]
  (let [data-fn #(delay-data % found-data scrape-fn)]
    (merge
      found-data
      (->> crawl-node-def node-attrs (map data-fn) (into {})))))

;; .......................................................................................

; crawl node realization helpers

(defn- merging-node-defs
  [crawl-node-def]
  (->> crawl-node-def :merges (map crawl-node)))

(defn- realize-crawl-node*
  [crawl-node-def]
  (loop [merging-node nil
         merging-nodes (merging-node-defs crawl-node-def)
         acc-node (assoc crawl-node-def :merges [])]
    (if-not merging-node
      (if (empty? merging-nodes)
        acc-node
        (recur (first merging-nodes) (rest merging-nodes) acc-node))
      (recur
        (first merging-nodes)
        (concat (merging-node-defs merging-node) (rest merging-nodes))
        (update-in acc-node [:attrs] concat (:attrs merging-node))))))
